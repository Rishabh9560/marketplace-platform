package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.config.TestDataConfig;
import com.logicveda.marketplace.vendor.dto.UpdateVendorProfileDTO;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.entity.VendorProfile;
import com.logicveda.marketplace.vendor.exception.VendorException;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for VendorProfileService
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("VendorProfileService Integration Tests")
public class VendorProfileServiceTest {

    @Autowired
    private VendorProfileService vendorService;

    @Autowired
    private VendorProfileRepository vendorRepository;

    private VendorProfileDTO testVendorDTO;
    private UUID testVendorId;

    @BeforeEach
    public void setUp() {
        vendorRepository.deleteAll();
        testVendorDTO = TestDataConfig.createTestVendorProfileDTO();
        testVendorDTO.setBusinessEmail("vendor" + UUID.randomUUID() + "@test.com");
    }

    @Test
    @DisplayName("Should register vendor successfully")
    public void testRegisterVendor() {
        // Act
        VendorProfileDTO registeredVendor = vendorService.registerVendor(testVendorDTO);

        // Assert
        assertNotNull(registeredVendor);
        assertNotNull(registeredVendor.getId());
        assertEquals(testVendorDTO.getBusinessName(), registeredVendor.getBusinessName());
        assertEquals("PENDING", registeredVendor.getKycStatus());
        testVendorId = registeredVendor.getId();
    }

    @Test
    @DisplayName("Should retrieve vendor by ID")
    public void testGetVendorById() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        // Act
        VendorProfileDTO retrieved = vendorService.getVendorById(testVendorId);

        // Assert
        assertNotNull(retrieved);
        assertEquals(testVendorId, retrieved.getId());
        assertEquals(testVendorDTO.getBusinessName(), retrieved.getBusinessName());
    }

    @Test
    @DisplayName("Should throw exception when vendor not found")
    public void testGetVendorByIdNotFound() {
        // Act & Assert
        assertThrows(VendorException.class, () -> 
            vendorService.getVendorById(UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("Should retrieve vendor by user ID")
    public void testGetVendorByUserId() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        UUID userId = registered.getUserId();

        // Act
        VendorProfileDTO retrieved = vendorService.getVendorByUserId(userId);

        // Assert
        assertNotNull(retrieved);
        assertEquals(userId, retrieved.getUserId());
    }

    @Test
    @DisplayName("Should update vendor successfully")
    public void testUpdateVendor() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        UpdateVendorProfileDTO updateDTO = TestDataConfig.createTestUpdateVendorProfileDTO();

        // Act
        VendorProfileDTO updated = vendorService.updateVendor(testVendorId, updateDTO);

        // Assert
        assertNotNull(updated);
        assertEquals(updateDTO.getBusinessPhone(), updated.getBusinessPhone());
    }

    @Test
    @DisplayName("Should verify KYC for vendor")
    public void testVerifyKYC() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        // Act
        VendorProfileDTO verified = vendorService.verifyKYC(testVendorId);

        // Assert
        assertNotNull(verified);
        assertEquals("VERIFIED", verified.getKycStatus());
    }

    @Test
    @DisplayName("Should suspend vendor")
    public void testSuspendVendor() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        // Act
        VendorProfileDTO suspended = vendorService.suspendVendor(testVendorId, "Policy violation");

        // Assert
        assertNotNull(suspended);
        assertTrue(suspended.getIsSuspended());
    }

    @Test
    @DisplayName("Should unsuspend vendor")
    public void testUnsuspendVendor() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();
        vendorService.suspendVendor(testVendorId, "Temporary suspension");

        // Act
        VendorProfileDTO unsuspended = vendorService.unsuspendVendor(testVendorId);

        // Assert
        assertNotNull(unsuspended);
        assertFalse(unsuspended.getIsSuspended());
    }

    @Test
    @DisplayName("Should get high-rated vendors")
    public void testGetHighRatedVendors() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(testVendorDTO);
        vendorService.updateVendorRating(vendor1.getId(), new BigDecimal("4.5"), 10);

        testVendorDTO.setBusinessEmail("vendor2" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor2 = vendorService.registerVendor(testVendorDTO);
        vendorService.updateVendorRating(vendor2.getId(), new BigDecimal("3.0"), 5);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> highRated = vendorService.getHighRatedVendors(new BigDecimal("4.0"), pageable);

        // Assert
        assertNotNull(highRated);
        assertTrue(highRated.getContent().stream()
            .anyMatch(v -> v.getId().equals(vendor1.getId())));
    }

    @Test
    @DisplayName("Should update vendor balance")
    public void testUpdateVendorBalance() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();
        BigDecimal amount = new BigDecimal("1000.00");

        // Act
        vendorService.updateVendorBalance(testVendorId, amount, true);
        VendorProfileDTO updated = vendorService.getVendorById(testVendorId);

        // Assert
        assertNotNull(updated);
        assertEquals(amount, updated.getAvailableBalance());
    }

    @Test
    @DisplayName("Should update vendor rating")
    public void testUpdateVendorRating() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        // Act
        vendorService.updateVendorRating(testVendorId, new BigDecimal("4.5"), 50);
        VendorProfileDTO updated = vendorService.getVendorById(testVendorId);

        // Assert
        assertNotNull(updated);
        assertEquals(new BigDecimal("4.5"), updated.getAverageRating());
        assertEquals(50, updated.getTotalReviews());
    }

    @Test
    @DisplayName("Should check if vendor can sell products")
    public void testCanVendorSellProducts() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();
        vendorService.verifyKYC(testVendorId);

        // Act
        boolean canSell = vendorService.canVendorSellProducts(testVendorId);

        // Assert
        assertTrue(canSell);
    }

    @Test
    @DisplayName("Should return false when vendor not KYC verified")
    public void testCannotSellWhenNotKYCVerified() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();

        // Act
        boolean canSell = vendorService.canVendorSellProducts(testVendorId);

        // Assert
        assertFalse(canSell);
    }

    @Test
    @DisplayName("Should return false when vendor is suspended")
    public void testCannotSellWhenSuspended() {
        // Arrange
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);
        testVendorId = registered.getId();
        vendorService.verifyKYC(testVendorId);
        vendorService.suspendVendor(testVendorId, "Test suspension");

        // Act
        boolean canSell = vendorService.canVendorSellProducts(testVendorId);

        // Assert
        assertFalse(canSell);
    }

    @Test
    @DisplayName("Should get active vendors with pagination")
    public void testGetActiveVendors() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(testVendorDTO);
        
        testVendorDTO.setBusinessEmail("vendor3" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor2 = vendorService.registerVendor(testVendorDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> activeVendors = vendorService.getActiveVendors(pageable);

        // Assert
        assertNotNull(activeVendors);
        assertTrue(activeVendors.getContent().size() >= 2);
    }

    @Test
    @DisplayName("Should search vendors by name")
    public void testSearchVendorsByName() {
        // Arrange
        testVendorDTO.setBusinessName("Unique Test Vendor");
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> results = vendorService.searchVendorsByName("Unique", pageable);

        // Assert
        assertNotNull(results);
        assertTrue(results.getContent().stream()
            .anyMatch(v -> v.getId().equals(registered.getId())));
    }

    @Test
    @DisplayName("Should get top earning vendors")
    public void testGetTopEarningVendors() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(testVendorDTO);
        vendorService.updateVendorBalance(vendor1.getId(), new BigDecimal("5000.00"), true);

        testVendorDTO.setBusinessEmail("vendor4" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor2 = vendorService.registerVendor(testVendorDTO);
        vendorService.updateVendorBalance(vendor2.getId(), new BigDecimal("2000.00"), true);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> topEarners = vendorService.getTopEarningVendors(pageable);

        // Assert
        assertNotNull(topEarners);
        assertNotNull(topEarners.getContent());
    }

    @Test
    @DisplayName("Should get vendors by city")
    public void testGetVendorsByCity() {
        // Arrange
        testVendorDTO.setCity("New York");
        VendorProfileDTO registered = vendorService.registerVendor(testVendorDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> vendors = vendorService.getVendorsByCity("New York", pageable);

        // Assert
        assertNotNull(vendors);
        assertTrue(vendors.getContent().stream()
            .anyMatch(v -> v.getId().equals(registered.getId())));
    }

    @Test
    @DisplayName("Should get total vendor count")
    public void testGetTotalVendorCount() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(testVendorDTO);
        
        testVendorDTO.setBusinessEmail("vendor5" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor2 = vendorService.registerVendor(testVendorDTO);

        // Act
        long count = vendorService.getTotalVendorCount();

        // Assert
        assertTrue(count >= 2);
    }

    @Test
    @DisplayName("Should throw exception on duplicate email")
    public void testRegisterVendorDuplicateEmail() {
        // Arrange
        vendorService.registerVendor(testVendorDTO);

        VendorProfileDTO duplicateDTO = new VendorProfileDTO();
        duplicateDTO.setBusinessEmail(testVendorDTO.getBusinessEmail());
        duplicateDTO.setBusinessName("Different Name");

        // Act & Assert
        assertThrows(Exception.class, () -> 
            vendorService.registerVendor(duplicateDTO)
        );
    }
}
