package com.jacob.openfga.exception;

import com.jacob.openfga.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralised exception handling for the REST layer.
 *
 * <p>Converts exceptions into the standard {@link ErrorResponse} envelope with a
 * meaningful HTTP status:
 * <ul>
 *   <li>{@code 400 Bad Request} — validation / malformed request errors</li>
 *   <li>{@code 502 Bad Gateway} — failures talking to the upstream OpenFGA server</li>
 *   <li>{@code 500 Internal Server Error} — anything unexpected</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles bean-validation failures on {@code @Valid @RequestBody} arguments,
     * collecting per-field messages into the response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed for {}: {}", request.getRequestURI(), fieldErrors);

        ErrorResponse body = baseError(HttpStatus.BAD_REQUEST, "Request validation failed", request);
        body.setValidationErrors(fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles missing required query parameters (e.g. on {@code GET /objects}).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing parameter for {}: {}", request.getRequestURI(), ex.getParameterName());
        String message = "Missing required parameter: " + ex.getParameterName();
        return ResponseEntity.badRequest().body(baseError(HttpStatus.BAD_REQUEST, message, request));
    }

    /**
     * Handles failures originating from the OpenFGA upstream. The wrapper is
     * healthy but its dependency could not satisfy the request, hence 502.
     */
    @ExceptionHandler(OpenFGAException.class)
    public ResponseEntity<ErrorResponse> handleOpenFGA(
            OpenFGAException ex, HttpServletRequest request) {
        log.error("OpenFGA error for {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(baseError(HttpStatus.BAD_GATEWAY, ex.getMessage(), request));
    }

    /**
     * Catch-all for anything not handled above.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error for {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(baseError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred", request));
    }

    /** Builds a populated {@link ErrorResponse} for the given status and message. */
    private ErrorResponse baseError(HttpStatus status, String message, HttpServletRequest request) {
        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                null);
    }
}
