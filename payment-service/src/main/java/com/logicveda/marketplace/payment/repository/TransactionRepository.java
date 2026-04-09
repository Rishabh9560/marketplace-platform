package com.logicveda.marketplace.payment.repository;

import com.logicveda.marketplace.payment.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    /**
     * Find transaction by order ID
     */
    Optional<Transaction> findByOrderId(UUID orderId);
    
    /**
     * Find transactions by customer ID
     */
    Page<Transaction> findByCustomerId(UUID customerId, Pageable pageable);
    
    /**
     * Find transaction by Stripe charge ID
     */
    Optional<Transaction> findByStripeChargeId(String chargeId);
    
    /**
     * Find completed transactions for refund tracking
     */
    List<Transaction> findByStatusAndIsRefundedFalse(Transaction.TransactionStatus status);
}
