package com.markethive.MarketHive.repository;

import com.markethive.MarketHive.entity.Payment;
import com.markethive.MarketHive.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByStatus(PaymentStatus status);
}