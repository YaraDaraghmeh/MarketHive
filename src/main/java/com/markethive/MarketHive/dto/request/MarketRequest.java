package com.markethive.MarketHive.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarketRequest {
    @NotBlank
    private String name;
    private String description;
    private String logoUrl;
}
