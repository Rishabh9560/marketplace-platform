package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VendorProfile DTO for API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Vendor Profile Information")
public class VendorProfileDTO {

    @Schema(description = "Vendor Profile ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "User ID associated with vendor", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @NotBlank(message = "Business name is required")
    @Size(min = 3, max = 255, message = "Business name must be between 3 and 255 characters")
    @Schema(description = "Business name", example = "Tech Gadgets Ltd")
    private String businessName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Business description")
    private String businessDescription;

    @Email(message = "Business email should be valid")
    @Schema(description = "Business email", example = "contact@techgadgets.com")
    private String businessEmail;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Business phone should be valid")
    @Schema(description = "Business phone number", example = "+1234567890")
    private String businessPhone;

    @Schema(description = "Business website", example = "https://techgadgets.com")
    private String website;

    // Business Address
    @Schema(description = "Business street address")
    private String businessAddress;

    @Schema(description = "Business city", example = "New York")
    private String businessCity;

    @Schema(description = "Business state", example = "NY")
    private String businessState;

    @Schema(description = "Business postal code", example = "10001")
    private String businessPostalCode;

    @Schema(description = "Business country", example = "United States")
    private String businessCountry;

    // KYC Information
    @Schema(description = "KYC Status", example = "VERIFIED", allowableValues = {"PENDING", "SUBMITTED", "VERIFIED", "REJECTED", "SUSPENDED"})
    private String kycStatus;

    @Schema(description = "Business license number")
    private String businessLicenseNumber;

    @Schema(description = "Tax ID")
    private String taxId;

    @Schema(description = "Bank account number (last 4 digits only in responses)")
    private String bankAccountNumber;

    @Schema(description = "Bank routing number")
    private String bankRoutingNumber;

    @Schema(description = "Bank name")
    private String bankName;

    @Schema(description = "KYC documents (JSON array of URLs)")
    private String kycDocuments;

    @Schema(description = "KYC verified date")
    private LocalDateTime kycVerifiedAt;

    // Commission & Payout
    @DecimalMin(value = "0.00", message = "Commission rate must be >= 0")
    @DecimalMax(value = "100.00", message = "Commission rate must be <= 100")
    @Schema(description = "Commission rate percentage", example = "10.00")
    private BigDecimal commissionRate;

    @Schema(description = "Total earnings", example = "5000.50")
    private BigDecimal totalEarnings;

    @Schema(description = "Total payouts", example = "3000.00")
    private BigDecimal totalPayouts;

    @Schema(description = "Available balance for payout", example = "2000.50")
    private BigDecimal availableBalance;

    // Status
    @Schema(description = "Vendor is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Vendor is suspended", example = "false")
    private Boolean isSuspended;

    @Schema(description = "Suspension reason")
    private String suspensionReason;

    // Ratings
    @Schema(description = "Average vendor rating", example = "4.50")
    private BigDecimal averageRating;

    @Schema(description = "Total number of reviews", example = "150")
    private Integer totalReviews;

    // Timestamps
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    /**
     * Create minimal response DTO with only public information
     */
    public static VendorProfileDTO publicProfile(VendorProfileDTO vendor) {
        return VendorProfileDTO.builder()
            .id(vendor.getId())
            .businessName(vendor.getBusinessName())
            .businessDescription(vendor.getBusinessDescription())
            .businessCity(vendor.getBusinessCity())
            .businessCountry(vendor.getBusinessCountry())
            .website(vendor.getWebsite())
            .averageRating(vendor.getAverageRating())
            .totalReviews(vendor.getTotalReviews())
            .isActive(vendor.getIsActive())
            .kycStatus(vendor.getKycStatus())
            .createdAt(vendor.getCreatedAt())
            .build();
    }
}
