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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByProduct(
            @PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getByProduct(productId)));
    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getByUser(currentUser.getId())));
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @PathVariable String productId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {
        ReviewResponse response = reviewService.addReview(productId, request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added successfully", response));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable String reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Review updated",
                reviewService.updateReview(reviewId, request, currentUser.getId())));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable String reviewId,
            @AuthenticationPrincipal User currentUser) {
        reviewService.deleteReview(reviewId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }









}

