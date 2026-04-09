package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * High-level vendor management service that orchestrates multiple services
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VendorManagementService {

    private final VendorProfileService vendorProfileService;
    private final ProductListingService productListingService;
    private final VendorPayoutService payoutService;
    private final KYCService kycService;
    private final VendorStatisticsService statisticsService;

    /**
     * Perform complete vendor registration and setup
     */
    public VendorProfileDTO registerAndSetupVendor(VendorProfileDTO vendorDTO) {
        log.info("Starting vendor registration and setup for: {}", vendorDTO.getBusinessName());

        // Step 1: Register vendor
        VendorProfileDTO registeredVendor = vendorProfileService.registerVendor(vendorDTO);
        log.info("Vendor registered: {}", registeredVendor.getId());

        // Step 2: Vendor is now ready for KYC submission
        log.info("Vendor ready for KYC submission: {}", registeredVendor.getId());

        return registeredVendor;
    }

    /**
     * Complete vendor onboarding (register + KYC + first listing)
     */
    public VendorProfileDTO completeVendorOnboarding(VendorProfileDTO vendorDTO) {
        log.info("Starting complete vendor onboarding for: {}", vendorDTO.getBusinessName());

        // Register vendor
        VendorProfileDTO vendor = registerAndSetupVendor(vendorDTO);

        log.info("Vendor onboarding completed: {}", vendor.getId());
        return vendor;
    }

    /**
     * Verify vendor and enable selling
     */
    public VendorProfileDTO verifyVendorAndEnableSelling(UUID vendorId) {
        log.info("Verifying vendor and enabling selling: {}", vendorId);

        // Verify KYC
        VendorProfileDTO vendor = kycService.verifyKYC(vendorId);
        log.info("KYC verified for vendor: {}", vendorId);

        // Verify vendor can sell
        if (!vendorProfileService.canVendorSellProducts(vendorId)) {
            throw new IllegalStateException("Vendor KYC requirements not met");
        }

        log.info("Vendor selling enabled: {}", vendorId);
        return vendor;
    }

    /**
     * Suspend vendor and all listings
     */
    public void suspendVendorCompletely(UUID vendorId, String reason) {
        log.warn("Suspending vendor and all operations: {} - Reason: {}", vendorId, reason);

        // Suspend vendor
        vendorProfileService.suspendVendor(vendorId, reason);

        // All vendor's listings will automatically be restricted due to vendor suspension check
        log.warn("Vendor suspended: {}", vendorId);
    }

    /**
     * Unsuspend vendor and restore operations
     */
    public void unsuspendVendorCompletely(UUID vendorId) {
        log.info("Unsuspending vendor and restoring operations: {}", vendorId);

        vendorProfileService.unsuspendVendor(vendorId);
        log.info("Vendor unsuspended: {}", vendorId);
    }

    /**
     * Calculate vendor commission and create payout
     */
    public void calculateAndCreatePayout(UUID vendorId, String payoutPeriod, BigDecimal totalSales) {
        log.info("Calculating payout for vendor: {} - Period: {} - Sales: {}", vendorId, payoutPeriod, totalSales);

        // Get vendor
        VendorProfileDTO vendor = vendorProfileService.getVendorById(vendorId);

        // Calculate commission
        BigDecimal commissionRate = vendor.getCommissionRate();
        BigDecimal commissionAmount = totalSales.multiply(commissionRate)
            .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);

        // Calculate net payout
        BigDecimal netPayout = totalSales.subtract(commissionAmount);

        // Create payout record
        com.logicveda.marketplace.vendor.dto.VendorPayoutRecordDTO payoutDTO = 
            com.logicveda.marketplace.vendor.dto.VendorPayoutRecordDTO.builder()
            .vendorId(vendorId)
            .payoutPeriod(payoutPeriod)
            .totalSalesAmount(totalSales)
            .commissionDeducted(commissionAmount)
            .netPayoutAmount(netPayout)
            .build();

        payoutService.createPayoutRecord(payoutDTO);
        log.info("Payout record created for vendor: {}", vendorId);
    }

    /**
     * Get complete vendor dashboard data
     */
    public VendorDashboardData getVendorDashboard(UUID vendorId) {
        log.debug("Fetching dashboard data for vendor: {}", vendorId);

        VendorProfileDTO vendor = vendorProfileService.getVendorById(vendorId);
        com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO stats = statisticsService.getVendorStatistics(vendorId);
        com.logicveda.marketplace.vendor.dto.PayoutSummaryDTO payoutSummary = payoutService.getPayoutSummary(vendorId);

        return VendorDashboardData.builder()
            .vendor(vendor)
            .statistics(stats)
            .payoutSummary(payoutSummary)
            .build();
    }

    /**
     * Validate vendor health (metrics and compliance)
     */
    public VendorHealthReport validateVendorHealth(UUID vendorId) {
        log.debug("Validating vendor health: {}", vendorId);

        VendorProfileDTO vendor = vendorProfileService.getVendorById(vendorId);
        com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO stats = statisticsService.getVendorStatistics(vendorId);
        boolean isPremium = statisticsService.isVendorPremium(vendorId);
        boolean kycCompliant = kycService.areKYCRequirementsMet(vendorId);

        return VendorHealthReport.builder()
            .vendorId(vendorId)
            .isActive(vendor.getIsActive() && !vendor.getIsSuspended())
            .isSuspended(vendor.getIsSuspended())
            .kycStatus(vendor.getKycStatus())
            .isPremium(isPremium)
            .isKYCCompliant(kycCompliant)
            .performanceScore(stats.getPerformanceScore())
            .ratingScore(vendor.getAverageRating())
            .reviewCount(stats.getTotalVendorReviews())
            .build();
    }

    /**
     * Dashboard data DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class VendorDashboardData {
        private VendorProfileDTO vendor;
        private com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO statistics;
        private com.logicveda.marketplace.vendor.dto.PayoutSummaryDTO payoutSummary;
    }

    /**
     * Health report DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class VendorHealthReport {
        private UUID vendorId;
        private Boolean isActive;
        private Boolean isSuspended;
        private String kycStatus;
        private Boolean isPremium;
        private Boolean isKYCCompliant;
        private Integer performanceScore;
        private java.math.BigDecimal ratingScore;
        private Integer reviewCount;
    }
}
