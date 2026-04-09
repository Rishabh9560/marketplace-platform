package com.logicveda.marketplace.payment.controller;

import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import com.logicveda.marketplace.payment.entity.Transaction;
import com.logicveda.marketplace.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    // ============= PAYMENT PROCESSING =============

    @PostMapping("/create-intent")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Create payment intent", description = "Create Stripe payment intent for checkout")
    public ResponseEntity<String> createPaymentIntent(
        @Parameter(description = "Order ID", required = true)
        @RequestParam UUID orderId,
        @Parameter(description = "Payment amount", required = true)
        @RequestParam @DecimalMin("0.01") BigDecimal amount) {

        String clientSecret = paymentService.createPaymentIntent(amount, orderId.toString());
        return ResponseEntity.ok(clientSecret);
    }

    @PostMapping("/{orderId}/charge")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Process payment", description = "Process payment for order")
    public ResponseEntity<Transaction> processPayment(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId,
        @Parameter(description = "Payment amount", required = true)
        @RequestParam @DecimalMin("0.01") BigDecimal amount,
        @Parameter(description = "Stripe payment method ID", required = true)
        @RequestParam String paymentMethodId,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        
        // Validate payment method
        if (!paymentService.validatePaymentMethod(paymentMethodId)) {
            return ResponseEntity.badRequest().build();
        }

        Transaction transaction = paymentService.processPayment(orderId, principal.getUserId(), amount, paymentMethodId);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    // ============= TRANSACTION HISTORY =============

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get transactions", description = "Retrieve customer's transaction history")
    public ResponseEntity<Page<Transaction>> getTransactions(
        @Parameter(description = "Page number")
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(paymentService.getCustomerTransactions(principal.getUserId(), pageable));
    }

    @GetMapping("/transactions/{transactionId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get transaction", description = "Retrieve specific transaction details")
    public ResponseEntity<Transaction> getTransaction(
        @Parameter(description = "Transaction ID", required = true)
        @PathVariable UUID transactionId) {

        Transaction transaction = paymentService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/orders/{orderId}/transaction")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get order transaction", description = "Get transaction for specific order")
    public ResponseEntity<Transaction> getOrderTransaction(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId) {

        Transaction transaction = paymentService.getTransactionByOrderId(orderId);
        return ResponseEntity.ok(transaction);
    }

    // ============= REFUND PROCESSING =============

    @PostMapping("/{transactionId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Refund payment", description = "Process full refund for transaction (admin only)")
    public ResponseEntity<Transaction> refundPayment(
        @Parameter(description = "Transaction ID", required = true)
        @PathVariable UUID transactionId,
        @Parameter(description = "Refund reason")
        @RequestParam String reason) {

        Transaction transaction = paymentService.refundPayment(transactionId, reason);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{transactionId}/partial-refund")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Partial refund", description = "Process partial refund for transaction (admin only)")
    public ResponseEntity<Transaction> partialRefund(
        @Parameter(description = "Transaction ID", required = true)
        @PathVariable UUID transactionId,
        @Parameter(description = "Refund amount", required = true)
        @RequestParam @DecimalMin("0.01") BigDecimal refundAmount,
        @Parameter(description = "Refund reason")
        @RequestParam String reason) {

        Transaction transaction = paymentService.partialRefund(transactionId, refundAmount, reason);
        return ResponseEntity.ok(transaction);
    }
}
