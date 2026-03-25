package com.taskmag.server.trade.order.dto;

import lombok.Data;

@Data
public class OrderPageRequest {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private String keyword;
}
