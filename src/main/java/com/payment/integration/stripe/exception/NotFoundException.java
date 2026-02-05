package com.payment.integration.stripe.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class NotFoundException extends RestException {

    public NotFoundException(@Nullable String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}
