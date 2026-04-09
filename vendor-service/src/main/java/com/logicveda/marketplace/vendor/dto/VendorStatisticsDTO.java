package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * Vendor Statistics DTO for dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Vendor Statistics Information")
public class VendorStatisticsDTO {

    @Schema(description = "Total active listings", example = "45")
    private Integer totalListings;

    @Schema(description = "Total products in stock", example = "1250")
    private Integer totalInventory;

    @Schema(description = "Number of products with low stock", example = "8")
    private Integer lowStockProducts;

    @Schema(description = "Total sales all-time", example = "5000")
    private Integer totalSalesAllTime;

    @Schema(description = "Current month sales", example = "500")
    private Integer currentMonthSales;

    @Schema(description = "Average product rating", example = "4.50")
    private BigDecimal averageProductRating;

    @Schema(description = "Total product reviews", example = "1250")
    private Integer totalProductReviews;

    @Schema(description = "Total revenue all-time", example = "150000.00")
    private BigDecimal totalRevenueAllTime;

    @Schema(description = "Current month revenue", example = "15000.00")
    private BigDecimal currentMonthRevenue;

    @Schema(description = "Total commission paid", example = "15000.00")
    private BigDecimal totalCommissionPaid;

    @Schema(description = "Vendor profile rating", example = "4.75")
    private BigDecimal vendorRating;

    @Schema(description = "Total customer reviews for vendor", example = "350")
    private Integer totalVendorReviews;

    @Schema(description = "Seller performance score (0-100)", example = "92")
    private Integer performanceScore;

    @Schema(description = "Average response time (hours)", example = "2")
    private Integer averageResponseTime;

    @Schema(description = "Order fulfillment rate (%)", example = "98.5")
    private BigDecimal fulfillmentRate;

    @Schema(description = "Return rate (%)", example = "2.1")
    private BigDecimal returnRate;
}
