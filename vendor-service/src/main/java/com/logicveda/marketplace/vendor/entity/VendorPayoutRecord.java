package com.logicveda.marketplace.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VendorPayoutRecord entity - tracks all vendor payouts
 */
@Entity
@Table(name = "vendor_payout_records", indexes = {
    @Index(name = "idx_payout_vendor", columnList = "vendor_id"),
    @Index(name = "idx_payout_status", columnList = "status"),
    @Index(name = "idx_payout_period", columnList = "payout_period")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class VendorPayoutRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID vendorId; // Reference to VendorProfile

    @Column(nullable = false, length = 20)
    private String payoutPeriod; // YYYYMMdd format (e.g., "202401" for Jan 2024)

    // Financial Summary
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSalesAmount; // Total sales in period

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal commissionDeducted; // Commission calculated

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundsDeducted = BigDecimal.ZERO; // Refunds in period

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal adjustments = BigDecimal.ZERO; // Manual adjustments

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netPayoutAmount; // Final amount to pay vendor

    // Payout Details
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PayoutStatus status = PayoutStatus.PENDING;

    @Column(length = 100)
    private String bankAccountNumber;

    @Column(length = 255)
    private String bankName;

    @Column(length = 100)
    private String transactionId; // External payment processor reference

    @Column
    private LocalDateTime scheduledPayoutDate;

    @Column
    private LocalDateTime actualPayoutDate;

    @Column(columnDefinition = "TEXT")
    private String failureReason; // Reason if payout failed

    @Column
    private Integer retryCount;

    // Report and Documentation
    @Column(columnDefinition = "TEXT")
    private String orderSummary; // JSON with order details

    @Column(length = 255)
    private String reportUrl; // URL to detailed payout report

    @Column(columnDefinition = "TEXT")
    private String notes; // Internal notes

    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Payout Status Enum
     */
    public enum PayoutStatus {
        PENDING,        // Awaiting payout processing
        SCHEDULED,      // Scheduled for payout
        PROCESSING,     // Currently being processed
        COMPLETED,      // Successfully paid
        FAILED,         // Payment failed
        CANCELLED,      // Payout cancelled
        ON_HOLD         // Held pending dispute resolution
    }

    public Boolean canBePaid() {
        return (status == PayoutStatus.PENDING || status == PayoutStatus.FAILED) 
                && netPayoutAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getTotalDeductions() {
        return commissionDeducted.add(refundsDeducted).subtract(adjustments);
    }
}
