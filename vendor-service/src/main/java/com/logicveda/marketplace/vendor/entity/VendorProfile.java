package com.logicveda.marketplace.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VendorProfile entity - detailed vendor information
 */
@Entity
@Table(name = "vendor_profiles", indexes = {
    @Index(name = "idx_vendor_profiles_user", columnList = "user_id"),
    @Index(name = "idx_vendor_profiles_status", columnList = "kyc_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class VendorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId; // Reference to User from Auth Service

    @Column(nullable = false, length = 255)
    private String businessName;

    @Column(length = 1000)
    private String businessDescription;

    @Column(length = 255)
    private String businessEmail;

    @Column(length = 20)
    private String businessPhone;

    @Column(length = 255)
    private String website;

    // Business Address
    @Column(length = 255)
    private String businessAddress;

    @Column(length = 100)
    private String businessCity;

    @Column(length = 50)
    private String businessState;

    @Column(length = 10)
    private String businessPostalCode;

    @Column(length = 100)
    private String businessCountry;

    // KYC Information
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private KYCStatus kycStatus = KYCStatus.PENDING;

    @Column(length = 100)
    private String businessLicenseNumber;

    @Column(length = 100)
    private String taxId;

    @Column(length = 100)
    private String bankAccountNumber;

    @Column(length = 20)
    private String bankRoutingNumber;

    @Column(length = 255)
    private String bankName;

    @Column(columnDefinition = "TEXT")
    private String kycDocuments; // JSON array of document URLs

    @Column
    private LocalDateTime kycVerifiedAt;

    // Commission & Payout
    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal commissionRate = new BigDecimal("10.00"); // Percentage

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalPayouts = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal availableBalance = BigDecimal.ZERO;

    // Status
    @Column
    @Builder.Default
    private Boolean isActive = true;

    @Column
    @Builder.Default
    private Boolean isSuspended = false;

    @Column(columnDefinition = "TEXT")
    private String suspensionReason;

    // Ratings
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private Integer totalReviews = 0;

    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * KYC Status Enum
     */
    public enum KYCStatus {
        PENDING,          // Awaiting submission
        SUBMITTED,        // Submitted for verification
        VERIFIED,         // KYC verified
        REJECTED,         // KYC rejected
        SUSPENDED         // Account suspended
    }

    public boolean canSellProducts() {
        return isActive && !isSuspended && kycStatus == KYCStatus.VERIFIED;
    }
}
