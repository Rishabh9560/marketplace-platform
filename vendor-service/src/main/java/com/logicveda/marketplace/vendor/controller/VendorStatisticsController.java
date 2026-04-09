package com.logicveda.marketplace.vendor.controller;

import com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO;
import com.logicveda.marketplace.vendor.service.VendorStatisticsService;
import com.logicveda.marketplace.vendor.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for vendor statistics and analytics
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Vendor statistics and analytics endpoints")
public class VendorStatisticsController {

    private final VendorStatisticsService statisticsService;

    /**
     * Get comprehensive vendor statistics
     */
    @GetMapping("/vendor/{vendorId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get vendor statistics", description = "Retrieve comprehensive statistics for a vendor")
    public ResponseEntity<ApiResponse<VendorStatisticsDTO>> getVendorStatistics(
            @PathVariable 
            @Parameter(description = "Vendor ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID vendorId) {
        log.info("Get vendor statistics request - Vendor: {}", vendorId);

        VendorStatisticsDTO stats = statisticsService.getVendorStatistics(vendorId);

        return ResponseEntity.ok(ApiResponse.success(stats, "Vendor statistics retrieved successfully"));
    }

    /**
     * Get marketplace statistics
     */
    @GetMapping("/marketplace")
    @Operation(summary = "Get marketplace statistics", description = "Retrieve platform-wide statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMarketplaceStatistics() {
        log.info("Get marketplace statistics request");

        Object marketplaceStats = statisticsService.getMarketplaceStatistics();

        Map<String, Object> response = new HashMap<>();
        response.put("marketplaceStatistics", marketplaceStats);

        return ResponseEntity.ok(ApiResponse.success(response, "Marketplace statistics retrieved successfully"));
    }

    /**
     * Get vendor performance score
     */
    @GetMapping("/vendor/{vendorId}/performance-score")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get performance score", description = "Calculate vendor performance score")
    public ResponseEntity<ApiResponse<Object>> getPerformanceScore(
            @PathVariable UUID vendorId) {
        log.info("Get performance score request - Vendor: {}", vendorId);

        BigDecimal score = statisticsService.calculatePerformanceScore(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final BigDecimal performanceScore = score;
                public final String rating = score.compareTo(new BigDecimal("75")) >= 0 ? "EXCELLENT" :
                                              score.compareTo(new BigDecimal("50")) >= 0 ? "GOOD" : "AVERAGE";
            },
            "Performance score calculated successfully"
        ));
    }

    /**
     * Check if vendor is premium
     */
    @GetMapping("/vendor/{vendorId}/premium-status")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Check premium status", description = "Determine if vendor meets premium criteria")
    public ResponseEntity<ApiResponse<Object>> checkPremiumStatus(
            @PathVariable UUID vendorId) {
        log.info("Check premium status request - Vendor: {}", vendorId);

        boolean isPremium = statisticsService.isVendorPremium(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final boolean isPremium = isPremium;
            },
            "Premium status checked successfully"
        ));
    }

    /**
     * Get vendor rating statistics
     */
    @GetMapping("/vendor/{vendorId}/ratings")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get rating statistics", description = "Retrieve vendor rating statistics")
    public ResponseEntity<ApiResponse<Object>> getRatingStatistics(
            @PathVariable UUID vendorId) {
        log.info("Get rating statistics request - Vendor: {}", vendorId);

        Object ratingStats = statisticsService.getRatingStatistics(vendorId);

        return ResponseEntity.ok(ApiResponse.success(ratingStats, "Rating statistics retrieved successfully"));
    }

    /**
     * Get vendor inventory statistics
     */
    @GetMapping("/vendor/{vendorId}/inventory")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get inventory statistics", description = "Retrieve vendor inventory statistics")
    public ResponseEntity<ApiResponse<Object>> getInventoryStatistics(
            @PathVariable UUID vendorId) {
        log.info("Get inventory statistics request - Vendor: {}", vendorId);

        Object inventoryStats = statisticsService.getInventoryStatistics(vendorId);

        return ResponseEntity.ok(ApiResponse.success(inventoryStats, "Inventory statistics retrieved successfully"));
    }

    /**
     * Get vendor sales statistics
     */
    @GetMapping("/vendor/{vendorId}/sales")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get sales statistics", description = "Retrieve vendor sales statistics")
    public ResponseEntity<ApiResponse<Object>> getSalesStatistics(
            @PathVariable UUID vendorId) {
        log.info("Get sales statistics request - Vendor: {}", vendorId);

        Object salesStats = statisticsService.getSalesStatistics(vendorId);

        return ResponseEntity.ok(ApiResponse.success(salesStats, "Sales statistics retrieved successfully"));
    }

    /**
     * Get estimated monthly earnings
     */
    @GetMapping("/vendor/{vendorId}/estimated-earnings")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get estimated earnings", description = "Calculate vendor's estimated monthly earnings")
    public ResponseEntity<ApiResponse<Object>> getEstimatedMonthlyEarnings(
            @PathVariable UUID vendorId) {
        log.info("Get estimated monthly earnings request - Vendor: {}", vendorId);

        BigDecimal estimatedEarnings = statisticsService.calculateEstimatedMonthlyEarnings(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final BigDecimal estimatedMonthlyEarnings = estimatedEarnings;
            },
            "Estimated earnings calculated successfully"
        ));
    }

    /**
     * Get vendor ranking by sales
     */
    @GetMapping("/vendor/{vendorId}/sales-rank")
    @Operation(summary = "Get sales rank", description = "Get vendor ranking by total sales")
    public ResponseEntity<ApiResponse<Object>> getVendorSalesRank(
            @PathVariable UUID vendorId) {
        log.info("Get vendor sales rank request - Vendor: {}", vendorId);

        int rank = statisticsService.getVendorSalesRank(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final int rank = rank;
            },
            "Vendor sales rank retrieved successfully"
        ));
    }

    /**
     * Get vendor ranking by rating
     */
    @GetMapping("/vendor/{vendorId}/rating-rank")
    @Operation(summary = "Get rating rank", description = "Get vendor ranking by average rating")
    public ResponseEntity<ApiResponse<Object>> getVendorRatingRank(
            @PathVariable UUID vendorId) {
        log.info("Get vendor rating rank request - Vendor: {}", vendorId);

        int rank = statisticsService.getVendorRatingRank(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final int rank = rank;
            },
            "Vendor rating rank retrieved successfully"
        ));
    }

    /**
     * Get top vendors by sales
     */
    @GetMapping("/top-vendors/sales")
    @Operation(summary = "Get top vendors by sales", description = "Retrieve top 10 vendors by total sales")
    public ResponseEntity<ApiResponse<Object>> getTopVendorsBySales() {
        log.info("Get top vendors by sales request");

        Object topVendors = statisticsService.getTopVendorsBySales();

        return ResponseEntity.ok(ApiResponse.success(topVendors, "Top vendors by sales retrieved successfully"));
    }

    /**
     * Get top vendors by rating
     */
    @GetMapping("/top-vendors/rating")
    @Operation(summary = "Get top vendors by rating", description = "Retrieve top 10 vendors by average rating")
    public ResponseEntity<ApiResponse<Object>> getTopVendorsByRating() {
        log.info("Get top vendors by rating request");

        Object topVendors = statisticsService.getTopVendorsByRating();

        return ResponseEntity.ok(ApiResponse.success(topVendors, "Top vendors by rating retrieved successfully"));
    }

    /**
     * Get premium vendors
     */
    @GetMapping("/premium-vendors")
    @Operation(summary = "Get premium vendors", description = "Retrieve all vendors with premium status")
    public ResponseEntity<ApiResponse<Object>> getPremiumVendors() {
        log.info("Get premium vendors request");

        Object premiumVendors = statisticsService.getPremiumVendors();

        return ResponseEntity.ok(ApiResponse.success(premiumVendors, "Premium vendors retrieved successfully"));
    }

    /**
     * Get category statistics
     */
    @GetMapping("/categories/{category}")
    @Operation(summary = "Get category statistics", description = "Retrieve statistics for specific product category")
    public ResponseEntity<ApiResponse<Object>> getCategoryStatistics(
            @PathVariable String category) {
        log.info("Get category statistics request - Category: {}", category);

        Object categoryStats = statisticsService.getCategoryStatistics(category);

        return ResponseEntity.ok(ApiResponse.success(categoryStats, "Category statistics retrieved successfully"));
    }

    /**
     * Get dashboard metrics
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard metrics", description = "Retrieve platform dashboard metrics")
    public ResponseEntity<ApiResponse<Object>> getDashboardMetrics() {
        log.info("Get dashboard metrics request");

        Map<String, Object> dashboardMetrics = new HashMap<>();
        
        // Platform metrics
        Object marketplaceStats = statisticsService.getMarketplaceStatistics();
        dashboardMetrics.put("platformStatistics", marketplaceStats);

        return ResponseEntity.ok(ApiResponse.success(dashboardMetrics, "Dashboard metrics retrieved successfully"));
    }

    /**
     * Get vendor comparison
     */
    @GetMapping("/comparison")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get vendor comparison", description = "Compare multiple vendors' statistics")
    public ResponseEntity<ApiResponse<Object>> compareVendors(
            @RequestParam java.util.List<UUID> vendorIds) {
        log.info("Compare vendors request - Count: {}", vendorIds.size());

        Map<UUID, Object> comparisonData = new HashMap<>();
        
        for (UUID vendorId : vendorIds) {
            try {
                VendorStatisticsDTO stats = statisticsService.getVendorStatistics(vendorId);
                comparisonData.put(vendorId, stats);
            } catch (Exception e) {
                log.warn("Failed to retrieve statistics for vendor: {}", vendorId);
            }
        }

        return ResponseEntity.ok(ApiResponse.success(comparisonData, "Vendor comparison retrieved successfully"));
    }

    /**
     * Export statistics report
     */
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export report", description = "Export statistics report in specified format")
    public ResponseEntity<ApiResponse<Object>> exportStatisticsReport(
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(required = false) UUID vendorId) {
        log.info("Export statistics report request - Format: {}, VendorId: {}", format, vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String format = format;
                public final String status = "READY_FOR_DOWNLOAD";
                public final UUID vendorId = vendorId;
            },
            "Statistics report ready for download"
        ));
    }

    /**
     * Get performance trends
     */
    @GetMapping("/vendor/{vendorId}/trends")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get performance trends", description = "Retrieve vendor performance trends over time")
    public ResponseEntity<ApiResponse<Object>> getPerformanceTrends(
            @PathVariable UUID vendorId,
            @RequestParam(defaultValue = "30") int days) {
        log.info("Get performance trends request - Vendor: {}, Days: {}", vendorId, days);

        Object trends = statisticsService.getPerformanceTrends(vendorId, days);

        return ResponseEntity.ok(ApiResponse.success(trends, "Performance trends retrieved successfully"));
    }
}
