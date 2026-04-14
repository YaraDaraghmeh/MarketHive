package com.markethive.MarketHive.repository;

import com.markethive.MarketHive.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, String> {
    List<Market> findByOwnerId(String ownerId);
    List<Market> findByIsApproved(boolean isApproved);
    Optional<Market> findByIdAndOwnerId(String id, String ownerId);
}
