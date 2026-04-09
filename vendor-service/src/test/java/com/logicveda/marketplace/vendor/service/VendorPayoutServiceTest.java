package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.config.TestDataConfig;
import com.logicveda.marketplace.vendor.dto.VendorPayoutRecordDTO;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.repository.VendorPayoutRecordRepository;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for VendorPayoutService
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("VendorPayoutService Integration Tests")
public class VendorPayoutServiceTest {

    @Autowired
    private VendorPayoutService payoutService;

    @Autowired
    private VendorProfileService vendorService;

    @Autowired
    private VendorPayoutRecordRepository payoutRepository;

    @Autowired
    private VendorProfileRepository vendorRepository;

    private UUID testVendorId;
    private VendorPayoutRecordDTO testPayoutDTO;

    @BeforeEach
    public void setUp() {
        payoutRepository.deleteAll();
        vendorRepository.deleteAll();

        // Create test vendor
        VendorProfileDTO vendorDTO = TestDataConfig.createTestVendorProfileDTO();
        vendorDTO.setBusinessEmail("vendor" + UUID.randomUUID() + "@test.com");
        VendorProfileDTO vendor = vendorService.registerVendor(vendorDTO);
        testVendorId = vendor.getId();

        // Create test payout
        testPayoutDTO = TestDataConfig.createTestPayoutRecordDTO(testVendorId);
    }

    @Test
    @DisplayName("Should create payout record successfully")
    public void testCreatePayoutRecord() {
        // Act
        VendorPayoutRecordDTO createdPayout = payoutService.createPayoutRecord(testPayoutDTO);

        // Assert
        assertNotNull(createdPayout);
        assertNotNull(createdPayout.getId());
        assertEquals(testVendorId, createdPayout.getVendorId());
        assertEquals("PENDING", createdPayout.getStatus());
    }

    @Test
    @DisplayName("Should retrieve payout by ID")
    public void testGetPayoutById() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);

        // Act
        VendorPayoutRecordDTO retrieved = payoutService.getPayoutById(created.getId());

        // Assert
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(testVendorId, retrieved.getVendorId());
    }

    @Test
    @DisplayName("Should schedule payout successfully")
    public void testSchedulePayout() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);

        // Act
        VendorPayoutRecordDTO scheduled = payoutService.schedulePayout(created.getId());

        // Assert
        assertNotNull(scheduled);
        assertEquals("SCHEDULED", scheduled.getStatus());
        assertNotNull(scheduled.getScheduledPayoutDate());
    }

    @Test
    @DisplayName("Should process payout successfully")
    public void testProcessPayout() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(created.getId());

        // Act
        VendorPayoutRecordDTO processed = payoutService.processPayout(created.getId());

        // Assert
        assertNotNull(processed);
        assertEquals("COMPLETED", processed.getStatus());
        assertNotNull(processed.getTransactionId());
        assertNotNull(processed.getActualPayoutDate());
    }

    @Test
    @DisplayName("Should mark payout as failed")
    public void testMarkPayoutAsFailed() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(created.getId());

        // Act
        VendorPayoutRecordDTO failed = payoutService.markPayoutAsFailed(created.getId(), "Bank Error");

        // Assert
        assertNotNull(failed);
        assertEquals("ON_HOLD", failed.getStatus());
        assertTrue(failed.getRetryCount() > 0);
    }

    @Test
    @DisplayName("Should retry failed payout")
    public void testRetryFailedPayout() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(created.getId());
        payoutService.markPayoutAsFailed(created.getId(), "First attempt failed");

        // Act
        VendorPayoutRecordDTO retried = payoutService.retryFailedPayout(created.getId());

        // Assert
        assertNotNull(retried);
        assertEquals("PENDING", retried.getStatus());
    }

    @Test
    @DisplayName("Should get vendor payouts with pagination")
    public void testGetVendorPayouts() {
        // Arrange
        VendorPayoutRecordDTO payout1 = payoutService.createPayoutRecord(testPayoutDTO);

        VendorPayoutRecordDTO payout2 = TestDataConfig.createTestPayoutRecordDTO(testVendorId);
        payout2.setPayoutPeriod("202402");
        payoutService.createPayoutRecord(payout2);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorPayoutRecordDTO> payouts = payoutService.getVendorPayouts(testVendorId, pageable);

        // Assert
        assertNotNull(payouts);
        assertTrue(payouts.getContent().size() >= 2);
    }

    @Test
    @DisplayName("Should get pending payouts")
    public void testGetPendingPayouts() {
        // Arrange
        VendorPayoutRecordDTO pendingPayout = payoutService.createPayoutRecord(testPayoutDTO);

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorPayoutRecordDTO> pending = payoutService.getPendingPayouts(pageable);

        // Assert
        assertNotNull(pending);
        assertTrue(pending.getContent().stream()
            .anyMatch(p -> p.getId().equals(pendingPayout.getId())));
    }

    @Test
    @DisplayName("Should get scheduled payouts")
    public void testGetScheduledPayouts() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(created.getId());

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorPayoutRecordDTO> scheduled = payoutService.getScheduledPayouts(pageable);

        // Assert
        assertNotNull(scheduled);
        assertTrue(scheduled.getContent().stream()
            .anyMatch(p -> p.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Should get completed payouts")
    public void testGetCompletedPayouts() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(created.getId());
        payoutService.processPayout(created.getId());

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorPayoutRecordDTO> completed = payoutService.getCompletedPayouts(pageable);

        // Assert
        assertNotNull(completed);
        assertTrue(completed.getContent().stream()
            .anyMatch(p -> p.getId().equals(created.getId())));
    }

    @Test
    @DisplayName("Should get failed payouts")
    public void testGetFailedPayouts() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(created.getId());
        payoutService.markPayoutAsFailed(created.getId(), "Bank Error");

        Pageable pageable = PageRequest.of(0, 20);

        // Act
        Page<VendorPayoutRecordDTO> failed = payoutService.getFailedPayouts(pageable);

        // Assert
        assertNotNull(failed);
    }

    @Test
    @DisplayName("Should get payout summary for vendor")
    public void testGetPayoutSummary() {
        // Arrange
        VendorPayoutRecordDTO payout1 = payoutService.createPayoutRecord(testPayoutDTO);
        
        VendorPayoutRecordDTO payout2 = TestDataConfig.createTestPayoutRecordDTO(testVendorId);
        payout2.setPayoutPeriod("202402");
        payoutService.createPayoutRecord(payout2);
        payoutService.schedulePayout(payout2.getId());

        // Act
        Object summary = payoutService.getPayoutSummary(testVendorId);

        // Assert
        assertNotNull(summary);
    }

    @Test
    @DisplayName("Should get total pending payout amount")
    public void testGetTotalPendingPayoutAmount() {
        // Arrange
        VendorPayoutRecordDTO payout1 = payoutService.createPayoutRecord(testPayoutDTO);

        VendorPayoutRecordDTO payout2 = TestDataConfig.createTestPayoutRecordDTO(testVendorId);
        payout2.setPayoutPeriod("202402");
        payoutService.createPayoutRecord(payout2);

        // Act
        BigDecimal totalPending = payoutService.getTotalPendingPayoutAmount(testVendorId);

        // Assert
        assertNotNull(totalPending);
        assertTrue(totalPending.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should calculate total commission collected")
    public void testGetTotalCommissionCollected() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);

        // Act
        BigDecimal totalCommission = payoutService.getTotalCommissionCollected("202401", "202401");

        // Assert
        assertNotNull(totalCommission);
    }

    @Test
    @DisplayName("Should process batch payouts asynchronously")
    public void testProcessBatchPayouts() {
        // Arrange
        VendorPayoutRecordDTO payout1 = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(payout1.getId());

        VendorPayoutRecordDTO payout2 = TestDataConfig.createTestPayoutRecordDTO(testVendorId);
        payout2.setPayoutPeriod("202402");
        VendorPayoutRecordDTO created2 = payoutService.createPayoutRecord(payout2);
        payoutService.schedulePayout(created2.getId());

        // Act & Assert - Just verify it doesn't throw exception
        assertDoesNotThrow(() -> payoutService.processBatchPayouts());
    }

    @Test
    @DisplayName("Should handle duplicate payout period")
    public void testDuplicatePayoutPeriod() {
        // Arrange
        payoutService.createPayoutRecord(testPayoutDTO);

        VendorPayoutRecordDTO duplicateDTO = TestDataConfig.createTestPayoutRecordDTO(testVendorId);
        duplicateDTO.setPayoutPeriod("202401");

        // Act & Assert
        assertThrows(Exception.class, () -> 
            payoutService.createPayoutRecord(duplicateDTO)
        );
    }

    @Test
    @DisplayName("Should get total pending payout count")
    public void testGetTotalPendingPayoutCount() {
        // Arrange
        VendorPayoutRecordDTO payout1 = payoutService.createPayoutRecord(testPayoutDTO);

        VendorPayoutRecordDTO payout2 = TestDataConfig.createTestPayoutRecordDTO(testVendorId);
        payout2.setPayoutPeriod("202402");
        payoutService.createPayoutRecord(payout2);

        // Act
        long count = payoutService.getTotalPendingPayoutCount();

        // Assert
        assertTrue(count >= 2);
    }

    @Test
    @DisplayName("Should get total completed payout count")
    public void testGetTotalCompletedPayoutCount() {
        // Arrange
        VendorPayoutRecordDTO created = payoutService.createPayoutRecord(testPayoutDTO);
        payoutService.schedulePayout(created.getId());
        payoutService.processPayout(created.getId());

        // Act
        long count = payoutService.getTotalCompletedPayoutCount();

        // Assert
        assertTrue(count >= 1);
    }
}
