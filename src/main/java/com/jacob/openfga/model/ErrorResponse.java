package com.jacob.openfga.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.Map;

/**
 * Standard error envelope returned by {@code GlobalExceptionHandler} for all
 * failure responses, giving clients a consistent, machine-readable shape.
 */
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

    public ErrorResponse() {
    }

    public ErrorResponse(Instant timestamp, int status, String error, String message, String path,
                         Map<String, String> validationErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", validationErrors=" + validationErrors +
                '}';
    }
}
