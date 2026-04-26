package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.PaymentRequest;
import com.markethive.MarketHive.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request, String userId);
    PaymentResponse getByOrderId(String orderId);
    List<PaymentResponse> getAll();
    PaymentResponse refund(String orderId);
}

