package com.payment.integration.stripe.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

public class ConflictException extends RestException {

    public ConflictException(@Nullable String reason) {
        super(HttpStatus.CONFLICT, reason);
    }
}
