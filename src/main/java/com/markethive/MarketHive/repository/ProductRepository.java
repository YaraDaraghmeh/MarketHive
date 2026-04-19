package com.markethive.MarketHive.repository;

import com.markethive.MarketHive.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByMarketId(String marketId);
    List<Product> findByMarketIdAndIsActive(String marketId, boolean isActive);
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findByIsActive(boolean isActive);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")

    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
