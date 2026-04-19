package com.markethive.MarketHive.repository;

import com.markethive.MarketHive.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByProductId(String productId);
    List<Review> findByUserId(String userId);
    Optional<Review> findByProductIdAndUserId(String productId, String userId);
    boolean existsByProductIdAndUserId(String productId, String userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") String productId);
}