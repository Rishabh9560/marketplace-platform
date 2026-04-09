package com.logicveda.marketplace.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Vendor entity for multi-vendor marketplace.
 * Handles vendor profile, KYC status, and commission configuration.
 */
@Entity
@Table(name = "vendors", indexes = {
    @Index(name = "idx_vendor_user", columnList = "user_id", unique = true),
    @Index(name = "idx_vendor_status", columnList = "kyc_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "userId")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String businessName;

    @Column(length = 15)
    private String gstin;

    @Column(length = 10)
    private String pan;

    @Column(length = 50)
    private String bankAccount;

    @Column(length = 11)
    private String ifscCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private KycStatus kycStatus = KycStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String kycRejectionReason;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal commissionRate = new BigDecimal("10.00");

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private UUID approvedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum for vendor KYC (Know Your Customer) status.
     */
    public enum KycStatus {
        PENDING,
        UNDER_REVIEW,
        APPROVED,
        REJECTED
    }
}
