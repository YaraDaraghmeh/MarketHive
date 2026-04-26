package com.markethive.MarketHive.controller;


import com.markethive.MarketHive.dto.request.ReviewRequest;
import com.markethive.MarketHive.dto.response.ApiResponse;
import com.markethive.MarketHive.dto.response.ReviewResponse;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @GetMapping("/reviews/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByProduct(
            @PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getByProduct(productId)));
    }
    @GetMapping("/reviews/me")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getByUser(currentUser.getId())));
    }




}

