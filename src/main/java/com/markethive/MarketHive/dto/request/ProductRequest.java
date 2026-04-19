package com.markethive.MarketHive.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull @DecimalMin("0.0")
    private BigDecimal price;

    @Min(0)
    private int stock;

    private String imageUrl;
    private Integer categoryId;
}

