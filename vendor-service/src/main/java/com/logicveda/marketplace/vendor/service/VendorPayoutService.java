package com.logicveda.marketplace.vendor.service;

import com.logicveda.marketplace.vendor.constants.VendorConstants;
import com.logicveda.marketplace.vendor.dto.VendorPayoutRecordDTO;
import com.logicveda.marketplace.vendor.dto.PayoutSummaryDTO;
import com.logicveda.marketplace.vendor.entity.VendorPayoutRecord;
import com.logicveda.marketplace.vendor.exception.VendorException;
import com.logicveda.marketplace.vendor.mapper.VendorPayoutRecordMapper;
import com.logicveda.marketplace.vendor.repository.VendorPayoutRecordRepository;
import com.logicveda.marketplace.vendor.util.DateUtils;
import com.logicveda.marketplace.vendor.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for vendor payout management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VendorPayoutService {

    private final VendorPayoutRecordRepository payoutRepository;
    private final VendorPayoutRecordMapper payoutMapper;
    private final VendorProfileService vendorService;

    /**
     * Create payout record
     */
    public VendorPayoutRecordDTO createPayoutRecord(VendorPayoutRecordDTO payoutDTO) {
        log.info("Creating payout record for vendor: {} - Period: {}", 
            payoutDTO.getVendorId(), payoutDTO.getPayoutPeriod());

        // Validate input
        ValidationUtils.validatePayoutPeriod(payoutDTO.getPayoutPeriod());
        ValidationUtils.validateCurrencyAmount(payoutDTO.getTotalSalesAmount());
        ValidationUtils.validateCurrencyAmount(payoutDTO.getCommissionDeducted());
        ValidationUtils.validateCurrencyAmount(payoutDTO.getNetPayoutAmount());

        // Check if payout already exists for this period
        if (payoutRepository.findByVendorIdAndPayoutPeriod(
            payoutDTO.getVendorId(), payoutDTO.getPayoutPeriod()).isPresent()) {
            throw new IllegalArgumentException("Payout already exists for this period");
        }

        VendorPayoutRecord payout = payoutMapper.toEntity(payoutDTO);
        payout.setId(UUID.randomUUID());
        payout.setStatus(VendorPayoutRecord.PayoutStatus.PENDING);
        payout.setRetryCount(0);
        payout.setCreatedAt(LocalDateTime.now());

        VendorPayoutRecord savedPayout = payoutRepository.save(payout);
        log.info("Payout record created: {}", savedPayout.getId());

        return payoutMapper.toDTO(savedPayout);
    }

    /**
     * Get payout by ID
     */
    @Cacheable(value = "payouts-by-id", key = "#payoutId")
    @Transactional(readOnly = true)
    public VendorPayoutRecordDTO getPayoutById(UUID payoutId) {
        log.debug("Fetching payout: {}", payoutId);

        VendorPayoutRecord payout = payoutRepository.findById(payoutId)
            .orElseThrow(() -> VendorException.payoutRecordNotFound(payoutId.toString()));

        return payoutMapper.toDTO(payout);
    }

    /**
     * Get payout by vendor and period
     */
    @Transactional(readOnly = true)
    public VendorPayoutRecordDTO getPayoutByVendorAndPeriod(UUID vendorId, String period) {
        log.debug("Fetching payout for vendor: {} - Period: {}", vendorId, period);

        ValidationUtils.validatePayoutPeriod(period);

        VendorPayoutRecord payout = payoutRepository.findByVendorIdAndPayoutPeriod(vendorId, period)
            .orElseThrow(() -> new IllegalArgumentException("Payout not found for this period"));

        return payoutMapper.toDTO(payout);
    }

    /**
     * Get vendor's payout history
     */
    @Transactional(readOnly = true)
    public Page<VendorPayoutRecordDTO> getVendorPayoutHistory(UUID vendorId, Pageable pageable) {
        log.debug("Fetching payout history for vendor: {}", vendorId);
        return payoutRepository.findByVendorId(vendorId, pageable)
            .map(payoutMapper::toDTO);
    }

    /**
     * Schedule payout for processing
     */
    @CacheEvict(value = "payouts-by-id", key = "#payoutId")
    public VendorPayoutRecordDTO schedulePayout(UUID payoutId, LocalDateTime scheduledDate) {
        log.info("Scheduling payout: {} - Date: {}", payoutId, scheduledDate);

        VendorPayoutRecord payout = payoutRepository.findById(payoutId)
            .orElseThrow(() -> VendorException.payoutRecordNotFound(payoutId.toString()));

        if (payout.getStatus() != VendorPayoutRecord.PayoutStatus.PENDING) {
            throw VendorException.payoutAlreadyProcessed(payoutId.toString());
        }

        payout.setStatus(VendorPayoutRecord.PayoutStatus.SCHEDULED);
        payout.setScheduledPayoutDate(scheduledDate);
        payout.setUpdatedAt(LocalDateTime.now());

        VendorPayoutRecord savedPayout = payoutRepository.save(payout);
        log.info("Payout scheduled: {}", payoutId);

        return payoutMapper.toDTO(savedPayout);
    }

    /**
     * Process payout
     */
    @CacheEvict(value = "payouts-by-id", key = "#payoutId")
    @Async
    public void processPayoutAsync(UUID payoutId) {
        try {
            processPayout(payoutId);
        } catch (Exception e) {
            log.error("Error processing payout: {}", payoutId, e);
        }
    }

    /**
     * Process payout (internal method)
     */
    @Transactional
    public VendorPayoutRecordDTO processPayout(UUID payoutId) {
        log.info("Processing payout: {}", payoutId);

        VendorPayoutRecord payout = payoutRepository.findById(payoutId)
            .orElseThrow(() -> VendorException.payoutRecordNotFound(payoutId.toString()));

        if (payout.getStatus() == VendorPayoutRecord.PayoutStatus.COMPLETED) {
            throw VendorException.payoutAlreadyProcessed(payoutId.toString());
        }

        // Check minimum payout amount (in dollars)
        if (payout.getNetPayoutAmount().compareTo(
            new BigDecimal(VendorConstants.MINIMUM_PAYOUT_AMOUNT).divide(new BigDecimal(100))) < 0) {
            throw new IllegalArgumentException("Payout amount below minimum threshold");
        }

        payout.setStatus(VendorPayoutRecord.PayoutStatus.PROCESSING);
        payout.setUpdatedAt(LocalDateTime.now());
        payoutRepository.save(payout);

        // Simulate payment processing
        try {
            Thread.sleep(1000); // Simulate API delay

            payout.setStatus(VendorPayoutRecord.PayoutStatus.COMPLETED);
            payout.setActualPayoutDate(LocalDateTime.now());
            payout.setTransactionId("TXN-" + System.currentTimeMillis());
            payout.setUpdatedAt(LocalDateTime.now());

            VendorPayoutRecord savedPayout = payoutRepository.save(payout);
            log.info("Payout completed: {} - Transaction: {}", payoutId, savedPayout.getTransactionId());

            return payoutMapper.toDTO(savedPayout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payout.setStatus(VendorPayoutRecord.PayoutStatus.FAILED);
            payout.setFailureReason("Processing interrupted");
            payoutRepository.save(payout);
            throw new RuntimeException("Payout processing interrupted", e);
        }
    }

    /**
     * Mark payout as failed
     */
    @CacheEvict(value = "payouts-by-id", key = "#payoutId")
    public VendorPayoutRecordDTO markPayoutAsFailed(UUID payoutId, String failureReason) {
        log.warn("Marking payout as failed: {} - Reason: {}", payoutId, failureReason);

        VendorPayoutRecord payout = payoutRepository.findById(payoutId)
            .orElseThrow(() -> VendorException.payoutRecordNotFound(payoutId.toString()));

        payout.setStatus(VendorPayoutRecord.PayoutStatus.FAILED);
        payout.setFailureReason(failureReason);
        payout.setRetryCount(payout.getRetryCount() + 1);
        payout.setUpdatedAt(LocalDateTime.now());

        if (payout.getRetryCount() >= VendorConstants.MAX_PAYOUT_RETRY_COUNT) {
            payout.setStatus(VendorPayoutRecord.PayoutStatus.ON_HOLD);
            log.warn("Payout placed on hold due to max retries: {}", payoutId);
        }

        VendorPayoutRecord savedPayout = payoutRepository.save(payout);
        return payoutMapper.toDTO(savedPayout);
    }

    /**
     * Retry failed payout
     */
    @CacheEvict(value = "payouts-by-id", key = "#payoutId")
    public VendorPayoutRecordDTO retryFailedPayout(UUID payoutId) {
        log.info("Retrying failed payout: {}", payoutId);

        VendorPayoutRecord payout = payoutRepository.findById(payoutId)
            .orElseThrow(() -> VendorException.payoutRecordNotFound(payoutId.toString()));

        if (payout.getStatus() != VendorPayoutRecord.PayoutStatus.FAILED) {
            throw new IllegalArgumentException("Can only retry failed payouts");
        }

        if (payout.getRetryCount() >= VendorConstants.MAX_PAYOUT_RETRY_COUNT) {
            throw new IllegalArgumentException("Maximum retry attempts exceeded");
        }

        payout.setStatus(VendorPayoutRecord.PayoutStatus.PENDING);
        payout.setFailureReason(null);
        payout.setUpdatedAt(LocalDateTime.now());

        VendorPayoutRecord savedPayout = payoutRepository.save(payout);
        return payoutMapper.toDTO(savedPayout);
    }

    /**
     * Get pending payouts
     */
    @Transactional(readOnly = true)
    public List<VendorPayoutRecordDTO> getPendingPayouts() {
        log.debug("Fetching pending payouts");
        List<VendorPayoutRecord> payouts = payoutRepository.findAllByStatus(VendorPayoutRecord.PayoutStatus.PENDING);
        return payouts.stream()
            .map(payoutMapper::toDTO)
            .toList();
    }

    /**
     * Get overdue payouts
     */
    @Transactional(readOnly = true)
    public List<VendorPayoutRecordDTO> getOverduePayouts() {
        log.debug("Fetching overdue payouts");
        List<VendorPayoutRecord> payouts = payoutRepository.findOverduePayouts();
        return payouts.stream()
            .map(payoutMapper::toDTO)
            .toList();
    }

    /**
     * Get payouts on hold
     */
    @Transactional(readOnly = true)
    public List<VendorPayoutRecordDTO> getPayoutsOnHold() {
        log.debug("Fetching payouts on hold");
        List<VendorPayoutRecord> payouts = payoutRepository.findPayoutsOnHold();
        return payouts.stream()
            .map(payoutMapper::toDTO)
            .toList();
    }

    /**
     * Get payout summary for vendor
     */
    @Transactional(readOnly = true)
    public PayoutSummaryDTO getPayoutSummary(UUID vendorId) {
        log.debug("Fetching payout summary for vendor: {}", vendorId);

        BigDecimal totalPayouts = payoutRepository.getTotalPayoutsByVendor(vendorId);
        if (totalPayouts == null) {
            totalPayouts = BigDecimal.ZERO;
        }

        List<VendorPayoutRecord> pendingPayouts = payoutRepository.findByVendorIdAndStatus(
            vendorId, VendorPayoutRecord.PayoutStatus.PENDING);
        List<VendorPayoutRecord> failedPayouts = payoutRepository.findByVendorIdAndStatus(
            vendorId, VendorPayoutRecord.PayoutStatus.FAILED);

        BigDecimal pendingAmount = pendingPayouts.stream()
            .map(VendorPayoutRecord::getNetPayoutAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        VendorPayoutRecord lastPayout = pendingPayouts.isEmpty() ? null : pendingPayouts.get(0);

        return PayoutSummaryDTO.builder()
            .vendorId(vendorId)
            .totalPayouts(totalPayouts)
            .pendingPayoutAmount(pendingAmount)
            .pendingPayoutCount(pendingPayouts.size())
            .failedPayoutCount(failedPayouts.size())
            .lastPayoutDate(lastPayout != null ? lastPayout.getActualPayoutDate() : null)
            .build();
    }

    /**
     * Get total pending payout amount across all vendors
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalPendingPayoutAmount() {
        log.debug("Calculating total pending payout amount");
        BigDecimal total = payoutRepository.getTotalPendingPayoutAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get vendors with available payout
     */
    @Transactional(readOnly = true)
    public List<UUID> getVendorsWithAvailablePayout() {
        log.debug("Fetching vendors with available payout");
        return payoutRepository.findVendorsWithAvailablePayout();
    }

    /**
     * Process batch payouts
     */
    @Async
    public void processBatchPayouts() {
        log.info("Starting batch payout processing");

        List<VendorPayoutRecord> overduePayouts = payoutRepository.findOverduePayouts();
        
        for (VendorPayoutRecord payout : overduePayouts) {
            try {
                processPayout(payout.getId());
            } catch (Exception e) {
                log.error("Error processing batch payout: {}", payout.getId(), e);
                markPayoutAsFailed(payout.getId(), "Batch processing error: " + e.getMessage());
            }
        }

        log.info("Batch payout processing completed");
    }

    /**
     * Get total commission collected
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalCommissionCollected(String startPeriod, String endPeriod) {
        log.debug("Fetching total commission collected from: {} to: {}", startPeriod, endPeriod);
        BigDecimal total = payoutRepository.getTotalCommissionsInPeriod(startPeriod, endPeriod);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total sales in period
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalSalesInPeriod(String startPeriod, String endPeriod) {
        log.debug("Fetching total sales from: {} to: {}", startPeriod, endPeriod);
        BigDecimal total = payoutRepository.getTotalSalesInPeriod(startPeriod, endPeriod);
        return total != null ? total : BigDecimal.ZERO;
    }
}
