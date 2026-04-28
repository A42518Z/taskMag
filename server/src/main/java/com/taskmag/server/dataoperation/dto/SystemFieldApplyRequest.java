package com.taskmag.server.dataoperation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SystemFieldApplyRequest {
    @JsonProperty("TableName")
    @JsonAlias({"tableName"})
    private String tableName;
}
