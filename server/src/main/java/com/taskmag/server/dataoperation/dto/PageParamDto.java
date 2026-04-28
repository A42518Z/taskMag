package com.taskmag.server.dataoperation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PageParamDto {
    @JsonProperty("PageSize")
    @JsonAlias({"pageSize"})
    private Integer pageSize;

    @JsonProperty("PageIndex")
    @JsonAlias({"pageIndex"})
    private Integer pageIndex;
}
