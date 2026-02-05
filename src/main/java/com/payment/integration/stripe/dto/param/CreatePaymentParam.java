package com.payment.integration.stripe.dto.param;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.payment.integration.stripe.dto.ProviderDetails;
import com.payment.integration.stripe.dto.StripeDetails;
import com.payment.integration.stripe.model.enums.PaymentProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePaymentParam(
        @Schema(
                description = "Payment amount in minor units (e.g. 1000 = 10.00 USD)",
                example = "1000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Positive
        Long amount,

        @Schema(
                description = "Currency code in lowercase (ISO-like)",
                example = "usd",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(min = 3, max = 10)
        String currency,

        @Schema(
                description = "Idempotency key to prevent duplicate payments",
                example = "test-payment-001"
        )
        @Size(max = 100)
        String idempotencyKey,

        @Schema(
                description = "Payment provider",
                example = "STRIPE",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        PaymentProvider provider,

        @Schema(
                description = "Provider-specific payment configuration",
                oneOf = {StripeDetails.class}
        )
        @Valid
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "provider"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = StripeDetails.class, name = "STRIPE")
        })
        ProviderDetails providerDetails
) {}
