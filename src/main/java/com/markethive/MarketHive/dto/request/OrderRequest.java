package com.markethive.MarketHive.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class OrderRequest {
    @NotBlank
    private String shippingAddress;


    // Items come from the user's cart automatically
}


