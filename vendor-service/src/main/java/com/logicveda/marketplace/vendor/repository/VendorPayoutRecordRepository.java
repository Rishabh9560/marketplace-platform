package com.logicveda.marketplace.vendor.repository;

import com.logicveda.marketplace.vendor.entity.VendorPayoutRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VendorPayoutRecord entity
 */
@Repository
public interface VendorPayoutRecordRepository extends JpaRepository<VendorPayoutRecord, UUID> {

    /**
     * Find payout records by vendor
     */
    Page<VendorPayoutRecord> findByVendorId(UUID vendorId, Pageable pageable);

    /**
     * Find payout record for vendor in specific period
     */
    Optional<VendorPayoutRecord> findByVendorIdAndPayoutPeriod(UUID vendorId, String payoutPeriod);

    /**
     * Find pending payouts for a vendor
     */
    List<VendorPayoutRecord> findByVendorIdAndStatus(UUID vendorId, VendorPayoutRecord.PayoutStatus status);

    /**
     * Find all pending payouts across all vendors
     */
    List<VendorPayoutRecord> findAllByStatus(VendorPayoutRecord.PayoutStatus status);

    /**
     * Find payouts scheduled for specific date range
     */
    @Query("SELECT pr FROM VendorPayoutRecord pr WHERE pr.vendorId = :vendorId " +
           "AND pr.scheduledPayoutDate BETWEEN :startDate AND :endDate " +
           "ORDER BY pr.scheduledPayoutDate ASC")
    List<VendorPayoutRecord> findScheduledPayouts(
        @Param("vendorId") UUID vendorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find failed payouts for retry
     */
    @Query("SELECT pr FROM VendorPayoutRecord pr WHERE pr.status = 'FAILED' " +
           "AND pr.retryCount < :maxRetries ORDER BY pr.updatedAt ASC")
    List<VendorPayoutRecord> findFailedPayoutsForRetry(@Param("maxRetries") int maxRetries);

    /**
     * Find completed payouts by vendor
     */
    Page<VendorPayoutRecord> findByVendorIdAndStatus(
        UUID vendorId,
        VendorPayoutRecord.PayoutStatus status,
        Pageable pageable
    );

    /**
     * Find payouts in specific period range
     */
    @Query("SELECT pr FROM VendorPayoutRecord pr WHERE pr.payoutPeriod >= :startPeriod " +
           "AND pr.payoutPeriod <= :endPeriod AND pr.vendorId = :vendorId")
    List<VendorPayoutRecord> findByPeriodRange(
        @Param("vendorId") UUID vendorId,
        @Param("startPeriod") String startPeriod,
        @Param("endPeriod") String endPeriod
    );

    /**
     * Get total payout amount by vendor
     */
    @Query("SELECT SUM(pr.netPayoutAmount) FROM VendorPayoutRecord pr WHERE pr.vendorId = :vendorId " +
           "AND pr.status = 'COMPLETED'")
    BigDecimal getTotalPayoutsByVendor(@Param("vendorId") UUID vendorId);

    /**
     * Find payouts with hold status
     */
    List<VendorPayoutRecord> findAllByStatusOrderByCreatedAtAsc(VendorPayoutRecord.PayoutStatus status);

    /**
     * Get average payout amount
     */
    @Query("SELECT AVG(pr.netPayoutAmount) FROM VendorPayoutRecord pr WHERE pr.status = 'COMPLETED'")
    BigDecimal getAveragePayoutAmount();

    /**
     * Get total commission deducted
     */
    @Query("SELECT SUM(pr.commissionDeducted) FROM VendorPayoutRecord pr WHERE pr.vendorId = :vendorId")
    BigDecimal getTotalCommissionDeducted(@Param("vendorId") UUID vendorId);

    /**
     * Find payouts by transaction ID
     */
    Optional<VendorPayoutRecord> findByTransactionId(String transactionId);

    /**
     * Count payouts by status
     */
    long countByStatus(VendorPayoutRecord.PayoutStatus status);

    /**
     * Find overdue payouts
     */
    @Query("SELECT pr FROM VendorPayoutRecord pr WHERE pr.status = 'SCHEDULED' " +
           "AND pr.scheduledPayoutDate < CURRENT_TIMESTAMP ORDER BY pr.scheduledPayoutDate ASC")
    List<VendorPayoutRecord> findOverduePayouts();

    /**
     * Get total commissions in period
     */
    @Query("SELECT SUM(pr.commissionDeducted) FROM VendorPayoutRecord pr " +
           "WHERE pr.payoutPeriod >= :startPeriod AND pr.payoutPeriod <= :endPeriod")
    BigDecimal getTotalCommissionsInPeriod(
        @Param("startPeriod") String startPeriod,
        @Param("endPeriod") String endPeriod
    );

    /**
     * Get total sales in period
     */
    @Query("SELECT SUM(pr.totalSalesAmount) FROM VendorPayoutRecord pr " +
           "WHERE pr.payoutPeriod >= :startPeriod AND pr.payoutPeriod <= :endPeriod")
    BigDecimal getTotalSalesInPeriod(
        @Param("startPeriod") String startPeriod,
        @Param("endPeriod") String endPeriod
    );

    /**
     * Find recent payouts for dashboard
     */
    @Query("SELECT pr FROM VendorPayoutRecord pr WHERE pr.vendorId = :vendorId " +
           "ORDER BY pr.actualPayoutDate DESC LIMIT 10")
    List<VendorPayoutRecord> findRecentPayouts(@Param("vendorId") UUID vendorId);

    /**
     * Find payouts with disputes
     */
    @Query("SELECT pr FROM VendorPayoutRecord pr WHERE pr.status = 'ON_HOLD' " +
           "ORDER BY pr.createdAt ASC")
    List<VendorPayoutRecord> findPayoutsOnHold();

    /**
     * Calculate total pending payout amount
     */
    @Query("SELECT SUM(pr.netPayoutAmount) FROM VendorPayoutRecord pr " +
           "WHERE pr.status IN ('PENDING', 'SCHEDULED', 'PROCESSING')")
    BigDecimal getTotalPendingPayoutAmount();

    /**
     * Find vendors with available payout
     */
    @Query("SELECT DISTINCT pr.vendorId FROM VendorPayoutRecord pr " +
           "WHERE pr.status = 'PENDING' AND pr.netPayoutAmount > 0")
    List<UUID> findVendorsWithAvailablePayout();
}
