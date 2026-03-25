package com.taskmag.server.dataoperation.dto;

import lombok.Data;

@Data
public class TableQueryDto {
    private String Name;
    private String DbName;
    private String PrimaryKeyFields;
    private FilterNodeDto Filter;
}
