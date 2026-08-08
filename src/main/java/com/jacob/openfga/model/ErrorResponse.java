package com.jacob.openfga.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error envelope returned by {@code GlobalExceptionHandler} for all
 * failure responses, giving clients a consistent, machine-readable shape.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** When the error occurred (UTC, ISO-8601). */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    /** HTTP status code, e.g. 400. */
    private int status;

    /** Short reason phrase, e.g. "Bad Request". */
    private String error;

    /** Human-readable description of what went wrong. */
    private String message;

    /** The request path that produced the error. */
    private String path;

    /** Optional field-level validation errors, keyed by field name. */
    private Map<String, String> validationErrors;
}
