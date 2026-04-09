package com.logicveda.marketplace.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItem entity - represents a single product variant in an order
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_items_order", columnList = "order_id"),
    @Index(name = "idx_order_items_product", columnList = "product_id"),
    @Index(name = "idx_order_items_vendor", columnList = "vendor_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID variantId;

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false)
    private UUID vendorId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice; // Price at time of order (may differ from current price)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal; // unitPrice * quantity

    // Fulfillment tracking
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FulfillmentStatus fulfillmentStatus = FulfillmentStatus.PENDING;

    /**
     * Fulfillment status for each item
     */
    public enum FulfillmentStatus {
        PENDING,      // Awaiting fulfillment
        PROCESSING,   // Being packed
        SHIPPED,      // In transit
        DELIVERED,    // Delivered to customer
        RETURNED,     // Returned by customer
        CANCELLED,    // Order item cancelled
        FAILED        // Fulfillment failed
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;
}
