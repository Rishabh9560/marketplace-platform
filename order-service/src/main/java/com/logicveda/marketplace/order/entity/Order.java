package com.logicveda.marketplace.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Order entity - represents a customer order (contains items from possibly multiple vendors)
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_customer", columnList = "customer_id"),
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber; // ORD-YYYYMMDD-XXXXX

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal; // Sum of all items

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // subtotal + shipping + tax - discount

    // Shipping Information
    @Column(length = 255)
    private String shippingAddress;

    @Column(length = 100)
    private String shippingCity;

    @Column(length = 50)
    private String shippingState;

    @Column(length = 10)
    private String shippingPostalCode;

    @Column(length = 100)
    private String billingAddress;

    @Column(length = 100)
    private String billingCity;

    @Column(length = 50)
    private String billingState;

    @Column(length = 10)
    private String billingPostalCode;

    // Tracking
    @Column(length = 100)
    private String trackingNumber;

    @Column(length = 50)
    private String shippingCarrier;

    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime paidAt;

    @Column
    private LocalDateTime shippedAt;

    @Column
    private LocalDateTime deliveredAt;

    @Column
    private LocalDateTime cancelledAt;

    // Notes
    @Column(columnDefinition = "TEXT")
    private String notes;

    // Relations
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> items = new HashSet<>();

    /**
     * Order status state machine
     */
    public enum OrderStatus {
        PENDING,           // Initial state, awaiting payment
        CONFIRMED,         // Payment received, awaiting fulfillment
        PROCESSING,        // Being packed
        SHIPPED,           // In transit
        DELIVERED,         // Final state
        CANCELLED,         // Cancelled by customer or system
        FAILED             // Payment failed
    }

    /**
     * Check if order is in final state
     */
    public boolean isFinal() {
        return status == OrderStatus.DELIVERED || 
               status == OrderStatus.CANCELLED || 
               status == OrderStatus.FAILED;
    }

    /**
     * Check if order can be cancelled
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
}
