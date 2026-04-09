package com.logicveda.marketplace.vendor.exception;

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

    private static final String TRACE_ID_HEADER = "X-Trace-ID";

    /**
     * Handle VendorException
     */
    @ExceptionHandler(VendorException.class)
    public ResponseEntity<ErrorResponse> handleVendorException(
            VendorException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .statusCode(ex.getStatusCode().value())
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .traceId(getTraceId(request))
            .build();

        log.warn("VendorException: {} - {}", ex.getErrorCode(), ex.getMessage());

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .errorCode("VALIDATION_ERROR")
            .message("Validation failed")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .traceId(getTraceId(request))
            .details(errors.toString())
            .build();

        log.warn("Validation error: {}", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .errorCode("ILLEGAL_ARGUMENT")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .traceId(getTraceId(request))
            .build();

        log.warn("Illegal argument: {}", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred")
            .path(request.getDescription(false).replace("uri=", ""))
            .timestamp(LocalDateTime.now())
            .traceId(getTraceId(request))
            .build();

        log.error("Unexpected error", ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extract trace ID from request
     */
    private String getTraceId(WebRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        return traceId != null ? traceId : "N/A";
    }
}
