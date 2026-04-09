package com.logicveda.marketplace.vendor.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Generic API response wrapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic API Response Wrapper")
public class ApiResponse<T> {

    @Schema(description = "HTTP Status Code", example = "200")
    private int statusCode;

    @Schema(description = "Response message", example = "Success")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @Schema(description = "Timestamp")
    private String timestamp;

    @Schema(description = "Request path")
    private String path;

    /**
     * Create success response with data
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .statusCode(200)
            .message(message)
            .data(data)
            .timestamp(java.time.LocalDateTime.now().toString())
            .build();
    }

    /**
     * Create success response with data (default message)
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    /**
     * Create created response
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
            .statusCode(201)
            .message(message)
            .data(data)
            .timestamp(java.time.LocalDateTime.now().toString())
            .build();
    }

    /**
     * Create created response (default message)
     */
    public static <T> ApiResponse<T> created(T data) {
        return created(data, "Resource created successfully");
    }

    /**
     * Create no content response
     */
    public static <T> ApiResponse<T> noContent(String message) {
        return ApiResponse.<T>builder()
            .statusCode(204)
            .message(message)
            .timestamp(java.time.LocalDateTime.now().toString())
            .build();
    }

    /**
     * Create no content response (default message)
     */
    public static <T> ApiResponse<T> noContent() {
        return noContent("Request processed successfully");
    }

    /**
     * Create error response
     */
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return ApiResponse.<T>builder()
            .statusCode(statusCode)
            .message(message)
            .timestamp(java.time.LocalDateTime.now().toString())
            .build();
    }

    /**
     * Check if response is successful
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Check if response is error
     */
    public boolean isError() {
        return statusCode >= 400;
    }
}
