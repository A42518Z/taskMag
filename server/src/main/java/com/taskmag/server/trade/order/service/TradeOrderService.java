package com.taskmag.server.trade.order.service;

import com.taskmag.server.common.exception.BizException;
import com.taskmag.server.trade.order.dto.OrderPageRequest;
import com.taskmag.server.trade.order.dto.UpdateOrderAddressRequest;
import com.taskmag.server.trade.order.dto.UpdateOrderPriceRequest;
import com.taskmag.server.trade.order.dto.UpdateOrderRemarkRequest;
import com.taskmag.server.trade.order.mapper.TradeOrderMapper;
import com.taskmag.server.trade.order.vo.OrderDetailVO;
import com.taskmag.server.trade.order.vo.OrderPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeOrderService {
    private final TradeOrderMapper tradeOrderMapper;

    public OrderPageVO page(OrderPageRequest request) {
        int pageIndex = request.getPageIndex() == null || request.getPageIndex() <= 0 ? 1 : request.getPageIndex();
        int pageSize = request.getPageSize() == null || request.getPageSize() <= 0 ? 10 : request.getPageSize();
        long offset = (long) (pageIndex - 1) * pageSize;
        return new OrderPageVO(tradeOrderMapper.selectPage(offset, pageSize, request.getKeyword()), tradeOrderMapper.count(request.getKeyword()));
    }

    public OrderDetailVO detail(String id) {
        OrderDetailVO detail = tradeOrderMapper.selectById(id);
        if (detail == null) {
            throw new BizException(4004, "order not found");
        }
        return detail;
    }

    public boolean updateRemark(UpdateOrderRemarkRequest request) {
        return tradeOrderMapper.updateRemark(request.getId(), request.getRemark()) > 0;
    }

    public boolean updatePrice(UpdateOrderPriceRequest request) {
        return tradeOrderMapper.updatePrice(request.getId(), request.getPrice()) > 0;
    }

    public boolean updateAddress(UpdateOrderAddressRequest request) {
        return tradeOrderMapper.updateAddress(request.getId(), request.getAddress()) > 0;
    }
}
