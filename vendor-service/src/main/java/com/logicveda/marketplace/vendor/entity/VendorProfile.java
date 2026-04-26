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
@Getter
@Setter
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

    // Manual getters and setters for Java 25 Lombok workaround
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    
    public String getBusinessDescription() { return businessDescription; }
    public void setBusinessDescription(String businessDescription) { this.businessDescription = businessDescription; }
    
    public String getBusinessEmail() { return businessEmail; }
    public void setBusinessEmail(String businessEmail) { this.businessEmail = businessEmail; }
    
    public String getBusinessPhone() { return businessPhone; }
    public void setBusinessPhone(String businessPhone) { this.businessPhone = businessPhone; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    
    public String getBusinessCity() { return businessCity; }
    public void setBusinessCity(String businessCity) { this.businessCity = businessCity; }
    
    public String getBusinessState() { return businessState; }
    public void setBusinessState(String businessState) { this.businessState = businessState; }
    
    public String getBusinessPostalCode() { return businessPostalCode; }
    public void setBusinessPostalCode(String businessPostalCode) { this.businessPostalCode = businessPostalCode; }
    
    public String getBusinessCountry() { return businessCountry; }
    public void setBusinessCountry(String businessCountry) { this.businessCountry = businessCountry; }
    
    public KYCStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KYCStatus kycStatus) { this.kycStatus = kycStatus; }
    
    public String getBusinessLicenseNumber() { return businessLicenseNumber; }
    public void setBusinessLicenseNumber(String businessLicenseNumber) { this.businessLicenseNumber = businessLicenseNumber; }
    
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    
    public String getBankRoutingNumber() { return bankRoutingNumber; }
    public void setBankRoutingNumber(String bankRoutingNumber) { this.bankRoutingNumber = bankRoutingNumber; }
    
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getKycDocuments() { return kycDocuments; }
    public void setKycDocuments(String kycDocuments) { this.kycDocuments = kycDocuments; }
    
    public LocalDateTime getKycVerifiedAt() { return kycVerifiedAt; }
    public void setKycVerifiedAt(LocalDateTime kycVerifiedAt) { this.kycVerifiedAt = kycVerifiedAt; }
    
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    
    public BigDecimal getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(BigDecimal totalEarnings) { this.totalEarnings = totalEarnings; }
    
    public BigDecimal getTotalPayouts() { return totalPayouts; }
    public void setTotalPayouts(BigDecimal totalPayouts) { this.totalPayouts = totalPayouts; }
    
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsSuspended() { return isSuspended; }
    public void setIsSuspended(Boolean isSuspended) { this.isSuspended = isSuspended; }
    
    public String getSuspensionReason() { return suspensionReason; }
    public void setSuspensionReason(String suspensionReason) { this.suspensionReason = suspensionReason; }
    
    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
    
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
