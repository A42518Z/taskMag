package com.taskmag.server.dataoperation.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldMeta {
    private String requestField;
    private String columnName;
    private boolean queryable;
    private boolean writable;
}
