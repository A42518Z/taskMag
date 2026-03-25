package com.taskmag.server.dataoperation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCondition {
    private String sql;
    private Map<String, Object> params = new LinkedHashMap<>();
}
