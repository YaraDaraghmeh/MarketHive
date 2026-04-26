package com.markethive.MarketHive.controller;


import com.markethive.MarketHive.dto.request.PaymentRequest;
import com.markethive.MarketHive.dto.response.ApiResponse;
import com.markethive.MarketHive.dto.response.PaymentResponse;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Payment processed",
                paymentService.processPayment(request, currentUser.getId())));
    }
    /** User: get payment for own order */
    @GetMapping("/payments/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByOrderId(orderId)));
    }

    /** Admin: list all payments */
    @GetMapping("/admin/payments")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAll()));
    }


}
