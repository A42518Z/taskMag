package com.taskmag.server.dataoperation.meta;

import com.taskmag.server.common.exception.BizException;
import com.taskmag.server.dataoperation.dto.ColumnMetaDto;
import com.taskmag.server.dataoperation.dto.TableEntryDto;
import com.taskmag.server.dataoperation.mapper.SchemaMetaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TableMetaRegistry {
    private final SchemaMetaMapper schemaMetaMapper;
    private final Map<String, TableMeta> registry = new ConcurrentHashMap<>();

    public TableMeta getTableMeta(String dbName, String logicalTableName) {
        ResolvedTable resolved = resolve(dbName, logicalTableName);
        String key = buildKey(resolved.dbName(), resolved.tableName());
        return registry.computeIfAbsent(key, k -> loadTableMeta(resolved.dbName(), resolved.tableName()));
    }

    public TableMeta getByCompositeName(String compositeName) {
        ResolvedTable resolved = resolve(null, compositeName);
        return getTableMeta(resolved.dbName(), resolved.tableName());
    }

    public String resolveDbName(String dbName) {
        if (StringUtils.hasText(dbName)) {
            return sanitizeIdentifier(dbName.trim(), "dbName");
        }
        String currentDb = schemaMetaMapper.currentDatabase();
        if (!StringUtils.hasText(currentDb)) {
            throw new BizException(4001, "current database is empty");
        }
        return sanitizeIdentifier(currentDb.trim(), "dbName");
    }

    public List<TableEntryDto> listTables(String dbName) {
        String resolvedDbName = resolveDbName(dbName);
        return schemaMetaMapper.listTables(resolvedDbName);
    }

    private TableMeta loadTableMeta(String dbName, String logicalTableName) {
        List<ColumnMetaDto> columns = schemaMetaMapper.listColumns(dbName, logicalTableName);
        if (columns == null || columns.isEmpty()) {
            throw new BizException(4001, "table not found in metadata: " + dbName + "@" + logicalTableName);
        }

        Map<String, FieldMeta> fieldMetaMap = new LinkedHashMap<>();
        String requestPrimaryKey = null;
        String primaryKeyColumn = null;

        for (ColumnMetaDto column : columns) {
            String columnName = sanitizeIdentifier(column.getColumnName(), "columnName");
            boolean isPrimaryKey = "PRI".equalsIgnoreCase(column.getColumnKey());
            if (isPrimaryKey && primaryKeyColumn == null) {
                primaryKeyColumn = columnName;
                requestPrimaryKey = columnName;
            }
            fieldMetaMap.put(columnName, FieldMeta.builder()
                    .requestField(columnName)
                    .columnName(columnName)
                    .queryable(true)
                    .writable(!isPrimaryKey)
                    .build());
        }

        if (fieldMetaMap.isEmpty()) {
            throw new BizException(4001, "table has no available fields in metadata: " + dbName + "@" + logicalTableName);
        }

        if (primaryKeyColumn == null) {
            if (fieldMetaMap.containsKey("id")) {
                primaryKeyColumn = "id";
                requestPrimaryKey = "id";
            } else if (fieldMetaMap.containsKey("rowid")) {
                primaryKeyColumn = "rowid";
                requestPrimaryKey = "rowid";
            } else {
                String firstColumn = fieldMetaMap.keySet().iterator().next();
                primaryKeyColumn = firstColumn;
                requestPrimaryKey = firstColumn;
            }
        }

        String physicalDbName = resolvePhysicalDbName();
        return TableMeta.builder()
                .dbName(dbName)
                .logicalTableName(logicalTableName)
                .physicalTableName("`" + physicalDbName + "`.`" + logicalTableName + "`")
                .requestPrimaryKey(requestPrimaryKey)
                .primaryKeyColumn(primaryKeyColumn)
                .allowDelete(true)
                .fieldMetaMap(fieldMetaMap)
                .build();
    }

    private String resolvePhysicalDbName() {
        String currentDb = schemaMetaMapper.currentDatabase();
        if (!StringUtils.hasText(currentDb)) {
            throw new BizException(4001, "current database is empty");
        }
        return sanitizeIdentifier(currentDb.trim(), "physicalDbName");
    }

    private ResolvedTable resolve(String dbName, String logicalTableName) {
        String rawTableName = logicalTableName == null ? "" : logicalTableName.trim();
        String rawDbName = dbName == null ? "" : dbName.trim();

        if (rawTableName.contains("@")) {
            String[] arr = rawTableName.split("@", 2);
            if (!StringUtils.hasText(arr[0]) || !StringUtils.hasText(arr[1])) {
                throw new BizException(4001, "table name invalid: " + rawTableName);
            }
            rawDbName = arr[0].trim();
            rawTableName = arr[1].trim();
        }

        if (!StringUtils.hasText(rawTableName)) {
            throw new BizException(4001, "table name is required");
        }

        String resolvedDbName = resolveDbName(rawDbName);
        String resolvedTableName = sanitizeIdentifier(rawTableName, "tableName");
        return new ResolvedTable(resolvedDbName, resolvedTableName);
    }

    private String buildKey(String dbName, String logicalTableName) {
        return dbName + "@" + logicalTableName;
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

    private record ResolvedTable(String dbName, String tableName) {
    }
}
