package com.taskmag.server.dataoperation.service;

import com.taskmag.server.common.api.DataOperationResponse;
import com.taskmag.server.common.exception.BizException;
import com.taskmag.server.dataoperation.dto.GetDataRequest;
import com.taskmag.server.dataoperation.dto.PageParamDto;
import com.taskmag.server.dataoperation.dto.ParsedCondition;
import com.taskmag.server.dataoperation.dto.TableEntryDto;
import com.taskmag.server.dataoperation.dto.TableQueryDto;
import com.taskmag.server.dataoperation.mapper.DynamicQueryMapper;
import com.taskmag.server.dataoperation.meta.TableMeta;
import com.taskmag.server.dataoperation.meta.TableMetaRegistry;
import com.taskmag.server.dataoperation.parser.FilterParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataQueryService {
    private final TableMetaRegistry tableMetaRegistry;
    private final FilterParser filterParser;
    private final DynamicQueryMapper dynamicQueryMapper;

    public DataOperationResponse query(GetDataRequest request) {
        try {
            if (request == null || CollectionUtils.isEmpty(request.getTable())) {
                throw new BizException(4000, "Table is required");
            }
            TableQueryDto tableQuery = request.getTable().get(0);
            ResolvedQueryTarget target = resolveQueryTarget(tableQuery);
            PageParamDto pageParam = request.getPageParam() == null ? new PageParamDto() : request.getPageParam();
            int pageSize = pageParam.getPageSize() == null || pageParam.getPageSize() <= 0 ? 10 : pageParam.getPageSize();
            int pageIndex = pageParam.getPageIndex() == null || pageParam.getPageIndex() <= 0 ? 1 : pageParam.getPageIndex();
            long offset = (long) (pageIndex - 1) * pageSize;

            if (target.listTables()) {
                List<TableEntryDto> tables = tableMetaRegistry.listTables(target.dbName());
                long total = tables.size();
                int fromIndex = (int) Math.min(offset, total);
                int toIndex = (int) Math.min(offset + pageSize, total);
                List<TableEntryDto> pageTables = fromIndex >= toIndex ? Collections.emptyList() : tables.subList(fromIndex, toIndex);
                List<Map<String, Object>> items = pageTables.stream().map(item -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("DbName", target.dbName());
                    row.put("Name", item.getTableName());
                    row.put("TableType", item.getTableType());
                    row.put("CompositeName", target.dbName() + "@" + item.getTableName());
                    return row;
                }).collect(Collectors.toList());
                return new DataOperationResponse(new DataOperationResponse.ResultBody(true, "mock-key", new DataOperationResponse.DataBody(items, total)));
            }

            TableMeta meta = tableMetaRegistry.getTableMeta(target.dbName(), target.tableName());
            ParsedCondition parsedCondition = filterParser.parse(tableQuery.getFilter(), meta);
            String selectColumns = meta.getSelectColumnAliases().entrySet().stream()
                    .map(e -> e.getKey() + " AS `" + e.getValue() + "`")
                    .collect(Collectors.joining(", "));
            List<Map<String, Object>> items = dynamicQueryMapper.selectPage(meta.getPhysicalTableName(), selectColumns, parsedCondition.getSql(), parsedCondition.getParams(), offset, pageSize);
            Long count = dynamicQueryMapper.count(meta.getPhysicalTableName(), parsedCondition.getSql(), parsedCondition.getParams());
            return new DataOperationResponse(new DataOperationResponse.ResultBody(true, "mock-key", new DataOperationResponse.DataBody(items, count)));
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            String detail = e.getClass().getName() + ": " + String.valueOf(e.getMessage());
            if (e.getCause() != null) {
                detail += " | cause=" + e.getCause().getClass().getName() + ": " + String.valueOf(e.getCause().getMessage());
            }
            throw new BizException(5001, detail);
        }
    }

    private ResolvedQueryTarget resolveQueryTarget(TableQueryDto tableQuery) {
        if (tableQuery == null) {
            throw new BizException(4000, "Table is required");
        }

        String rawDbName = tableQuery.getDbName();
        String rawTableName = tableQuery.getName();

        if (!StringUtils.hasText(rawDbName) && !StringUtils.hasText(rawTableName)) {
            throw new BizException(4000, "Table is required");
        }

        if (StringUtils.hasText(rawTableName) && rawTableName.contains("@")) {
            String[] arr = rawTableName.split("@", 2);
            rawDbName = arr[0];
            rawTableName = arr.length > 1 ? arr[1] : "";
        }

        String dbName = tableMetaRegistry.resolveDbName(rawDbName);
        boolean listTables = !StringUtils.hasText(rawTableName) || "*".equals(rawTableName.trim());
        String tableName = listTables ? null : rawTableName.trim();
        return new ResolvedQueryTarget(dbName, tableName, listTables);
    }

    private record ResolvedQueryTarget(String dbName, String tableName, boolean listTables) {
    }
}
