package com.taskmag.server.trade.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderAddressRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String address;
}
