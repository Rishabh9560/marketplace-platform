package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VendorPayoutRecord DTO for API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Vendor Payout Record Information")
public class VendorPayoutRecordDTO {

    @Schema(description = "Payout Record ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @NotNull(message = "Vendor ID is required")
    @Schema(description = "Vendor ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID vendorId;

    @NotBlank(message = "Payout period is required")
    @Pattern(regexp = "^\\d{6}$", message = "Payout period must be in YYYYMM format")
    @Schema(description = "Payout period (YYYYMM)", example = "202401")
    private String payoutPeriod;

    // Financial Summary
    @NotNull(message = "Total sales amount is required")
    @DecimalMin(value = "0.00", message = "Total sales must be >= 0")
    @Schema(description = "Total sales in period", example = "10000.00")
    private BigDecimal totalSalesAmount;

    @NotNull(message = "Commission deducted is required")
    @DecimalMin(value = "0.00", message = "Commission must be >= 0")
    @Schema(description = "Commission deducted", example = "1000.00")
    private BigDecimal commissionDeducted;

    @DecimalMin(value = "0.00", message = "Refunds must be >= 0")
    @Schema(description = "Refunds deducted", example = "500.00")
    private BigDecimal refundsDeducted;

    @Schema(description = "Manual adjustments", example = "100.00")
    private BigDecimal adjustments;

    @NotNull(message = "Net payout amount is required")
    @DecimalMin(value = "0.00", message = "Net payout must be >= 0")
    @Schema(description = "Final payout amount", example = "8600.00")
    private BigDecimal netPayoutAmount;

    // Payout Details
    @Schema(description = "Payout status", example = "PENDING", allowableValues = {
        "PENDING", "SCHEDULED", "PROCESSING", "COMPLETED", "FAILED", "CANCELLED", "ON_HOLD"
    })
    private String status;

    @Schema(description = "Bank account number (masked)")
    private String bankAccountNumber;

    @Schema(description = "Bank name")
    private String bankName;

    @Schema(description = "External payment transaction ID")
    private String transactionId;

    @Schema(description = "Scheduled payout date")
    private LocalDateTime scheduledPayoutDate;

    @Schema(description = "Actual payout date")
    private LocalDateTime actualPayoutDate;

    @Schema(description = "Failure reason if payout failed")
    private String failureReason;

    @Min(value = 0, message = "Retry count cannot be negative")
    @Schema(description = "Number of retry attempts", example = "0")
    private Integer retryCount;

    // Report and Documentation
    @Schema(description = "Order summary (JSON)")
    private String orderSummary;

    @Schema(description = "Detailed payout report URL")
    private String reportUrl;

    @Schema(description = "Internal notes")
    private String notes;

    // Timestamps
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    /**
     * Calculate total deductions
     */
    public BigDecimal getTotalDeductions() {
        BigDecimal total = BigDecimal.ZERO;
        if (commissionDeducted != null) {
            total = total.add(commissionDeducted);
        }
        if (refundsDeducted != null) {
            total = total.add(refundsDeducted);
        }
        if (adjustments != null) {
            total = total.subtract(adjustments);
        }
        return total;
    }

    /**
     * Get commission percentage
     */
    public BigDecimal getCommissionPercentage() {
        if (totalSalesAmount == null || totalSalesAmount.compareTo(BigDecimal.ZERO) == 0 || commissionDeducted == null) {
            return BigDecimal.ZERO;
        }
        return commissionDeducted.multiply(new BigDecimal(100)).divide(totalSalesAmount, 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Create summary DTO for listing (without sensitive info)
     */
    public static VendorPayoutRecordDTO summary(VendorPayoutRecordDTO payout) {
        return VendorPayoutRecordDTO.builder()
            .id(payout.getId())
            .vendorId(payout.getVendorId())
            .payoutPeriod(payout.getPayoutPeriod())
            .totalSalesAmount(payout.getTotalSalesAmount())
            .netPayoutAmount(payout.getNetPayoutAmount())
            .status(payout.getStatus())
            .scheduledPayoutDate(payout.getScheduledPayoutDate())
            .actualPayoutDate(payout.getActualPayoutDate())
            .createdAt(payout.getCreatedAt())
            .build();
    }
}
