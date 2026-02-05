package com.payment.integration.stripe.handler;

import com.payment.integration.stripe.exception.RestException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ProblemDetail handleRestException(
            RestException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                ex.getStatusCode(),
                ex.getReason()
        );
        problemDetail.setTitle(ex.getStatusCode().toString());
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", OffsetDateTime.now());
        return problemDetail;
    }
}
