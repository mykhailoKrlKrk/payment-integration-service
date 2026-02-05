package com.payment.integration.stripe.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("stripe")
public class StripeProperties {

    private String secretKey;
    private String webHookSecretKey;
}
