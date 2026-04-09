package com.logicveda.marketplace.vendor.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard API error response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API Error Response")
public class ErrorResponse {

    @Schema(description = "HTTP Status Code", example = "400")
    private int statusCode;

    @Schema(description = "Error code identifier", example = "VENDOR_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Error message", example = "Vendor not found with ID: xxx")
    private String message;

    @Schema(description = "Request path")
    private String path;

    @Schema(description = "Timestamp of error")
    private LocalDateTime timestamp;

    @Schema(description = "Request trace ID")
    private String traceId;

    @Schema(description = "Additional details")
    private String details;
}
