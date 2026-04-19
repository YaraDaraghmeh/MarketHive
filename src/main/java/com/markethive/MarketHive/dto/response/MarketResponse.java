package com.markethive.MarketHive.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class MarketResponse {
    private String id;
    private String ownerId;
    private String ownerName;
    private String name;
    private String description;
    private String logoUrl;
    private boolean isApproved;
    private LocalDateTime createdAt;
}
