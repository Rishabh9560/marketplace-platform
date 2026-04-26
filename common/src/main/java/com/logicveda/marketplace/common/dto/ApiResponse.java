package com.logicveda.marketplace.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Standard API Response wrapper for all REST endpoints.Used to provide consistent response format across all microservices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API Response wrapper")
public class ApiResponse<T> implements Serializable {
    
    @Schema(description = "HTTP status code")
    private Integer status;
    
    @Schema(description = "Response message")
    private String message;
    
    @Schema(description = "Actual response data")
    private T data;
    
    @Schema(description = "Error details (if applicable)")
    private String error;
    
    @Schema(description = "Timestamp of response")
    private LocalDateTime timestamp;
    
    @Schema(description = "Request path")
    private String path;

    /**
     * Success response with data
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .status(200)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Success response with data and custom status
     */
    public static <T> ApiResponse<T> success(int status, T data, String message) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Created response (201)
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
            .status(201)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Error response
     */
    public static <T> ApiResponse<T> error(int status, String message, String error) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .error(error)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Error response with code
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
            .status(status)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
