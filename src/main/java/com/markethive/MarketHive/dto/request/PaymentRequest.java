package com.markethive.MarketHive.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotBlank
    private String orderId;
    @NotBlank
    private String method;        // e.g. "credit_card", "paypal"
    private String transactionId; // from the payment gateway
}

