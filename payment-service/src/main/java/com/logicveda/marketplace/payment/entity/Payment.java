package com.logicveda.marketplace.payment.entity;

import com.marketplace.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment entity representing a payment transaction
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "INR";

    @Column(name = "description")
    private String description;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "refund_amount", precision = 19, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "idempotency_key", unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    /**
     * Enum for payment status
     */
    public enum PaymentStatus {
        PENDING,           // Initial state
        PROCESSING,        // Payment is being processed
        COMPLETED,         // Payment successful
        FAILED,           // Payment failed
        CANCELLED,        // Payment cancelled
        REFUNDED          // Payment refunded
    }

    /**
     * Enum for payment methods
     */
    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        UPI,
        NETBANKING,
        WALLET,
        COD              // Cash on Delivery
    }
}
