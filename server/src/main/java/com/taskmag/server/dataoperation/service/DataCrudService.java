package com.taskmag.server.dataoperation.service;

import com.taskmag.server.common.exception.BizException;
import com.taskmag.server.common.util.IdGenerator;
import com.taskmag.server.dataoperation.dto.BatchCrudRequestItem;
import com.taskmag.server.dataoperation.dto.CrudModelDto;
import com.taskmag.server.dataoperation.mapper.DynamicCrudMapper;
import com.taskmag.server.dataoperation.meta.FieldMeta;
import com.taskmag.server.dataoperation.meta.TableMeta;
import com.taskmag.server.dataoperation.meta.TableMetaRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataCrudService {
    private final TableMetaRegistry tableMetaRegistry;
    private final DynamicCrudMapper dynamicCrudMapper;

    @Transactional(rollbackFor = Exception.class)
    public boolean batchOperate(List<BatchCrudRequestItem> requestItems) {
        if (CollectionUtils.isEmpty(requestItems)) {
            throw new BizException(4000, "request body is required");
        }
        for (BatchCrudRequestItem item : requestItems) {
            TableMeta meta = tableMetaRegistry.getByCompositeName(item.getTableName());
            CrudModelDto crudModel = item.getCrudModel();
            if (crudModel == null) {
                continue;
            }
            handleAdded(meta, crudModel.getAdded());
            handleChanged(meta, crudModel.getChanged());
            handleDeleted(meta, crudModel.getDeleted());
        }
        return true;
    }

    private void handleAdded(TableMeta meta, List<Map<String, Object>> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        for (Map<String, Object> row : rows) {
            Map<String, Object> data = filterWritableFields(meta, row);
            applyInsertDefaults(meta, data);
            Object idValue = row.get(meta.getRequestPrimaryKey());
            if (idValue == null || String.valueOf(idValue).isBlank()) {
                data.put(meta.getPrimaryKeyColumn(), IdGenerator.uuid());
            } else {
                data.put(meta.getPrimaryKeyColumn(), idValue);
            }
            dynamicCrudMapper.insert(meta.getPhysicalTableName(), data);
        }
    }

    private void handleChanged(TableMeta meta, List<Map<String, Object>> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        for (Map<String, Object> row : rows) {
            Object idValue = row.get(meta.getRequestPrimaryKey());
            if (idValue == null || String.valueOf(idValue).isBlank()) {
                throw new BizException(4004, "primary key is required for update: " + meta.getRequestPrimaryKey());
            }
            Map<String, Object> data = filterWritableFields(meta, row);
            applyUpdateDefaults(meta, data);
            if (data.isEmpty()) {
                continue;
            }
            dynamicCrudMapper.updateById(meta.getPhysicalTableName(), meta.getPrimaryKeyColumn(), idValue, data);
        }
    }

    private void handleDeleted(TableMeta meta, List<Map<String, Object>> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        if (!meta.isAllowDelete()) {
            throw new BizException(4005, "delete not allowed");
        }
        for (Map<String, Object> row : rows) {
            Object idValue = row.get(meta.getRequestPrimaryKey());
            if (idValue == null || String.valueOf(idValue).isBlank()) {
                throw new BizException(4004, "primary key is required for delete: " + meta.getRequestPrimaryKey());
            }
            dynamicCrudMapper.deleteById(meta.getPhysicalTableName(), meta.getPrimaryKeyColumn(), idValue);
        }
    }

    private void applyInsertDefaults(TableMeta meta, Map<String, Object> data) {
        String currentUser = currentUser();
        LocalDateTime now = LocalDateTime.now();
        putIfFieldExists(meta, data, "createuser", currentUser);
        putIfFieldExists(meta, data, "createtime", now);
        putIfFieldExists(meta, data, "description", "");
        putIfFieldExists(meta, data, "lingma_sys_is_delete", 0);
        putIfFieldExists(meta, data, "updateuser", currentUser);
        putIfFieldExists(meta, data, "updatetime", now);
    }

    private void applyUpdateDefaults(TableMeta meta, Map<String, Object> data) {
        String currentUser = currentUser();
        LocalDateTime now = LocalDateTime.now();
        putIfFieldExists(meta, data, "updateuser", currentUser);
        putIfFieldExists(meta, data, "updatetime", now);
    }

    private void putIfFieldExists(TableMeta meta, Map<String, Object> data, String fieldName, Object value) {
        FieldMeta fieldMeta = meta.getField(fieldName);
        if (fieldMeta == null) {
            return;
        }
        data.putIfAbsent(fieldMeta.getColumnName(), value);
    }

    private String currentUser() {
        return "system";
    }

    private Map<String, Object> filterWritableFields(TableMeta meta, Map<String, Object> row) {
        Map<String, Object> data = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            FieldMeta fieldMeta = meta.getField(entry.getKey());
            if (fieldMeta == null || !fieldMeta.isWritable()) {
                continue;
            }
            data.put(fieldMeta.getColumnName(), entry.getValue());
        }
        return data;
    }
}
