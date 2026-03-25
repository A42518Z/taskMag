package com.taskmag.server.dataoperation.service;

import com.taskmag.server.common.api.DataOperationResponse;
import com.taskmag.server.common.exception.BizException;
import com.taskmag.server.dataoperation.dto.GetDataRequest;
import com.taskmag.server.dataoperation.dto.PageParamDto;
import com.taskmag.server.dataoperation.dto.ParsedCondition;
import com.taskmag.server.dataoperation.dto.TableQueryDto;
import com.taskmag.server.dataoperation.mapper.DynamicQueryMapper;
import com.taskmag.server.dataoperation.meta.TableMeta;
import com.taskmag.server.dataoperation.meta.TableMetaRegistry;
import com.taskmag.server.dataoperation.parser.FilterParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        if (request == null || CollectionUtils.isEmpty(request.getTable())) {
            throw new BizException(4000, "Table is required");
        }
        TableQueryDto tableQuery = request.getTable().get(0);
        TableMeta meta = tableMetaRegistry.getTableMeta(tableQuery.getDbName(), tableQuery.getName());
        ParsedCondition parsedCondition = filterParser.parse(tableQuery.getFilter(), meta);
        PageParamDto pageParam = request.getPageParam() == null ? new PageParamDto() : request.getPageParam();
        int pageSize = pageParam.getPageSize() == null || pageParam.getPageSize() <= 0 ? 10 : pageParam.getPageSize();
        int pageIndex = pageParam.getPageIndex() == null || pageParam.getPageIndex() <= 0 ? 1 : pageParam.getPageIndex();
        long offset = (long) (pageIndex - 1) * pageSize;
        String selectColumns = meta.getSelectColumnAliases().entrySet().stream()
                .map(e -> e.getKey() + " AS `" + e.getValue() + "`")
                .collect(Collectors.joining(", "));
        List<Map<String, Object>> items = dynamicQueryMapper.selectPage(meta.getPhysicalTableName(), selectColumns, parsedCondition.getSql(), parsedCondition.getParams(), offset, pageSize);
        Long count = dynamicQueryMapper.count(meta.getPhysicalTableName(), parsedCondition.getSql(), parsedCondition.getParams());
        return new DataOperationResponse(new DataOperationResponse.ResultBody(true, "mock-key", new DataOperationResponse.DataBody(items, count)));
    }
}
