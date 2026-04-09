package com.logicveda.marketplace.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transaction entity - represents a payment transaction
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transactions_order", columnList = "order_id"),
    @Index(name = "idx_transactions_customer", columnList = "customer_id"),
    @Index(name = "idx_transactions_status", columnList = "status"),
    @Index(name = "idx_transactions_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId; // Reference to Order Service

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false, unique = true, length = 100)
    private String stripePaymentMethodId; // Stripe token/PM ID

    @Column(nullable = false, unique = true, length = 100)
    private String stripeChargeId; // Stripe charge ID for refund tracking

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CARD;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 10)
    private String currency; // USD, INR, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(length = 255)
    private String failureReason;

    // Refund tracking
    @Column
    private Boolean isRefunded;

    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(length = 100)
    private String refundTransactionId;

    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime processedAt;

    @Column
    private LocalDateTime refundedAt;

    /**
     * Payment method enum
     */
    public enum PaymentMethod {
        CARD,          // Credit/Debit card
        UPI,           // UPI (India)
        DIGITAL_WALLET // Apple Pay, Google Pay, etc.
    }

    /**
     * Transaction status enum
     */
    public enum TransactionStatus {
        PENDING,       // Awaiting processing
        PROCESSING,    // Being processed
        COMPLETED,     // Successfully charged
        FAILED,        // Payment failed
        REFUNDED,      // Refunded
        PARTIALLY_REFUNDED  // Partially refunded
    }

    public boolean canBeRefunded() {
        return status == TransactionStatus.COMPLETED || status == TransactionStatus.PARTIALLY_REFUNDED;
    }
}
