package com.taskmag.server.trade.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailVO {
    private String id;
    private String orderNo;
    private String customerName;
    private BigDecimal price;
    private String remark;
    private String address;
    private String status;
    private LocalDateTime createTime;
}
