package com.payment.integration.stripe.controller;

import com.payment.integration.stripe.dto.param.CreatePaymentParam;
import com.payment.integration.stripe.dto.PaymentResponseDto;
import com.payment.integration.stripe.model.enums.PaymentStatus;
import com.payment.integration.stripe.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Tag(name = "Payments", description = "Create and track payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(
            summary = "Create payment",
            description = """
                Creates a payment on server side and initializes provider-specific payment flow.
                The selected provider defines how the payment is processed (e.g. Stripe, PayPal).
                """
    )
    @ApiResponse(
            responseCode = "201",
            description = "Payment created",
            content = @Content(schema = @Schema(implementation = PaymentResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error / invalid request payload"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Idempotency conflict (payment already exists for the same idempotency key)"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Payment provider error or internal server error"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createPayment(@RequestBody @Valid CreatePaymentParam paymentParam) {
        return paymentService.createPayment(paymentParam);
    }

    @GetMapping("/status/{id}")
    @Operation(summary = "Get payment status by id")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully fetched payment status"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error / invalid request payload"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Payment with provided id is not found"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Payment provider error or internal server error"
    )
    public PaymentStatus getPaymentStatus(@PathVariable UUID id) {
        return paymentService.getPaymentStatus(id);
    }
}
