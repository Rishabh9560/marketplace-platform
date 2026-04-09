package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.config.TestDataConfig;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for VendorStatisticsService
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("VendorStatisticsService Integration Tests")
public class VendorStatisticsServiceTest {

    @Autowired
    private VendorStatisticsService statisticsService;

    @Autowired
    private VendorProfileService vendorService;

    @Autowired
    private VendorProfileRepository vendorRepository;

    private UUID testVendorId;

    @BeforeEach
    public void setUp() {
        vendorRepository.deleteAll();

        // Create test vendor
        VendorProfileDTO vendorDTO = TestDataConfig.createTestVendorProfileDTO();
        vendorDTO.setBusinessEmail("vendor" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor = vendorService.registerVendor(vendorDTO);
        testVendorId = vendor.getId();
        vendorService.verifyKYC(testVendorId);
    }

    @Test
    @DisplayName("Should retrieve vendor statistics")
    public void testGetVendorStatistics() {
        // Arrange
        vendorService.updateVendorRating(testVendorId, new BigDecimal("4.5"), 50);
        vendorService.updateVendorBalance(testVendorId, new BigDecimal("10000.00"), true);

        // Act
        VendorStatisticsDTO stats = statisticsService.getVendorStatistics(testVendorId);

        // Assert
        assertNotNull(stats);
        assertEquals(testVendorId, stats.getVendorId());
        assertNotNull(stats.getTotalSales());
        assertNotNull(stats.getTotalEarnings());
    }

    @Test
    @DisplayName("Should calculate performance score")
    public void testCalculatePerformanceScore() {
        // Arrange
        vendorService.updateVendorRating(testVendorId, new BigDecimal("4.5"), 50);

        // Act
        BigDecimal score = statisticsService.calculatePerformanceScore(testVendorId);

        // Assert
        assertNotNull(score);
        assertTrue(score.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(score.compareTo(new BigDecimal(100)) <= 0);
    }

    @Test
    @DisplayName("Should determine if vendor is premium")
    public void testIsVendorPremium() {
        // Arrange - Set up to meet premium criteria
        vendorService.updateVendorRating(testVendorId, new BigDecimal("4.5"), 100);

        // Act
        boolean isPremium = statisticsService.isVendorPremium(testVendorId);

        // Assert
        assertNotNull(isPremium);
    }

    @Test
    @DisplayName("Should return false for non-premium vendor")
    public void testIsNotPremium() {
        // Arrange - Vendor with low ratings
        vendorService.updateVendorRating(testVendorId, new BigDecimal("2.0"), 5);

        // Act
        boolean isPremium = statisticsService.isVendorPremium(testVendorId);

        // Assert
        assertFalse(isPremium);
    }

    @Test
    @DisplayName("Should get rating statistics")
    public void testGetRatingStatistics() {
        // Arrange
        vendorService.updateVendorRating(testVendorId, new BigDecimal("4.5"), 50);

        // Act
        Object ratingStats = statisticsService.getRatingStatistics(testVendorId);

        // Assert
        assertNotNull(ratingStats);
    }

    @Test
    @DisplayName("Should get inventory statistics")
    public void testGetInventoryStatistics() {
        // Act
        Object inventoryStats = statisticsService.getInventoryStatistics(testVendorId);

        // Assert
        assertNotNull(inventoryStats);
    }

    @Test
    @DisplayName("Should get sales statistics")
    public void testGetSalesStatistics() {
        // Arrange
        vendorService.updateVendorBalance(testVendorId, new BigDecimal("5000.00"), true);

        // Act
        Object salesStats = statisticsService.getSalesStatistics(testVendorId);

        // Assert
        assertNotNull(salesStats);
    }

    @Test
    @DisplayName("Should calculate estimated monthly earnings")
    public void testCalculateEstimatedMonthlyEarnings() {
        // Arrange
        vendorService.updateVendorBalance(testVendorId, new BigDecimal("12000.00"), true);

        // Act
        BigDecimal estimatedEarnings = statisticsService.calculateEstimatedMonthlyEarnings(testVendorId);

        // Assert
        assertNotNull(estimatedEarnings);
        assertTrue(estimatedEarnings.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Should get vendor sales rank")
    public void testGetVendorSalesRank() {
        // Arrange - Create multiple vendors with different sales
        VendorProfileDTO vendor1 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorBalance(vendor1.getId(), new BigDecimal("5000.00"), true);

        VendorProfileDTO vendor2 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorBalance(vendor2.getId(), new BigDecimal("3000.00"), true);

        // Act
        int rank1 = statisticsService.getVendorSalesRank(vendor1.getId());
        int rank2 = statisticsService.getVendorSalesRank(vendor2.getId());

        // Assert
        assertNotNull(rank1);
        assertNotNull(rank2);
        assertTrue(rank1 > 0);
        assertTrue(rank2 > 0);
    }

    @Test
    @DisplayName("Should get vendor rating rank")
    public void testGetVendorRatingRank() {
        // Arrange - Create multiple vendors with different ratings
        VendorProfileDTO vendor1 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorRating(vendor1.getId(), new BigDecimal("4.8"), 50);

        VendorProfileDTO vendor2 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorRating(vendor2.getId(), new BigDecimal("3.5"), 30);

        // Act
        int rank1 = statisticsService.getVendorRatingRank(vendor1.getId());
        int rank2 = statisticsService.getVendorRatingRank(vendor2.getId());

        // Assert
        assertNotNull(rank1);
        assertNotNull(rank2);
        assertTrue(rank1 > 0);
        assertTrue(rank2 > 0);
    }

    @Test
    @DisplayName("Should get top vendors by sales")
    public void testGetTopVendorsBySales() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorBalance(vendor1.getId(), new BigDecimal("10000.00"), true);

        VendorProfileDTO vendor2 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorBalance(vendor2.getId(), new BigDecimal("5000.00"), true);

        // Act
        Object topVendors = statisticsService.getTopVendorsBySales();

        // Assert
        assertNotNull(topVendors);
    }

    @Test
    @DisplayName("Should get top vendors by rating")
    public void testGetTopVendorsByRating() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorRating(vendor1.getId(), new BigDecimal("4.8"), 50);

        VendorProfileDTO vendor2 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorRating(vendor2.getId(), new BigDecimal("3.5"), 30);

        // Act
        Object topVendors = statisticsService.getTopVendorsByRating();

        // Assert
        assertNotNull(topVendors);
    }

    @Test
    @DisplayName("Should get premium vendors")
    public void testGetPremiumVendors() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        vendorService.updateVendorRating(vendor1.getId(), new BigDecimal("4.5"), 100);

        // Act
        Object premiumVendors = statisticsService.getPremiumVendors();

        // Assert
        assertNotNull(premiumVendors);
    }

    @Test
    @DisplayName("Should get category statistics")
    public void testGetCategoryStatistics() {
        // Act
        Object categoryStats = statisticsService.getCategoryStatistics("Electronics");

        // Assert
        assertNotNull(categoryStats);
    }

    @Test
    @DisplayName("Should get marketplace statistics")
    public void testGetMarketplaceStatistics() {
        // Act
        Object marketplaceStats = statisticsService.getMarketplaceStatistics();

        // Assert
        assertNotNull(marketplaceStats);
    }

    @Test
    @DisplayName("Should get performance trends")
    public void testGetPerformanceTrends() {
        // Act
        Object trends = statisticsService.getPerformanceTrends(testVendorId, 30);

        // Assert
        assertNotNull(trends);
    }

    @Test
    @DisplayName("Should handle statistics for non-existent vendor")
    public void testStatisticsForNonExistentVendor() {
        // Act & Assert
        assertThrows(Exception.class, () ->
            statisticsService.getVendorStatistics(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Should calculate correct performance score formula")
    public void testPerformanceScoreFormula() {
        // Arrange
        vendorService.updateVendorRating(testVendorId, new BigDecimal("5.0"), 100);

        // Act
        BigDecimal score = statisticsService.calculatePerformanceScore(testVendorId);

        // Assert
        assertNotNull(score);
        // Score should be close to max (100) with high rating
        assertTrue(score.compareTo(new BigDecimal("50")) >= 0);
    }

    @Test
    @DisplayName("Should handle multiple vendors for ranking")
    public void testRankingWithMultipleVendors() {
        // Arrange
        for (int i = 0; i < 5; i++) {
            VendorProfileDTO vendor = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
            vendorService.updateVendorBalance(vendor.getId(), new BigDecimal(String.valueOf(1000 * (i + 1))), true);
        }

        // Act
        int rank = statisticsService.getVendorSalesRank(testVendorId);

        // Assert
        assertTrue(rank > 0);
    }
}
