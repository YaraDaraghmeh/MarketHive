package com.markethive.MarketHive.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class ReviewResponse {
    private String id;
    private String productId;
    private String userId;
    private String userName;
    private short rating;
    private String comment;
    private LocalDateTime createdAt;
}
