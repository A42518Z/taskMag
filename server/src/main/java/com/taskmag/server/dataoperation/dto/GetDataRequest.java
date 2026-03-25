package com.taskmag.server.dataoperation.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetDataRequest {
    private List<TableQueryDto> Table;
    private PageParamDto PageParam;
}
