package com.taskmag.server.dataoperation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FilterNodeDto {
    @JsonProperty("Type")
    @JsonAlias({"type"})
    private String type;

    @JsonProperty("Field")
    @JsonAlias({"field"})
    private String field;

    @JsonProperty("Operator")
    @JsonAlias({"operator"})
    private String operator;

    @JsonProperty("ValueFun")
    @JsonAlias({"valueFun"})
    private ValueFunDto valueFun;

    @JsonProperty("Filters")
    @JsonAlias({"filters"})
    private List<FilterNodeDto> filters;
}
