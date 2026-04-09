package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO;
import com.logicveda.marketplace.vendor.entity.ProductListing;
import com.logicveda.marketplace.vendor.entity.VendorProfile;
import com.logicveda.marketplace.vendor.exception.VendorException;
import com.logicveda.marketplace.vendor.repository.ProductListingRepository;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Service layer for vendor statistics and analytics
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VendorStatisticsService {

    private final VendorProfileRepository vendorRepository;
    private final ProductListingRepository listingRepository;

    /**
     * Get vendor statistics
     */
    public VendorStatisticsDTO getVendorStatistics(UUID vendorId) {
        log.debug("Fetching statistics for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        // Get listing count
        long totalListings = listingRepository.countByVendorIdAndStatus(
            vendorId, ProductListing.ListingStatus.ACTIVE);

        // Get inventory
        Integer totalInventory = listingRepository.getTotalInventoryByVendor(vendorId);
        if (totalInventory == null) {
            totalInventory = 0;
        }

        // Get low stock count
        long lowStockCount = listingRepository.countOutOfStockListings(vendorId);

        // Get sales
        Integer totalSalesAllTime = listingRepository.getTotalSalesByVendor(vendorId);
        if (totalSalesAllTime == null) {
            totalSalesAllTime = 0;
        }

        // Calculate stats
        BigDecimal averageProductRating = BigDecimal.ZERO;
        Integer totalProductReviews = 0;

        // Get vendor stats
        BigDecimal vendorRating = vendor.getAverageRating();
        Integer vendorReviews = vendor.getTotalReviews();

        // Calculate performance score (0-100)
        int performanceScore = calculatePerformanceScore(vendor, totalListings);

        // Estimated metrics (would come from order service in real system)
        Integer averageResponseTime = 2; // hours
        BigDecimal fulfillmentRate = new BigDecimal("98.5");
        BigDecimal returnRate = new BigDecimal("2.1");

        return VendorStatisticsDTO.builder()
            .totalListings((int) totalListings)
            .totalInventory(totalInventory)
            .lowStockProducts((int) lowStockCount)
            .totalSalesAllTime(totalSalesAllTime)
            .currentMonthSales(0) // Would calculate from order service
            .averageProductRating(averageProductRating)
            .totalProductReviews(totalProductReviews)
            .totalRevenueAllTime(vendor.getTotalEarnings())
            .currentMonthRevenue(BigDecimal.ZERO) // Would calculate from order service
            .totalCommissionPaid(BigDecimal.ZERO) // Would calculate from payout service
            .vendorRating(vendorRating)
            .totalVendorReviews(vendorReviews)
            .performanceScore(performanceScore)
            .averageResponseTime(averageResponseTime)
            .fulfillmentRate(fulfillmentRate)
            .returnRate(returnRate)
            .build();
    }

    /**
     * Get marketplace statistics
     */
    public VendorStatisticsDTO getMarketplaceStatistics() {
        log.debug("Fetching marketplace wide statistics");

        long totalVendors = vendorRepository.countByIsActiveTrueAndIsSuspendedFalse();
        Long totalEarnings = vendorRepository.getTotalVendorEarnings();
        if (totalEarnings == null) {
            totalEarnings = 0L;
        }

        return VendorStatisticsDTO.builder()
            .totalListings(0)
            .totalInventory(0)
            .totalSalesAllTime(0)
            .totalRevenueAllTime(new BigDecimal(totalEarnings))
            .build();
    }

    /**
     * Calculate vendor performance score
     */
    private int calculatePerformanceScore(VendorProfile vendor, long listingCount) {
        int score = 50; // Base score

        // Rating bonus (max 20 points)
        if (vendor.getAverageRating() != null && vendor.getAverageRating().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratingPercentage = vendor.getAverageRating()
                .divide(new BigDecimal(5), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(20));
            score += ratingPercentage.intValue();
        }

        // Listing bonus (max 15 points)
        if (listingCount > 0) {
            int listingBonus = Math.min(15, (int) (listingCount / 10));
            score += listingBonus;
        }

        // Total reviews bonus (max 15 points)
        if (vendor.getTotalReviews() != null && vendor.getTotalReviews() > 0) {
            int reviewBonus = Math.min(15, vendor.getTotalReviews() / 100);
            score += reviewBonus;
        }

        // Cap at 100
        return Math.min(100, score);
    }

    /**
     * Get vendor ranking by sales
     */
    public int getVendorSalesRank(UUID vendorId) {
        log.debug("Calculating sales rank for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        // Count vendors with higher earnings
        return 1; // Would need to query all vendors - simplified for now
    }

    /**
     * Get vendor ranking by rating
     */
    public int getVendorRatingRank(UUID vendorId) {
        log.debug("Calculating rating rank for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        // Count vendors with higher rating
        return 1; // Would need to query all vendors - simplified for now
    }

    /**
     * Check if vendor qualifies for premium status
     */
    public boolean isVendorPremium(UUID vendorId) {
        log.debug("Checking premium status for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        Integer totalSales = listingRepository.getTotalSalesByVendor(vendorId);
        if (totalSales == null) {
            totalSales = 0;
        }

        return vendor.getAverageRating().compareTo(new BigDecimal("4.50")) >= 0 &&
               vendor.getTotalReviews() >= 100 &&
               totalSales >= 500 &&
               vendor.getKycStatus() == com.logicveda.marketplace.vendor.entity.VendorProfile.KYCStatus.VERIFIED;
    }

    /**
     * Calculate estimated earnings based on current metrics
     */
    public BigDecimal calculateEstimatedMonthlyEarnings(UUID vendorId) {
        log.debug("Calculating estimated monthly earnings for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        // Simplified calculation: (total earnings / months active)
        if (vendor.getCreatedAt() != null && vendor.getTotalEarnings().compareTo(BigDecimal.ZERO) > 0) {
            long monthsActive = java.time.temporal.ChronoUnit.MONTHS.between(
                vendor.getCreatedAt().toLocalDate().atStartOfDay(),
                java.time.LocalDateTime.now()
            );
            
            if (monthsActive > 0) {
                return vendor.getTotalEarnings()
                    .divide(new BigDecimal(monthsActive), 2, RoundingMode.HALF_UP);
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Get inventory statistics
     */
    public VendorStatisticsDTO getInventoryStatistics(UUID vendorId) {
        log.debug("Fetching inventory statistics for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        Integer totalInventory = listingRepository.getTotalInventoryByVendor(vendorId);
        if (totalInventory == null) {
            totalInventory = 0;
        }

        long outOfStock = listingRepository.countOutOfStockListings(vendorId);

        return VendorStatisticsDTO.builder()
            .totalInventory(totalInventory)
            .lowStockProducts((int) outOfStock)
            .build();
    }

    /**
     * Get sales statistics
     */
    public VendorStatisticsDTO getSalesStatistics(UUID vendorId) {
        log.debug("Fetching sales statistics for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        Integer totalSales = listingRepository.getTotalSalesByVendor(vendorId);
        if (totalSales == null) {
            totalSales = 0;
        }

        return VendorStatisticsDTO.builder()
            .totalSalesAllTime(totalSales)
            .totalRevenueAllTime(vendor.getTotalEarnings())
            .build();
    }

    /**
     * Get rating and review statistics
     */
    public VendorStatisticsDTO getRatingStatistics(UUID vendorId) {
        log.debug("Fetching rating statistics for vendor: {}", vendorId);

        VendorProfile vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> VendorException.vendorNotFound(vendorId.toString()));

        return VendorStatisticsDTO.builder()
            .vendorRating(vendor.getAverageRating())
            .totalVendorReviews(vendor.getTotalReviews())
            .build();
    }
}
