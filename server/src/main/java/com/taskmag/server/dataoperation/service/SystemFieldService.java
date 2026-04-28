package com.taskmag.server.dataoperation.service;

import com.taskmag.server.common.exception.BizException;
import com.taskmag.server.common.util.IdGenerator;
import com.taskmag.server.dataoperation.mapper.SchemaMetaMapper;
import com.taskmag.server.dataoperation.mapper.SystemFieldMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemFieldService {
    private final SystemFieldMapper systemFieldMapper;
    private final SchemaMetaMapper schemaMetaMapper;

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> applySystemFields(String compositeTableName) {
        ResolvedTarget target = resolve(compositeTableName);
        Map<String, Object> tableMeta = systemFieldMapper.findTableMeta(target.dbName(), target.tableName());
        if (tableMeta == null || tableMeta.isEmpty()) {
            throw new BizException(4001, "table not found in metadata: " + compositeTableName);
        }

        String tableId = stringValue(tableMeta.get("rowid"));
        String tblname = stringValue(tableMeta.get("tblname"));
        String lingmaSysEnt = stringValue(tableMeta.get("lingmaSysEnt"));
        if (!StringUtils.hasText(tableId) || !StringUtils.hasText(tblname)) {
            throw new BizException(4001, "table metadata invalid: " + compositeTableName);
        }
        if (!StringUtils.hasText(lingmaSysEnt)) {
            lingmaSysEnt = "NewApp";
        }

        Set<String> existingFields = systemFieldMapper.findFieldNames(tableId, tblname).stream()
                .filter(StringUtils::hasText)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        boolean hasPrimaryKey = safeInt(systemFieldMapper.countLogicalPrimaryKeys(tableId, tblname)) > 0
                || safeInt(systemFieldMapper.countPhysicalPrimaryKeys(currentPhysicalDbName(), tblname)) > 0;

        String physicalTableName = buildPhysicalTableName(currentPhysicalDbName(), tblname);
        List<String> addedColumns = new ArrayList<>();
        List<String> skippedColumns = new ArrayList<>();
        Map<String, String> reasons = new LinkedHashMap<>();
        int orderBase = existingFields.size() + 1;

        for (SystemFieldDefinition definition : buildDefinitions()) {
            String fieldName = definition.fieldName();
            if ("id".equals(fieldName) && hasPrimaryKey) {
                skippedColumns.add(fieldName);
                reasons.put(fieldName, "table already has primary key");
                continue;
            }
            if (existingFields.contains(fieldName.toLowerCase(Locale.ROOT))) {
                skippedColumns.add(fieldName);
                reasons.put(fieldName, "field already exists");
                continue;
            }
            systemFieldMapper.addColumn(physicalTableName, definition.columnSql());
            systemFieldMapper.insertFieldMeta(
                    IdGenerator.uuid(),
                    tableId,
                    tblname,
                    fieldName,
                    definition.cnName(),
                    definition.dataType(),
                    definition.dataLen(),
                    definition.isPrimaryKey() ? 1 : 0,
                    String.valueOf(orderBase++),
                    lingmaSysEnt
            );
            addedColumns.add(fieldName);
            existingFields.add(fieldName.toLowerCase(Locale.ROOT));
        }

        systemFieldMapper.markHasDefaultFields(tableId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("TableName", target.dbName() + "@" + tblname);
        result.put("AddedColumns", addedColumns);
        result.put("SkippedColumns", skippedColumns);
        result.put("Reason", reasons);
        return result;
    }

    private String currentPhysicalDbName() {
        String db = schemaMetaMapper.currentDatabase();
        if (!StringUtils.hasText(db)) {
            throw new BizException(4001, "current database is empty");
        }
        return sanitizeIdentifier(db.trim(), "physicalDbName");
    }

    private String buildPhysicalTableName(String dbName, String tableName) {
        return "`" + sanitizeIdentifier(dbName, "dbName") + "`.`" + sanitizeIdentifier(tableName, "tableName") + "`";
    }

    private ResolvedTarget resolve(String compositeTableName) {
        if (!StringUtils.hasText(compositeTableName)) {
            throw new BizException(4000, "TableName is required");
        }
        String raw = compositeTableName.trim();
        String dbName;
        String tableName;
        if (raw.contains("@")) {
            String[] arr = raw.split("@", 2);
            if (!StringUtils.hasText(arr[0]) || !StringUtils.hasText(arr[1])) {
                throw new BizException(4001, "TableName invalid: " + compositeTableName);
            }
            dbName = sanitizeIdentifier(arr[0].trim(), "dbName");
            tableName = sanitizeIdentifier(arr[1].trim(), "tableName");
        } else {
            dbName = currentPhysicalDbName();
            tableName = sanitizeIdentifier(raw, "tableName");
        }
        return new ResolvedTarget(dbName, tableName);
    }

    private String sanitizeIdentifier(String name, String fieldName) {
        if (!StringUtils.hasText(name)) {
            throw new BizException(4001, fieldName + " is required");
        }
        String trimmed = name.trim();
        for (char c : trimmed.toCharArray()) {
            if (!(Character.isLetterOrDigit(c) || c == '_' || c == '$')) {
                throw new BizException(4001, fieldName + " contains illegal characters: " + name);
            }
        }
        return trimmed;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private List<SystemFieldDefinition> buildDefinitions() {
        List<SystemFieldDefinition> list = new ArrayList<>();
        list.add(new SystemFieldDefinition("id", "唯一值（ID）", "字符串型", "32", true, "`id` VARCHAR(32) NULL"));
        list.add(new SystemFieldDefinition("createuser", "创建人", "字符串型", "100", false, "`createuser` VARCHAR(100) NULL"));
        list.add(new SystemFieldDefinition("createtime", "创建时间", "日期型", "", false, "`createtime` DATETIME NULL"));
        list.add(new SystemFieldDefinition("description", "摘要", "文本", "", false, "`description` TEXT NULL"));
        list.add(new SystemFieldDefinition("lingma_sys_is_delete", "是否删除", "整数型", "", false, "`lingma_sys_is_delete` INT NULL DEFAULT 0"));
        list.add(new SystemFieldDefinition("updateuser", "修改人", "字符串型", "100", false, "`updateuser` VARCHAR(100) NULL"));
        list.add(new SystemFieldDefinition("updatetime", "修改时间", "日期型", "", false, "`updatetime` DATETIME NULL"));
        return list;
    }

    private record ResolvedTarget(String dbName, String tableName) {
    }

    private record SystemFieldDefinition(String fieldName, String cnName, String dataType, String dataLen,
                                         boolean isPrimaryKey, String columnSql) {
    }
}
