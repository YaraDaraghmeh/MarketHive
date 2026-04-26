package com.markethive.MarketHive.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class CartResponse {
    private String id;
    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;
    private LocalDateTime addedAt;
}
