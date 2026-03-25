package com.taskmag.server.trade.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageVO {
    private List<OrderDetailVO> records;
    private Long total;
}
