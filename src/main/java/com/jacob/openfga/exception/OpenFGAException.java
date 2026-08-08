package com.jacob.openfga.exception;

/**
 * Domain-specific runtime exception raised by the service layer when a call to
 * OpenFGA fails for reasons that are not the client's fault (network errors,
 * SDK failures, upstream 5xx responses, etc.).
 *
 * <p>It is translated into a {@code 502 Bad Gateway} by
 * {@link GlobalExceptionHandler}, signalling that the wrapper itself is healthy
 * but the upstream authorization engine could not fulfil the request.
 */
public class OpenFGAException extends RuntimeException {

    public OpenFGAException(String message) {
        super(message);
    }

    public OpenFGAException(String message, Throwable cause) {
        super(message, cause);
    }
}
