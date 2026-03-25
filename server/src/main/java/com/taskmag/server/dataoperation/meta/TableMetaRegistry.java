package com.taskmag.server.dataoperation.meta;

import com.taskmag.server.common.exception.BizException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TableMetaRegistry {
    private final Map<String, TableMeta> registry = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        Map<String, FieldMeta> userFields = new LinkedHashMap<>();
        userFields.put("ID", FieldMeta.builder().requestField("ID").columnName("id").queryable(true).writable(false).build());
        userFields.put("UserName", FieldMeta.builder().requestField("UserName").columnName("user_name").queryable(true).writable(true).build());
        userFields.put("LoginName", FieldMeta.builder().requestField("LoginName").columnName("login_name").queryable(true).writable(true).build());
        userFields.put("State", FieldMeta.builder().requestField("State").columnName("state").queryable(true).writable(true).build());
        userFields.put("Phone", FieldMeta.builder().requestField("Phone").columnName("phone").queryable(true).writable(true).build());
        userFields.put("CreateTime", FieldMeta.builder().requestField("CreateTime").columnName("create_time").queryable(true).writable(false).build());

        TableMeta userMeta = TableMeta.builder()
                .dbName("QYVirtualPlat")
                .logicalTableName("Base_UserInfo")
                .physicalTableName("base_user_info")
                .requestPrimaryKey("ID")
                .primaryKeyColumn("id")
                .allowDelete(true)
                .fieldMetaMap(userFields)
                .build();
        registry.put(buildKey(userMeta.getDbName(), userMeta.getLogicalTableName()), userMeta);
    }

    public TableMeta getTableMeta(String dbName, String logicalTableName) {
        TableMeta meta = registry.get(buildKey(dbName, logicalTableName));
        if (meta == null) {
            throw new BizException(4001, "table not allowed: " + dbName + "@" + logicalTableName);
        }
        return meta;
    }

    public TableMeta getByCompositeName(String compositeName) {
        String[] arr = compositeName.split("@");
        if (arr.length != 2) {
            throw new BizException(4001, "table name invalid: " + compositeName);
        }
        return getTableMeta(arr[0], arr[1]);
    }

    private String buildKey(String dbName, String logicalTableName) {
        return dbName + "@" + logicalTableName;
    }
}
