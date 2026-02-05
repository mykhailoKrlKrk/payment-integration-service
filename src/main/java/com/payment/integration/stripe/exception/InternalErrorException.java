package com.payment.integration.stripe.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

public class InternalErrorException extends RestException {

    public InternalErrorException(
            @Nullable String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }
}
