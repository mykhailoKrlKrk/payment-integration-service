package com.payment.integration.stripe.service.strategy;

import static com.payment.integration.stripe.constants.ApplicationConstants.StripeConstants.PAYMENT_METADATA_KEY;
import static com.payment.integration.stripe.constants.ApplicationConstants.StripeConstants.STRIPE_PAYMENT_CARD;

import com.payment.integration.stripe.dto.PaymentResponseDto;
import com.payment.integration.stripe.exception.InternalErrorException;
import com.payment.integration.stripe.model.Payment;
import com.payment.integration.stripe.model.enums.PaymentProvider;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentManagementStrategy implements PaymentManagementStrategy {

    @Override
    public PaymentResponseDto createPayment(Payment payment, String idempotencyKey) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(payment.getAmount())
                .setCurrency(payment.getCurrency())
                .addPaymentMethodType(STRIPE_PAYMENT_CARD)
                .putMetadata(PAYMENT_METADATA_KEY, payment.getId().toString())
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params, requestOptions);
            return new PaymentResponseDto(intent.getId(), intent.getClientSecret(), intent.getStatus());
        } catch (StripeException e) {
            log.error("Failed to process payment {}", e.getMessage());
            throw new InternalErrorException("Failed to process payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.STRIPE;
    }
}
