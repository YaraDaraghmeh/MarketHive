package com.markethive.MarketHive.repository;

import com.markethive.MarketHive.entity.Order;
import com.markethive.MarketHive.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
}