package com.payment.integration.stripe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan
public class StripePaymentIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(StripePaymentIntegrationApplication.class, args);
    }
}
