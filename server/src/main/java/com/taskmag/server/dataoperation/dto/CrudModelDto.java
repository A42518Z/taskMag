package com.taskmag.server.dataoperation.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CrudModelDto {
    private List<Map<String, Object>> Added;
    private List<Map<String, Object>> Changed;
    private List<Map<String, Object>> Deleted;
}
