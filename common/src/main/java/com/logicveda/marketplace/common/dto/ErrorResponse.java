package com.logicveda.marketplace.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Unified error response DTO for API error responses.
 */
@Schema(description = "Error response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    @Schema(description = "HTTP status code", example = "400")
    int status,

    @Schema(description = "Error code identifier", example = "VALIDATION_ERROR")
    String errorCode,

    @Schema(description = "Human-readable error message", example = "Request validation failed")
    String message,

    @Schema(description = "Timestamp of error occurrence")
    LocalDateTime timestamp,

    @Schema(description = "Request path that caused error", example = "/api/products")
    String path,

    @Schema(description = "Field-level validation errors")
    Map<String, String> fieldErrors,

    @Schema(description = "Trace ID for debugging", example = "trace-123456")
    String traceId
) {}
