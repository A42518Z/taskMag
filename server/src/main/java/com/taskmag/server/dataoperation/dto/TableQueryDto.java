package com.taskmag.server.dataoperation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TableQueryDto {
    @JsonProperty("Name")
    @JsonAlias({"name"})
    private String name;

    @JsonProperty("DbName")
    @JsonAlias({"dbName"})
    private String dbName;

    @JsonProperty("PrimaryKeyFields")
    @JsonAlias({"primaryKeyFields"})
    private String primaryKeyFields;

    @JsonProperty("Filter")
    @JsonAlias({"filter"})
    private FilterNodeDto filter;
}
