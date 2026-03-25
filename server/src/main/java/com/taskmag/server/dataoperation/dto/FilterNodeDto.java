package com.taskmag.server.dataoperation.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilterNodeDto {
    private String Type;
    private String Field;
    private String Operator;
    private ValueFunDto ValueFun;
    private List<FilterNodeDto> Filters;
}
