package com.taskmag.server.dataoperation.parser;

import com.taskmag.server.common.exception.BizException;
import com.taskmag.server.dataoperation.dto.FilterNodeDto;
import com.taskmag.server.dataoperation.dto.ParsedCondition;
import com.taskmag.server.dataoperation.meta.FieldMeta;
import com.taskmag.server.dataoperation.meta.TableMeta;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FilterParser {

    public ParsedCondition parse(FilterNodeDto filter, TableMeta tableMeta) {
        if (filter == null) {
            return new ParsedCondition("1 = 1", new LinkedHashMap<>());
        }
        AtomicInteger idx = new AtomicInteger(0);
        return parseNode(filter, tableMeta, idx);
    }

    private ParsedCondition parseNode(FilterNodeDto node, TableMeta tableMeta, AtomicInteger idx) {
        if (node == null || !StringUtils.hasText(node.getType())) {
            return new ParsedCondition("1 = 1", new LinkedHashMap<>());
        }
        String type = node.getType().toLowerCase();
        if ("and".equals(type) || "or".equals(type)) {
            if (CollectionUtils.isEmpty(node.getFilters())) {
                return new ParsedCondition("1 = 1", new LinkedHashMap<>());
            }
            List<String> sqlParts = new ArrayList<>();
            Map<String, Object> params = new LinkedHashMap<>();
            for (FilterNodeDto child : node.getFilters()) {
                ParsedCondition parsed = parseNode(child, tableMeta, idx);
                sqlParts.add("(" + parsed.getSql() + ")");
                params.putAll(parsed.getParams());
            }
            return new ParsedCondition(String.join(" " + type.toUpperCase() + " ", sqlParts), params);
        }
        if (!"cond".equals(type)) {
            throw new BizException(4002, "unsupported filter type: " + node.getType());
        }
        if (!StringUtils.hasText(node.getField())) {
            throw new BizException(4002, "filter field is required");
        }
        FieldMeta fieldMeta = tableMeta.getField(node.getField());
        if (fieldMeta == null || !fieldMeta.isQueryable()) {
            throw new BizException(4003, "field not allowed for query: " + node.getField());
        }
        String operator = node.getOperator() == null ? "equal" : node.getOperator().toLowerCase();
        Object value = node.getValueFun() == null ? null : node.getValueFun().getValue();
        String column = fieldMeta.getColumnName();
        String paramKey = "p" + idx.incrementAndGet();
        Map<String, Object> params = new LinkedHashMap<>();
        return switch (operator) {
            case "equal" -> bind(column + " = #{params." + paramKey + "}", paramKey, value, params);
            case "notequal" -> bind(column + " <> #{params." + paramKey + "}", paramKey, value, params);
            case "contains" -> bind(column + " LIKE #{params." + paramKey + "}", paramKey, "%" + value + "%", params);
            case "startswith" -> bind(column + " LIKE #{params." + paramKey + "}", paramKey, value + "%", params);
            case "endswith" -> bind(column + " LIKE #{params." + paramKey + "}", paramKey, "%" + value, params);
            case "greaterthan" -> bind(column + " > #{params." + paramKey + "}", paramKey, value, params);
            case "greaterthanorequal" -> bind(column + " >= #{params." + paramKey + "}", paramKey, value, params);
            case "lessthan" -> bind(column + " < #{params." + paramKey + "}", paramKey, value, params);
            case "lessthanorequal" -> bind(column + " <= #{params." + paramKey + "}", paramKey, value, params);
            case "isnull" -> new ParsedCondition(column + " IS NULL", params);
            case "isnotnull" -> new ParsedCondition(column + " IS NOT NULL", params);
            case "isempty" -> new ParsedCondition("(" + column + " IS NULL OR " + column + " = '')", params);
            case "isnotempty" -> new ParsedCondition("(" + column + " IS NOT NULL AND " + column + " <> '')", params);
            case "in" -> buildInCondition(column, value, paramKey, params, true);
            case "notin" -> buildInCondition(column, value, paramKey, params, false);
            default -> throw new BizException(4002, "unsupported operator: " + node.getOperator());
        };
    }

    private ParsedCondition bind(String sql, String paramKey, Object value, Map<String, Object> params) {
        params.put(paramKey, value);
        return new ParsedCondition(sql, params);
    }

    private ParsedCondition buildInCondition(String column, Object value, String paramKey, Map<String, Object> params, boolean positive) {
        if (!(value instanceof Collection<?> collection) || collection.isEmpty()) {
            throw new BizException(4002, "in/notin value must be a non-empty array");
        }
        List<String> placeholders = new ArrayList<>();
        int i = 0;
        for (Object item : collection) {
            String key = paramKey + "_" + i++;
            params.put(key, item);
            placeholders.add("#{params." + key + "}");
        }
        return new ParsedCondition(column + (positive ? " IN (" : " NOT IN (") + String.join(",", placeholders) + ")", params);
    }
}
