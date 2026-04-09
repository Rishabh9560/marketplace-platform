package com.logicveda.marketplace.vendor.config;

import com.logicveda.marketplace.vendor.entity.VendorProfile;
import com.logicveda.marketplace.vendor.entity.ProductListing;
import com.logicveda.marketplace.vendor.entity.VendorPayoutRecord;
import com.logicveda.marketplace.vendor.dto.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test configuration for integration tests
 */
@TestConfiguration
public class TestDataConfig {

    /**
     * Create test vendor profile entity
     */
    public static VendorProfile createTestVendorProfile() {
        VendorProfile vendor = new VendorProfile();
        vendor.setId(UUID.randomUUID());
        vendor.setUserId(UUID.randomUUID());
        vendor.setBusinessName("Test Vendor Corp");
        vendor.setBusinessEmail("test@vendor.com");
        vendor.setBusinessPhone("+1234567890");
        vendor.setTaxId("TAX123456");
        vendor.setBusinessLicenseNumber("LICENSE123");
        vendor.setBankAccountNumber("BANK123456");
        vendor.setCommissionRate(new BigDecimal("15.00"));
        vendor.setKycStatus(VendorProfile.KYCStatus.VERIFIED);
        vendor.setIsActive(true);
        vendor.setIsSuspended(false);
        vendor.setTotalEarnings(BigDecimal.ZERO);
        vendor.setAvailableBalance(BigDecimal.ZERO);
        vendor.setAverageRating(BigDecimal.ZERO);
        vendor.setTotalReviews(0);
        vendor.setTotalSales(0L);
        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());
        return vendor;
    }

    /**
     * Create test vendor profile DTO
     */
    public static VendorProfileDTO createTestVendorProfileDTO() {
        VendorProfileDTO dto = new VendorProfileDTO();
        dto.setId(UUID.randomUUID());
        dto.setUserId(UUID.randomUUID());
        dto.setBusinessName("Test Vendor Corp");
        dto.setBusinessEmail("test@vendor.com");
        dto.setBusinessPhone("+1234567890");
        dto.setTaxId("TAX123456");
        dto.setBusinessLicenseNumber("LICENSE123");
        dto.setBankAccountNumber("BANK123456");
        dto.setCommissionRate(new BigDecimal("15.00"));
        dto.setKycStatus("VERIFIED");
        dto.setIsActive(true);
        dto.setIsSuspended(false);
        dto.setTotalEarnings(BigDecimal.ZERO);
        dto.setAvailableBalance(BigDecimal.ZERO);
        dto.setAverageRating(BigDecimal.ZERO);
        return dto;
    }

    /**
     * Create test product listing entity
     */
    public static ProductListing createTestProductListing(UUID vendorId) {
        ProductListing listing = new ProductListing();
        listing.setId(UUID.randomUUID());
        listing.setVendorId(vendorId);
        listing.setProductId(UUID.randomUUID());
        listing.setProductName("Test Product");
        listing.setSku("SKU" + System.nanoTime());
        listing.setDescription("Test product description");
        listing.setVendorPrice(new BigDecimal("99.99"));
        listing.setDiscountPercentage(BigDecimal.ZERO);
        listing.setShippingCost(new BigDecimal("5.00"));
        listing.setQuantityAvailable(100);
        listing.setQuantityReserved(0);
        listing.setReorderLevel(10);
        listing.setReorderQuantity(50);
        listing.setStatus(ProductListing.Status.DRAFT);
        listing.setViewCount(0);
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());
        return listing;
    }

    /**
     * Create test product listing DTO
     */
    public static ProductListingDTO createTestProductListingDTO(UUID vendorId) {
        ProductListingDTO dto = new ProductListingDTO();
        dto.setId(UUID.randomUUID());
        dto.setVendorId(vendorId);
        dto.setProductId(UUID.randomUUID());
        dto.setProductName("Test Product");
        dto.setSku("SKU" + System.nanoTime());
        dto.setDescription("Test product description");
        dto.setVendorPrice(new BigDecimal("99.99"));
        dto.setDiscountPercentage(BigDecimal.ZERO);
        dto.setShippingCost(new BigDecimal("5.00"));
        dto.setQuantityAvailable(100);
        dto.setStatus("DRAFT");
        return dto;
    }

    /**
     * Create test payout record entity
     */
    public static VendorPayoutRecord createTestPayoutRecord(UUID vendorId) {
        VendorPayoutRecord payout = new VendorPayoutRecord();
        payout.setId(UUID.randomUUID());
        payout.setVendorId(vendorId);
        payout.setPayoutPeriod("202401");
        payout.setTotalSalesAmount(new BigDecimal("10000.00"));
        payout.setCommissionRate(new BigDecimal("15.00"));
        payout.setCommissionDeducted(new BigDecimal("1500.00"));
        payout.setNetPayoutAmount(new BigDecimal("8500.00"));
        payout.setStatus(VendorPayoutRecord.Status.PENDING);
        payout.setRetryCount(0);
        payout.setCreatedAt(LocalDateTime.now());
        payout.setUpdatedAt(LocalDateTime.now());
        return payout;
    }

    /**
     * Create test payout record DTO
     */
    public static VendorPayoutRecordDTO createTestPayoutRecordDTO(UUID vendorId) {
        VendorPayoutRecordDTO dto = new VendorPayoutRecordDTO();
        dto.setId(UUID.randomUUID());
        dto.setVendorId(vendorId);
        dto.setPayoutPeriod("202401");
        dto.setTotalSalesAmount(new BigDecimal("10000.00"));
        dto.setCommissionRate(new BigDecimal("15.00"));
        dto.setCommissionDeducted(new BigDecimal("1500.00"));
        dto.setNetPayoutAmount(new BigDecimal("8500.00"));
        dto.setStatus("PENDING");
        return dto;
    }

    /**
     * Create test KYC submission request
     */
    public static KYCSubmissionRequestDTO createTestKYCSubmissionRequest() {
        KYCSubmissionRequestDTO dto = new KYCSubmissionRequestDTO();
        dto.setVendorId(UUID.randomUUID());
        dto.setBusinessLicenseNumber("LICENSE123");
        dto.setTaxId("TAX123456");
        dto.setBankAccountNumber("BANK123456");
        return dto;
    }

    /**
     * Create test inventory update DTO
     */
    public static InventoryUpdateDTO createTestInventoryUpdateDTO() {
        InventoryUpdateDTO dto = new InventoryUpdateDTO();
        dto.setQuantityAvailable(100);
        dto.setReorderLevel(10);
        dto.setReorderQuantity(50);
        return dto;
    }

    /**
     * Create test price update DTO
     */
    public static PriceUpdateDTO createTestPriceUpdateDTO() {
        PriceUpdateDTO dto = new PriceUpdateDTO();
        dto.setVendorPrice(new BigDecimal("99.99"));
        dto.setDiscountPercentage(new BigDecimal("10.00"));
        dto.setShippingCost(new BigDecimal("5.00"));
        return dto;
    }

    /**
     * Create test update vendor profile DTO
     */
    public static UpdateVendorProfileDTO createTestUpdateVendorProfileDTO() {
        UpdateVendorProfileDTO dto = new UpdateVendorProfileDTO();
        dto.setBusinessPhone("+9876543210");
        dto.setTaxId("TAX999999");
        return dto;
    }
}
