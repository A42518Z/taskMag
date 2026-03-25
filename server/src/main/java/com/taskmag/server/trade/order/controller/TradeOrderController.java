package com.taskmag.server.trade.order.controller;

import com.taskmag.server.common.api.CommonResult;
import com.taskmag.server.trade.order.dto.OrderPageRequest;
import com.taskmag.server.trade.order.dto.UpdateOrderAddressRequest;
import com.taskmag.server.trade.order.dto.UpdateOrderPriceRequest;
import com.taskmag.server.trade.order.dto.UpdateOrderRemarkRequest;
import com.taskmag.server.trade.order.service.TradeOrderService;
import com.taskmag.server.trade.order.vo.OrderDetailVO;
import com.taskmag.server.trade.order.vo.OrderPageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trade/order")
@RequiredArgsConstructor
public class TradeOrderController {
    private final TradeOrderService tradeOrderService;

    @GetMapping("/page")
    public CommonResult<OrderPageVO> page(OrderPageRequest request) {
        return CommonResult.success(tradeOrderService.page(request));
    }

    @GetMapping("/get-detail")
    public CommonResult<OrderDetailVO> getDetail(@RequestParam("id") String id) {
        return CommonResult.success(tradeOrderService.detail(id));
    }

    @PutMapping("/update-remark")
    public CommonResult<Boolean> updateRemark(@Valid @RequestBody UpdateOrderRemarkRequest request) {
        return CommonResult.success(tradeOrderService.updateRemark(request));
    }

    @PutMapping("/update-price")
    public CommonResult<Boolean> updatePrice(@Valid @RequestBody UpdateOrderPriceRequest request) {
        return CommonResult.success(tradeOrderService.updatePrice(request));
    }

    @PutMapping("/update-address")
    public CommonResult<Boolean> updateAddress(@Valid @RequestBody UpdateOrderAddressRequest request) {
        return CommonResult.success(tradeOrderService.updateAddress(request));
    }
}
