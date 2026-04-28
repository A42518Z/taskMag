package com.taskmag.server.dataoperation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ValueFunDto {
    @JsonProperty("Type")
    @JsonAlias({"type"})
    private String type;

    @JsonProperty("Value")
    @JsonAlias({"value"})
    private Object value;
}
