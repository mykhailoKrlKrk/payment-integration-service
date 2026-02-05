package com.payment.integration.stripe.dto;

public record PaymentResponseDto(
    String providerPaymentId,
    String clientSecret,
    String rawStatus
) {}
