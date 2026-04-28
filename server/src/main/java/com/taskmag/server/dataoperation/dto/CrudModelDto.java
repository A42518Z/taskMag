package com.taskmag.server.dataoperation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CrudModelDto {
    @JsonProperty("Added")
    @JsonAlias({"added"})
    private List<Map<String, Object>> added;

    @JsonProperty("Changed")
    @JsonAlias({"changed"})
    private List<Map<String, Object>> changed;

    @JsonProperty("Deleted")
    @JsonAlias({"deleted"})
    private List<Map<String, Object>> deleted;
}
