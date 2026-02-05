package com.payment.integration.stripe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Stripe-specific payment configuration"
)
public record StripeDetails(
        @Schema(
                description = "Return URL for redirect-based flows (e.g. 3DS)",
                example = "http://localhost:3000/result"
        )
        @Size(max = 2048)
        String returnUrl,

        @Schema(
                description = "Whether to save card for future payments",
                example = "true"
        )
        Boolean saveCard,

        @Schema(
                description = "Customer email for receipt",
                example = "test@example.com"
        )
        @Email
        @Size(max = 255)
        String customerEmail
) implements ProviderDetails {}
