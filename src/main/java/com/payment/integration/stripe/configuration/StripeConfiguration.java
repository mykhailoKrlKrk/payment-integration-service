package com.payment.integration.stripe.configuration;

import com.payment.integration.stripe.properties.StripeProperties;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StripeConfiguration {

    private final StripeProperties properties;

    @PostConstruct
    public void init() {
        Stripe.apiKey = properties.getSecretKey();
    }
}
