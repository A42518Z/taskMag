package com.taskmag.server.dataoperation.controller;

import com.taskmag.server.common.api.CommonResult;
import com.taskmag.server.common.api.DataOperationResponse;
import com.taskmag.server.common.context.RequestMetaContext;
import com.taskmag.server.dataoperation.dto.BatchCrudRequestItem;
import com.taskmag.server.dataoperation.dto.GetDataRequest;
import com.taskmag.server.dataoperation.dto.SystemFieldApplyRequest;
import com.taskmag.server.dataoperation.service.DataCrudService;
import com.taskmag.server.dataoperation.service.DataQueryService;
import com.taskmag.server.dataoperation.service.SystemFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/DataOperation")
@RequiredArgsConstructor
public class DataOperationController {
    private final DataQueryService dataQueryService;
    private final DataCrudService dataCrudService;
    private final SystemFieldService systemFieldService;

    @PostMapping("/GetData")
    public DataOperationResponse getData(@RequestBody GetDataRequest request) {
        RequestMetaContext.get();
        return dataQueryService.query(request);
    }

    @PostMapping("/BatchTableOperateRequestByCRUD")
    public CommonResult<Boolean> batchTableOperateRequestByCRUD(@RequestBody List<BatchCrudRequestItem> request) {
        return CommonResult.success(dataCrudService.batchOperate(request));
    }

    @PostMapping("/ApplySystemFields")
    public CommonResult<Map<String, Object>> applySystemFields(@RequestBody SystemFieldApplyRequest request) {
        return CommonResult.success(systemFieldService.applySystemFields(request.getTableName()));
    }
}
