package com.payment.integration.stripe.controller;

import com.payment.integration.stripe.service.providers.stripe.StripeWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments/webhook")
@Tag(name = "Webhook", description = "Webhook event management resource")
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @PostMapping("/stripe")
    @Operation(summary = "Webhook api for handling payments status changing")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully handled event"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error / invalid request payload"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Payment provider error or internal server error"
    )
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody @NotBlank String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        boolean accepted = stripeWebhookService.handleWebHookEvent(payload, sigHeader);
        return accepted
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
