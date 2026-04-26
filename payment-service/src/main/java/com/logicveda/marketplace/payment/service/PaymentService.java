package com.logicveda.marketplace.payment.service;

import com.logicveda.marketplace.common.exception.BusinessException;
import com.logicveda.marketplace.common.exception.ResourceNotFoundException;
import com.logicveda.marketplace.common.event.PaymentProcessedEvent;
import com.logicveda.marketplace.common.service.EventPublisher;
import com.logicveda.marketplace.payment.entity.Transaction;
import com.logicveda.marketplace.payment.repository.TransactionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Payment Service
 * Handles payment processing via Stripe integration
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final EventPublisher eventPublisher;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    // ============= PAYMENT PROCESSING =============

    /**
     * Process payment via Stripe
     * @param orderId Order being paid
     * @param customerId Customer making payment
     * @param amount Payment amount (in cents for Stripe)
     * @param paymentMethodId Stripe payment method ID
     * @return Transaction record
     */
    public Transaction processPayment(UUID orderId, UUID customerId, BigDecimal amount, String paymentMethodId) {
        log.info("Processing payment for order: {} by customer: {}", orderId, customerId);

        try {
            // Create Stripe charge
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amountInCents);
            chargeParams.put("currency", "usd");
            chargeParams.put("source", paymentMethodId);
            chargeParams.put("description", "Order " + orderId);
            chargeParams.put("metadata", Map.of(
                "orderId", orderId.toString(),
                "customerId", customerId.toString()
            ));

            Charge charge = Charge.create(chargeParams);

            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setOrderId(orderId);
            transaction.setCustomerId(customerId);
            transaction.setStripePaymentMethodId(paymentMethodId);
            transaction.setStripeChargeId(charge.getId());
            transaction.setPaymentMethod(Transaction.PaymentMethod.CARD);
            transaction.setAmount(amount);
            transaction.setCurrency("USD");
            transaction.setStatus(charge.getStatus().equals("succeeded") ? 
                Transaction.TransactionStatus.COMPLETED : Transaction.TransactionStatus.FAILED);
            transaction.setProcessedAt(LocalDateTime.now());
            transaction.setIsRefunded(false);

            if (!charge.getStatus().equals("succeeded")) {
                transaction.setStatus(Transaction.TransactionStatus.FAILED);
                transaction.setFailureReason(charge.getFailureMessage());
            }

            transaction = transactionRepository.save(transaction);
            log.info("Payment processed successfully. Transaction ID: {}, Charge ID: {}", transaction.getId(), charge.getId());

            // Publish PaymentProcessed event to Kafka
            if (transaction.getStatus() == Transaction.TransactionStatus.COMPLETED) {
                PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                    .paymentId(transaction.getId())
                    .orderId(orderId)
                    .customerId(customerId)
                    .vendorId(transaction.getVendorId())
                    .amount(amount)
                    .paymentMethod("STRIPE_CARD")
                    .status("COMPLETED")
                    .transactionId(charge.getId())
                    .processedAt(LocalDateTime.now())
                    .build();
                
                eventPublisher.publishPaymentProcessed(event);
                log.info("PaymentProcessedEvent published for order: {}", orderId);
            }

            return transaction;

        } catch (StripeException e) {
            log.error("Stripe payment error: {}", e.getMessage());
            
            // Save failed transaction record
            Transaction transaction = new Transaction();
            transaction.setOrderId(orderId);
            transaction.setCustomerId(customerId);
            transaction.setStripePaymentMethodId(paymentMethodId);
            transaction.setPaymentMethod(Transaction.PaymentMethod.CARD);
            transaction.setAmount(amount);
            transaction.setCurrency("USD");
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transaction.setIsRefunded(false);

            transactionRepository.save(transaction);

            // Publish PaymentProcessed event with FAILED status
            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(transaction.getId())
                .orderId(orderId)
                .customerId(customerId)
                .vendorId(transaction.getVendorId())
                .amount(amount)
                .paymentMethod("STRIPE_CARD")
                .status("FAILED")
                .transactionId("")
                .processedAt(LocalDateTime.now())
                .build();
            
            eventPublisher.publishPaymentProcessed(event);
            log.info("PaymentProcessedEvent (FAILED) published for order: {}", orderId);

            throw new BusinessException("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Get transaction by order ID
     */
    public Transaction getTransactionByOrderId(UUID orderId) {
        return transactionRepository.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for order: " + orderId));
    }

    /**
     * Get customer's transactions
     */
    public Page<Transaction> getCustomerTransactions(UUID customerId, Pageable pageable) {
        return transactionRepository.findByCustomerId(customerId, pageable);
    }

    /**
     * Get transaction by ID
     */
    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    // ============= REFUND PROCESSING =============

    /**
     * Process full refund
     */
    public Transaction refundPayment(UUID transactionId, String reason) {
        Transaction transaction = getTransactionById(transactionId);

        if (!transaction.canBeRefunded()) {
            throw new BusinessException("Cannot refund transaction with status: " + transaction.getStatus());
        }

        try {
            // Create Stripe refund
            RefundCreateParams params = RefundCreateParams.builder()
                .setCharge(transaction.getStripeChargeId())
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                .setMetadata(Map.of("reason", reason != null ? reason : "Manual refund"))
                .build();

            Refund refund = Refund.create(params);

            // Update transaction
            transaction.setStatus(Transaction.TransactionStatus.REFUNDED);
            transaction.setIsRefunded(true);
            transaction.setRefundAmount(transaction.getAmount());
            transaction.setRefundTransactionId(refund.getId());
            transaction.setRefundedAt(LocalDateTime.now());

            transaction = transactionRepository.save(transaction);
            log.info("Refund processed successfully. Transaction ID: {}, Refund ID: {}", transactionId, refund.getId());

            // Publish PaymentProcessed event with CANCELLED status (refund)
            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(transaction.getId())
                .orderId(transaction.getOrderId())
                .customerId(transaction.getCustomerId())
                .vendorId(transaction.getVendorId())
                .amount(transaction.getAmount())
                .paymentMethod("STRIPE_CARD")
                .status("CANCELLED")
                .transactionId(refund.getId())
                .processedAt(LocalDateTime.now())
                .build();
            
            eventPublisher.publishPaymentProcessed(event);
            log.info("PaymentProcessedEvent (CANCELLED via refund) published for order: {}", transaction.getOrderId());

            return transaction;

        } catch (StripeException e) {
            log.error("Stripe refund error: {}", e.getMessage());
            throw new BusinessException("Refund processing failed: " + e.getMessage());
        }
    }

    /**
     * Process partial refund
     */
    public Transaction partialRefund(UUID transactionId, BigDecimal refundAmount, String reason) {
        Transaction transaction = getTransactionById(transactionId);

       if (!transaction.canBeRefunded()) {
            throw new BusinessException("Cannot refund transaction with status: " + transaction.getStatus());
        }

        if (refundAmount.compareTo(transaction.getAmount()) > 0) {
            throw new BusinessException("Refund amount cannot exceed transaction amount");
        }

        try {
            long amountInCents = refundAmount.multiply(BigDecimal.valueOf(100)).longValue();
            
            RefundCreateParams params = RefundCreateParams.builder()
                .setCharge(transaction.getStripeChargeId())
                .setAmount(amountInCents)
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                .setMetadata(Map.of("reason", reason != null ? reason : "Partial refund"))
                .build();

            Refund refund = Refund.create(params);

            // Update transaction
            transaction.setStatus(Transaction.TransactionStatus.PARTIALLY_REFUNDED);
            transaction.setRefundAmount(refundAmount);
            transaction.setRefundTransactionId(refund.getId());
            transaction.setRefundedAt(LocalDateTime.now());

            transaction = transactionRepository.save(transaction);
            log.info("Partial refund processed. Transaction ID: {}, Refund ID: {}, Amount: {}", 
                transactionId, refund.getId(), refundAmount);

            return transaction;

        } catch (StripeException e) {
            log.error("Stripe partial refund error: {}", e.getMessage());
            throw new BusinessException("Partial refund processing failed: " + e.getMessage());
        }
    }

    /**
     * Validate payment method (Stripe token validation)
     */
    public boolean validatePaymentMethod(String paymentMethodId) {
        // TODO: Implement actual Stripe payment method validation
        // For now, just basic null check
        return paymentMethodId != null && !paymentMethodId.isEmpty();
    }

    // ============= PAYMENT TOKENS (PCI Compliance) =============

    /**
     * Create Stripe payment intent for client-side token generation
     * (Follows PCI DSS compliance - no card data on server)
     */
    public String createPaymentIntent(BigDecimal amount, String orderId) {
        try {
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amountInCents);
            params.put("currency", "usd");
            params.put("metadata", Map.of("orderId", orderId));

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return paymentIntent.getClientSecret();

        } catch (StripeException e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            throw new BusinessException("Failed to create payment intent: " + e.getMessage());
        }
    }
}
