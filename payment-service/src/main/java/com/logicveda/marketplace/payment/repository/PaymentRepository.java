package com.logicveda.marketplace.payment.repository;

import com.logicveda.marketplace.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Find by order
    Optional<Payment> findByOrderId(UUID orderId);

    // Find by transaction ID
    Optional<Payment> findByTransactionId(String transactionId);

    // Find by idempotency key
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    // Find by payment intent ID
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);

    // Find all by customer
    Page<Payment> findByCustomerId(UUID customerId, Pageable pageable);

    // Find all by vendor
    Page<Payment> findByVendorId(UUID vendorId, Pageable pageable);

    // Find all by status
    Page<Payment> findByStatus(Payment.PaymentStatus status, Pageable pageable);

    // Find all by method within date range
    @Query("SELECT p FROM Payment p WHERE p.method = :method " +
           "AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByMethodAndDateRange(
        @Param("method") Payment.PaymentMethod method,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // Find pending payments older than 24 hours
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' " +
           "AND p.createdAt < :beforeDate")
    List<Payment> findStalePayments(@Param("beforeDate") LocalDateTime beforeDate);

    // Calculate total revenue by status
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status " +
           "AND p.createdAt BETWEEN :startDate AND :endDate")
    Optional<Double> getTotalRevenueByStatus(
        @Param("status") Payment.PaymentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
