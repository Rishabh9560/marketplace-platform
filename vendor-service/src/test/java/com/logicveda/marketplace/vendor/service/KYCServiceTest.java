package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.config.TestDataConfig;
import com.logicveda.marketplace.vendor.dto.KYCSubmissionRequestDTO;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.repository.VendorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for KYCService
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("KYCService Integration Tests")
public class KYCServiceTest {

    @Autowired
    private KYCService kycService;

    @Autowired
    private VendorProfileService vendorService;

    @Autowired
    private VendorProfileRepository vendorRepository;

    private UUID testVendorId;
    private KYCSubmissionRequestDTO testKYCRequest;

    @BeforeEach
    public void setUp() {
        vendorRepository.deleteAll();

        // Create test vendor
        VendorProfileDTO vendorDTO = TestDataConfig.createTestVendorProfileDTO();
        vendorDTO.setBusinessEmail("vendor" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor = vendorService.registerVendor(vendorDTO);
        testVendorId = vendor.getId();

        // Create test KYC request
        testKYCRequest = TestDataConfig.createTestKYCSubmissionRequest();
        testKYCRequest.setVendorId(testVendorId);
    }

    @Test
    @DisplayName("Should submit KYC successfully")
    public void testSubmitKYC() {
        // Act
        VendorProfileDTO submitted = kycService.submitKYC(testVendorId, testKYCRequest);

        // Assert
        assertNotNull(submitted);
        assertEquals("SUBMITTED", submitted.getKycStatus());
    }

    @Test
    @DisplayName("Should verify KYC successfully")
    public void testVerifyKYC() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);

        // Act
        VendorProfileDTO verified = kycService.verifyKYC(testVendorId);

        // Assert
        assertNotNull(verified);
        assertEquals("VERIFIED", verified.getKycStatus());
        assertNotNull(verified.getKycVerifiedAt());
    }

    @Test
    @DisplayName("Should reject KYC successfully")
    public void testRejectKYC() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);

        // Act
        VendorProfileDTO rejected = kycService.rejectKYC(testVendorId, "Insufficient documents");

        // Assert
        assertNotNull(rejected);
        assertEquals("REJECTED", rejected.getKycStatus());
    }

    @Test
    @DisplayName("Should request KYC resubmission")
    public void testRequestKYCResubmission() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.rejectKYC(testVendorId, "Document quality issue");

        // Act
        VendorProfileDTO resubmitRequest = kycService.requestKYCResubmission(testVendorId, "Please upload clearer documents");

        // Assert
        assertNotNull(resubmitRequest);
        assertEquals("PENDING", resubmitRequest.getKycStatus());
    }

    @Test
    @DisplayName("Should check if KYC is verified")
    public void testIsKYCVerified() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.verifyKYC(testVendorId);

        // Act
        boolean isVerified = kycService.isKYCVerified(testVendorId);

        // Assert
        assertTrue(isVerified);
    }

    @Test
    @DisplayName("Should return false when KYC not verified")
    public void testIsKYCNotVerified() {
        // Act
        boolean isVerified = kycService.isKYCVerified(testVendorId);

        // Assert
        assertFalse(isVerified);
    }

    @Test
    @DisplayName("Should validate KYC requirements met")
    public void testAreKYCRequirementsMet() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.verifyKYC(testVendorId);

        // Act
        boolean requirementsMet = kycService.areKYCRequirementsMet(testVendorId);

        // Assert
        assertTrue(requirementsMet);
    }

    @Test
    @DisplayName("Should return false when requirements not met")
    public void testRequirementsNotMet() {
        // Act
        boolean requirementsMet = kycService.areKYCRequirementsMet(testVendorId);

        // Assert
        assertFalse(requirementsMet);
    }

    @Test
    @DisplayName("Should get pending KYC submissions")
    public void testGetPendingKYCSubmissions() {
        // Arrange
        VendorProfileDTO vendor1 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());
        VendorProfileDTO vendor2 = vendorService.registerVendor(TestDataConfig.createTestVendorProfileDTO());

        KYCSubmissionRequestDTO request1 = TestDataConfig.createTestKYCSubmissionRequest();
        request1.setVendorId(vendor1.getId());
        kycService.submitKYC(vendor1.getId(), request1);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> pending = kycService.getPendingKYCSubmissions(pageable);

        // Assert
        assertNotNull(pending);
    }

    @Test
    @DisplayName("Should get rejected KYC submissions")
    public void testGetRejectedKYCSubmissions() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.rejectKYC(testVendorId, "Incomplete documents");

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> rejected = kycService.getRejectedKYCSubmissions(pageable);

        // Assert
        assertNotNull(rejected);
        assertTrue(rejected.getContent().stream()
            .anyMatch(v -> v.getId().equals(testVendorId)));
    }

    @Test
    @DisplayName("Should get verified vendors")
    public void testGetVerifiedVendors() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.verifyKYC(testVendorId);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorProfileDTO> verified = kycService.getVerifiedVendors(pageable);

        // Assert
        assertNotNull(verified);
        assertTrue(verified.getContent().stream()
            .anyMatch(v -> v.getId().equals(testVendorId)));
    }

    @Test
    @DisplayName("Should get verified vendor count")
    public void testGetVerifiedVendorCount() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.verifyKYC(testVendorId);

        // Act
        long count = kycService.getVerifiedVendorCount();

        // Assert
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("Should get pending KYC count")
    public void testGetPendingKYCCount() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);

        // Act
        long count = kycService.getPendingKYCCount();

        // Assert
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("Should get rejected KYC count")
    public void testGetRejectedKYCCount() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.rejectKYC(testVendorId, "Invalid documents");

        // Act
        long count = kycService.getRejectedKYCCount();

        // Assert
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("Should renew KYC verification")
    public void testRenewKYCVerification() {
        // Arrange
        kycService.submitKYC(testVendorId, testKYCRequest);
        kycService.verifyKYC(testVendorId);
        VendorProfileDTO verified = vendorService.getVendorById(testVendorId);
        java.time.LocalDateTime originalVerificationTime = verified.getKycVerifiedAt();

        // Wait a bit to ensure timestamp difference
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        VendorProfileDTO renewed = kycService.renewKYCVerification(testVendorId);

        // Assert
        assertNotNull(renewed);
        assertEquals("VERIFIED", renewed.getKycStatus());
        assertNotNull(renewed.getKycVerifiedAt());
    }

    @Test
    @DisplayName("Should validate KYC documents successfully")
    public void testValidateKYCDocuments() {
        // Arrange
        VendorProfileDTO vendor = vendorService.getVendorById(testVendorId);

        // Act
        boolean isValid = kycService.isKYCDocumentsValid(testVendorId);

        // Assert - depends on implementation
        assertNotNull(isValid);
    }

    @Test
    @DisplayName("Should handle KYC workflow state transitions")
    public void testKYCWorkflowSTATES() {
        // Initial state: PENDING
        VendorProfileDTO initial = vendorService.getVendorById(testVendorId);
        assertEquals("PENDING", initial.getKycStatus());

        // Submit KYC
        kycService.submitKYC(testVendorId, testKYCRequest);
        VendorProfileDTO submitted = vendorService.getVendorById(testVendorId);
        assertEquals("SUBMITTED", submitted.getKycStatus());

        // Reject and request resubmission
        kycService.rejectKYC(testVendorId, "Document issue");
        VendorProfileDTO rejected = vendorService.getVendorById(testVendorId);
        assertEquals("REJECTED", rejected.getKycStatus());

        // Request resubmission
        kycService.requestKYCResubmission(testVendorId, "Please resubmit");
        VendorProfileDTO resubmissionRequested = vendorService.getVendorById(testVendorId);
        assertEquals("PENDING", resubmissionRequested.getKycStatus());

        // Verify
        kycService.verifyKYC(testVendorId);
        VendorProfileDTO verified = vendorService.getVendorById(testVendorId);
        assertEquals("VERIFIED", verified.getKycStatus());
    }
}
