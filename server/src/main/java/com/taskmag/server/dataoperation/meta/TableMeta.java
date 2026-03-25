package com.taskmag.server.dataoperation.meta;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
public class TableMeta {
    private String dbName;
    private String logicalTableName;
    private String physicalTableName;
    private String requestPrimaryKey;
    private String primaryKeyColumn;
    private boolean allowDelete;
    private Map<String, FieldMeta> fieldMetaMap;

    public FieldMeta getField(String requestField) {
        return fieldMetaMap.get(requestField);
    }

    public Map<String, String> getSelectColumnAliases() {
        Map<String, String> map = new LinkedHashMap<>();
        for (FieldMeta value : fieldMetaMap.values()) {
            if (value.isQueryable()) {
                map.put(value.getColumnName(), value.getRequestField());
            }
        }
        return map;
    }
}
