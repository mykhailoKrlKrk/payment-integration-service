package com.payment.integration.stripe.model.enums;

import java.util.Arrays;

public enum StripeIntentStatus {
    SUCCEEDED("succeeded"),
    PROCESSING("processing"),
    CANCELED("canceled"),
    REQUIRES_PAYMENT_METHOD("requires_payment_method");

    private final String value;

    StripeIntentStatus(String value) {
        this.value = value;
    }

    public static StripeIntentStatus from(String raw) {
        return Arrays.stream(values())
                .filter(s -> s.value.equals(raw))
                .findFirst()
                .orElse(null);
    }
}
