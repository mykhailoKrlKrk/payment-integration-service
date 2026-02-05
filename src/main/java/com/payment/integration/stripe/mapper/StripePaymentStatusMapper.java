package com.payment.integration.stripe.mapper;

import com.payment.integration.stripe.model.enums.PaymentStatus;
import com.payment.integration.stripe.model.enums.StripeIntentStatus;
import com.stripe.model.PaymentIntent;
import java.util.Map;

public final class StripePaymentStatusMapper {

    private static final Map<StripeIntentStatus, PaymentStatus> BASE_MAPPING =
            Map.of(
                    StripeIntentStatus.SUCCEEDED, PaymentStatus.SUCCEEDED,
                    StripeIntentStatus.PROCESSING, PaymentStatus.PROCESSING,
                    StripeIntentStatus.CANCELED, PaymentStatus.DECLINED
            );

    private StripePaymentStatusMapper() {}

    public static PaymentStatus map(PaymentIntent intent) {
        StripeIntentStatus stripeStatus = StripeIntentStatus.from(intent.getStatus());

        if (stripeStatus == StripeIntentStatus.REQUIRES_PAYMENT_METHOD) {
            return intent.getLastPaymentError() != null
                    ? PaymentStatus.DECLINED
                    : PaymentStatus.PENDING;
        }

        return BASE_MAPPING.getOrDefault(stripeStatus, PaymentStatus.PENDING);
    }
}

