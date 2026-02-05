package com.payment.integration.stripe.dto;

import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentId,
        Long amount,
        String currency,
        String providerPaymentId,
        String clientSecret,
        String rawStatus
) {}
