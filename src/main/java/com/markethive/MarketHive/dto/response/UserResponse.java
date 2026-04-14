package com.markethive.MarketHive.dto.response;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
}

