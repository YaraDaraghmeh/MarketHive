package com.markethive.MarketHive.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @Min(1) @Max(5)
    private short rating;
    private String comment;
}