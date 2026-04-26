package com.markethive.MarketHive.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartRequest {
    @NotBlank
    private String productId;
    @Min(1)
    private int quantity = 1;
}

