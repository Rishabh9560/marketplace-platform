package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payout Summary DTO for dashboard and summary endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Payout Summary Information")
public class PayoutSummaryDTO {

    @Schema(description = "Vendor ID")
    private UUID vendorId;

    @Schema(description = "Total available balance for payout", example = "5000.00")
    private BigDecimal availableBalance;

    @Schema(description = "Total earnings", example = "25000.00")
    private BigDecimal totalEarnings;

    @Schema(description = "Total payouts received", example = "20000.00")
    private BigDecimal totalPayouts;

    @Schema(description = "Pending payout amount", example = "1500.00")
    private BigDecimal pendingPayoutAmount;

    @Schema(description = "Last payout date")
    private LocalDateTime lastPayoutDate;

    @Schema(description = "Next scheduled payout date")
    private LocalDateTime nextPayoutDate;

    @Schema(description = "Average commission rate", example = "10.00")
    private BigDecimal averageCommissionRate;

    @Schema(description = "Current month sales", example = "3500.00")
    private BigDecimal currentMonthSales;

    @Schema(description = "Number of pending payouts")
    private Integer pendingPayoutCount;

    @Schema(description = "Number of failed payouts")
    private Integer failedPayoutCount;

    @Schema(description = "Payout status")
    private String status;
}
