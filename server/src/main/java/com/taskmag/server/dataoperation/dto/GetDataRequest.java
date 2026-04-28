package com.taskmag.server.dataoperation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetDataRequest {
    @JsonProperty("Table")
    @JsonAlias({"table"})
    private List<TableQueryDto> table;

    @JsonProperty("PageParam")
    @JsonAlias({"pageParam"})
    private PageParamDto pageParam;
}
