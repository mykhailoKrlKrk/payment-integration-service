package com.payment.integration.stripe.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApplicationConstants {

    @UtilityClass
    public class StripeConstants {
        public static final String STRIPE_PAYMENT_CARD = "card";
        public static final String PAYMENT_METADATA_KEY = "paymentId";
    }
}
