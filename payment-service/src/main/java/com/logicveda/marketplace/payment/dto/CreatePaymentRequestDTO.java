package com.logicveda.marketplace.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating a payment request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    @Schema(description = "Order ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.1", message = "Amount must be greater than 0")
    @Schema(description = "Payment amount", example = "1999.99")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment method (CREDIT_CARD, DEBIT_CARD, UPI, NETBANKING, WALLET, COD)", example = "UPI")
    private String method;

    @NotBlank(message = "Currency is required")
    @Schema(description = "Currency code", example = "INR")
    private String currency;

    @Schema(description = "Idempotency key for idempotent requests", example = "order-123-payment-001")
    private String idempotencyKey;

    @Schema(description = "Additional metadata", example = "{\"notes\": \"Order from app\"}")
    private String metadata;
}

/**
 * DTO for payment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PaymentResponseDTO {

    @Schema(description = "Payment ID")
    private UUID id;

    @Schema(description = "Order ID")
    private UUID orderId;

    @Schema(description = "Amount")
    private BigDecimal amount;

    @Schema(description = "Payment status")
    private String status;

    @Schema(description = "Payment method")
    private String method;

    @Schema(description = "Transaction ID")
    private String transactionId;

    @Schema(description = "Payment intent ID (for Stripe)")
    private String paymentIntentId;

    @Schema(description = "Currency")
    private String currency;

    @Schema(description = "Processed at timestamp")
    private LocalDateTime processedAt;

    @Schema(description = "Failure reason (if any)")
    private String failureReason;

    @Schema(description = "Created at timestamp")
    private LocalDateTime createdAt;
}

/**
 * DTO for payment verification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class VerifyPaymentRequestDTO {

    @NotNull(message = "Payment ID is required")
    @Schema(description = "Payment ID to verify")
    private UUID paymentId;

    @Schema(description = "Verification token from payment gateway")
    private String verificationToken;

    @Schema(description = "Payment intent ID (for Stripe)")
    private String paymentIntentId;
}

/**
 * DTO for payment refund request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RefundPaymentRequestDTO {

    @NotNull(message = "Payment ID is required")
    @Schema(description = "Payment ID to refund")
    private UUID paymentId;

    @Schema(description = "Refund amount (null = full refund)")
    private BigDecimal refundAmount;

    @NotBlank(message = "Reason is required")
    @Schema(description = "Refund reason")
    private String reason;
}
