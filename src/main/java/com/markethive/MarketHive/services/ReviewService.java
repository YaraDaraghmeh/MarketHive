package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.ReviewRequest;
import com.markethive.MarketHive.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(String productId, ReviewRequest request, String userId);
    List<ReviewResponse> getByProduct(String productId);
    List<ReviewResponse> getByUser(String userId);
    ReviewResponse updateReview(String reviewId, ReviewRequest request, String userId);
    void deleteReview(String reviewId, String userId);
}
