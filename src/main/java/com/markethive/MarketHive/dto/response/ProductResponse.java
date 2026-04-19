package com.markethive.MarketHive.dto.response;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class ProductResponse {
    private String id;
    private String marketId;
    private String marketName;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String imageUrl;
    private boolean isActive;
    private Double averageRating;
    private LocalDateTime createdAt;
}
