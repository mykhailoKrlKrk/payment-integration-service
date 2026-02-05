package com.payment.integration.stripe.service.strategy;

import com.payment.integration.stripe.dto.PaymentResponseDto;
import com.payment.integration.stripe.model.Payment;
import com.payment.integration.stripe.model.enums.PaymentProvider;

public interface PaymentManagementStrategy {

    PaymentProvider getProvider();

    PaymentResponseDto createPayment(Payment payment, String idempotencyKey);
}
