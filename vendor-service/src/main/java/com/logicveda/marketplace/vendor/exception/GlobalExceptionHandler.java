package com.logicveda.marketplace.vendor.exception;

import com.logicveda.marketplace.vendor.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for API responses
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle VendorException
     */
    @ExceptionHandler(VendorException.class)
    public ResponseEntity<ApiResponse<Object>> handleVendorException(
            VendorException ex,
            WebRequest request) {

        log.warn("VendorException: {} - {}", ex.getErrorCode(), ex.getMessage());

        return new ResponseEntity<>(
            ApiResponse.builder()
                .statusCode(ex.getStatusCode().value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build(),
            ex.getStatusCode()
        );
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        log.warn("Validation error: {}", errors);

        return new ResponseEntity<>(
            ApiResponse.builder()
                .statusCode(400)
                .message("Validation failed: " + errors.toString())
                .timestamp(LocalDateTime.now().toString())
                .build(),
            HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        return new ResponseEntity<>(
            ApiResponse.builder()
                .statusCode(400)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build(),
            HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";

        return new ResponseEntity<>(
            ApiResponse.builder()
                .statusCode(500)
                .message(errorMessage)
                .timestamp(LocalDateTime.now().toString())
                .build(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
