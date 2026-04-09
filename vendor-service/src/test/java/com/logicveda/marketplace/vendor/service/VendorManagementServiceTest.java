package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.config.TestDataConfig;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
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
 * Integration tests for VendorManagementService
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("VendorManagementService Integration Tests")
public class VendorManagementServiceTest {

    @Autowired
    private VendorManagementService managementService;

    @Autowired
    private VendorProfileService vendorService;

    @Autowired
    private KYCService kycService;

    @Autowired
    private VendorPayoutService payoutService;

    @Autowired
    private VendorProfileRepository vendorRepository;

    private VendorProfileDTO testVendorDTO;

    @BeforeEach
    public void setUp() {
        vendorRepository.deleteAll();
        testVendorDTO = TestDataConfig.createTestVendorProfileDTO();
        testVendorDTO.setBusinessEmail("vendor" + UUID.randomUUID() + "@test.com");
    }

    @Test
    @DisplayName("Should register and setup vendor")
    public void testRegisterAndSetupVendor() {
        // Act
        VendorProfileDTO result = managementService.registerAndSetupVendor(testVendorDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testVendorDTO.getBusinessName(), result.getBusinessName());
    }

    @Test
    @DisplayName("Should complete vendor onboarding")
    public void testCompleteVendorOnboarding() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);

        // Act
        VendorProfileDTO result = managementService.completeVendorOnboarding(registered.getId());

        // Assert
        assertNotNull(result);
        assertEquals(registered.getId(), result.getId());
    }

    @Test
    @DisplayName("Should verify vendor and enable selling")
    public void testVerifyVendorAndEnableSelling() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);

        // Act
        VendorProfileDTO result = managementService.verifyVendorAndEnableSelling(registered.getId());

        // Assert
        assertNotNull(result);
        assertEquals("VERIFIED", result.getKycStatus());
        assertTrue(result.getIsActive());
    }

    @Test
    @DisplayName("Should suspend vendor completely")
    public void testSuspendVendorCompletely() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        vendorService.verifyKYC(registered.getId());

        // Act
        VendorProfileDTO result = managementService.suspendVendorCompletely(registered.getId(), "Policy violation");

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsSuspended());
    }

    @Test
    @DisplayName("Should unsuspend vendor completely")
    public void testUnsuspendVendorCompletely() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        vendorService.verifyKYC(registered.getId());
        managementService.suspendVendorCompletely(registered.getId(), "Temporary suspension");

        // Act
        VendorProfileDTO result = managementService.unsuspendVendorCompletely(registered.getId());

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsSuspended());
    }

    @Test
    @DisplayName("Should calculate and create payout")
    public void testCalculateAndCreatePayout() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        UUID vendorId = registered.getId();

        // Act & Assert
        assertDoesNotThrow(() -> 
            managementService.calculateAndCreatePayout(
                vendorId,
                "202401",
                new BigDecimal("10000.00")
            )
        );
    }

    @Test
    @DisplayName("Should get vendor dashboard")
    public void testGetVendorDashboard() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        vendorService.verifyKYC(registered.getId());

        // Act
        Object dashboard = managementService.getVendorDashboard(registered.getId());

        // Assert
        assertNotNull(dashboard);
    }

    @Test
    @DisplayName("Should validate vendor health")
    public void testValidateVendorHealth() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        vendorService.verifyKYC(registered.getId());

        // Act
        Object healthReport = managementService.validateVendorHealth(registered.getId());

        // Assert
        assertNotNull(healthReport);
    }

    @Test
    @DisplayName("Should get onboarding status")
    public void testGetOnboardingStatus() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);

        // Act
        Object status = managementService.getOnboardingStatus(registered.getId());

        // Assert
        assertNotNull(status);
    }

    @Test
    @DisplayName("Should get compliance report")
    public void testGetComplianceReport() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);

        // Act
        Object report = managementService.getComplianceReport(registered.getId());

        // Assert
        assertNotNull(report);
    }

    @Test
    @DisplayName("Should get performance report")
    public void testGetPerformanceReport() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        vendorService.verifyKYC(registered.getId());

        // Act
        Object report = managementService.getPerformanceReport(registered.getId());

        // Assert
        assertNotNull(report);
    }

    @Test
    @DisplayName("Should complete full vendor lifecycle")
    public void testFullVendorLifecycle() {
        // Phase 1: Registration
        VendorProfileDTO registered = managementService.registerAndSetupVendor(testVendorDTO);
        assertNotNull(registered);
        assertEquals("PENDING", registered.getKycStatus());

        // Phase 2: KYC Verification
        VendorProfileDTO verified = managementService.verifyVendorAndEnableSelling(registered.getId());
        assertEquals("VERIFIED", verified.getKycStatus());

        // Phase 3: Dashboard access
        Object dashboard = managementService.getVendorDashboard(registered.getId());
        assertNotNull(dashboard);

        // Phase 4: Create payout
        assertDoesNotThrow(() ->
            managementService.calculateAndCreatePayout(
                registered.getId(),
                "202401",
                new BigDecimal("5000.00")
            )
        );

        // Phase 5: Suspend (if needed)
        VendorProfileDTO suspended = managementService.suspendVendorCompletely(
            registered.getId(),
            "Account under review"
        );
        assertTrue(suspended.getIsSuspended());

        // Phase 6: Unsuspend
        VendorProfileDTO unsuspended = managementService.unsuspendVendorCompletely(registered.getId());
        assertFalse(unsuspended.getIsSuspended());
    }

    @Test
    @DisplayName("Should handle multiple vendor operations")
    public void testMultipleVendorOperations() {
        // Create multiple vendors
        UUID vendor1Id = managementService.registerAndSetupVendor(testVendorDTO).getId();

        testVendorDTO.setBusinessEmail("vendor2" + UUID.randomUUID() + "@test.com");
        UUID vendor2Id = managementService.registerAndSetupVendor(testVendorDTO).getId();

        testVendorDTO.setBusinessEmail("vendor3" + UUID.randomUUID() + "@test.com");
        UUID vendor3Id = managementService.registerAndSetupVendor(testVendorDTO).getId();

        // Verify all vendors
        VendorProfileDTO v1 = managementService.verifyVendorAndEnableSelling(vendor1Id);
        VendorProfileDTO v2 = managementService.verifyVendorAndEnableSelling(vendor2Id);
        VendorProfileDTO v3 = managementService.verifyVendorAndEnableSelling(vendor3Id);

        // Assert all verified
        assertEquals("VERIFIED", v1.getKycStatus());
        assertEquals("VERIFIED", v2.getKycStatus());
        assertEquals("VERIFIED", v3.getKycStatus());
    }

    @Test
    @DisplayName("Should handle vendor state transitions correctly")
    public void testVendorStateTransitions() {
        // Arrange
        VendorProfileDTO registered = managementService.registerAndSetupVendor(testVendorDTO);
        UUID vendorId = registered.getId();

        // State 1: PENDING KYC
        assertEquals("PENDING", registered.getKycStatus());

        // State 2: VERIFIED
        VendorProfileDTO verified = managementService.verifyVendorAndEnableSelling(vendorId);
        assertEquals("VERIFIED", verified.getKycStatus());
        assertFalse(verified.getIsSuspended());

        // State 3: SUSPENDED
        VendorProfileDTO suspended = managementService.suspendVendorCompletely(vendorId, "Review needed");
        assertTrue(suspended.getIsSuspended());

        // State 4: RESTORED
        VendorProfileDTO restored = managementService.unsuspendVendorCompletely(vendorId);
        assertFalse(restored.getIsSuspended());
        assertEquals("VERIFIED", restored.getKycStatus());
    }

    @Test
    @DisplayName("Should throw exception for invalid vendor")
    public void testInvalidVendorOperations() {
        // Act & Assert
        assertThrows(Exception.class, () ->
            managementService.verifyVendorAndEnableSelling(UUID.randomUUID())
        );

        assertThrows(Exception.class, () ->
            managementService.suspendVendorCompletely(UUID.randomUUID(), "Invalid vendor")
        );

        assertThrows(Exception.class, () ->
            managementService.getVendorDashboard(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Should handle payout calculation with commission")
    public void testPayoutCalculationWithCommission() {
        // Arrange
        VendorProfileDTO registered = managementService.registerAndSetupVendor(testVendorDTO);
        UUID vendorId = registered.getId();

        BigDecimal totalSales = new BigDecimal("10000.00");

        // Act
        assertDoesNotThrow(() ->
            managementService.calculateAndCreatePayout(vendorId, "202401", totalSales)
        );

        // Commission should be deducted from sales
    }

    @Test
    @DisplayName("Should provide comprehensive vendor health check")
    public void testComprehensiveHealthCheck() {
        // Arrange
        VendorProfileDTO registered = managementService.registerAndSetupVendor(testVendorDTO);
        vendorService.updateVendorRating(registered.getId(), new BigDecimal("4.5"), 50);

        // Act
        Object healthReport = managementService.validateVendorHealth(registered.getId());

        // Assert
        assertNotNull(healthReport);
    }
}
