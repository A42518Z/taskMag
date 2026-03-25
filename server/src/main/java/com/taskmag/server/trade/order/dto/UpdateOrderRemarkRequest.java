package com.taskmag.server.trade.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderRemarkRequest {
    @NotBlank
    private String id;
    private String remark;
}
