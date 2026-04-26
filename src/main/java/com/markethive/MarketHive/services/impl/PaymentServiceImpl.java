package com.markethive.MarketHive.services.impl;


import com.markethive.MarketHive.dto.request.PaymentRequest;
import com.markethive.MarketHive.dto.response.PaymentResponse;
import com.markethive.MarketHive.entity.Order;
import com.markethive.MarketHive.entity.Payment;
import com.markethive.MarketHive.enums.OrderStatus;
import com.markethive.MarketHive.enums.PaymentStatus;
import com.markethive.MarketHive.exception.BadRequestException;
import com.markethive.MarketHive.exception.ForbiddenException;
import com.markethive.MarketHive.exception.ResourceNotFoundException;
import com.markethive.MarketHive.repository.OrderRepository;
import com.markethive.MarketHive.repository.PaymentRepository;
import com.markethive.MarketHive.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, String userId) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This order does not belong to you");
        }
        if (order.getStatus() == OrderStatus.cancelled) {
            throw new BadRequestException("Cannot pay for a cancelled order");
        }

        // Check if payment already exists
        paymentRepository.findByOrderId(order.getId()).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.paid) {
                throw new BadRequestException("Order is already paid");
            }
        });

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElse(Payment.builder().order(order).build());

        payment.setAmount(order.getTotalAmount());
        payment.setMethod(request.getMethod());
        payment.setTransactionId(request.getTransactionId());
        payment.setStatus(PaymentStatus.paid);
        payment.setPaidAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        // Move order to confirmed
        order.setStatus(OrderStatus.confirmed);
        orderRepository.save(order);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse refund(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));

        if (payment.getStatus() != PaymentStatus.paid) {
            throw new BadRequestException("Only paid payments can be refunded");
        }

        payment.setStatus(PaymentStatus.refunded);
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.cancelled);
        orderRepository.save(order);

        return mapToResponse(paymentRepository.save(payment));
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .method(p.getMethod())
                .transactionId(p.getTransactionId())
                .paidAt(p.getPaidAt())
                .build();
    }
}

