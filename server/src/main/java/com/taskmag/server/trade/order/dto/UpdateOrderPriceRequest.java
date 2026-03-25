package com.taskmag.server.trade.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateOrderPriceRequest {
    @NotBlank
    private String id;
    @NotNull
    private BigDecimal price;
}
