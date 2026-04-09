package com.logicveda.marketplace.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ProductListing entity - vendor's product availability and pricing
 */
@Entity
@Table(name = "product_listings", indexes = {
    @Index(name = "idx_product_listings_vendor", columnList = "vendor_id"),
    @Index(name = "idx_product_listings_product", columnList = "product_id"),
    @Index(name = "idx_product_listings_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProductListing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID vendorId; // Reference to VendorProfile

    @Column(nullable = false)
    private UUID productId; // Reference to Product from Product Service

    @Column(nullable = false, length = 255)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    @Column(columnDefinition = "TEXT")
    private String sku; // Stock Keeping Unit - unique within vendor

    // Pricing
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal vendorPrice; // Vendor's selling price

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal marketplaceList;  // Original marketplace list price

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage; // Vendor's discount

    // Inventory
    @Column(nullable = false)
    @Builder.Default
    private Integer quantityAvailable = 0;

    @Column
    @Builder.Default
    private Integer quantityReserved = 0;

    @Column
    @Builder.Default
    private Integer reorderLevel = 10;

    @Column
    @Builder.Default
    private Integer reorderQuantity = 50;

    // Listing Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ListingStatus status = ListingStatus.DRAFT;

    @Column
    @Builder.Default
    private Boolean isVisible = true;

    @Column
    @Builder.Default
    private Boolean isHighlighted = false;

    // Shipping
    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column
    @Builder.Default
    private Boolean freeShipping = false;

    @Column
    @Builder.Default
    private Integer shippingDays = 3; // Days to ship

    // Quality Metrics
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal vendorRating = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private Integer totalSales = 0;

    @Column
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(columnDefinition = "TEXT")
    private String images; // JSON array of image URLs

    @Column(columnDefinition = "TEXT")
    private String features; // JSON array of product features

    // Analytics
    @Column
    @Builder.Default
    private Integer viewCount = 0;

    @Column
    @Builder.Default
    private Integer favoriteCount = 0;

    @Column
    private LocalDateTime lastViewedAt;

    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime listedAt;

    @Column
    private LocalDateTime delistedAt;

    /**
     * Listing Status Enum
     */
    public enum ListingStatus {
        DRAFT,           // Not yet published
        ACTIVE,          // Available for sale
        INACTIVE,        // Temporarily unavailable
        DELISTED,        // Removed from marketplace
        SUSPENDED        // Vendor or product suspended
    }

    public BigDecimal getDiscountedPrice() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return vendorPrice;
        }
        BigDecimal discount = vendorPrice.multiply(discountPercentage).divide(new BigDecimal(100));
        return vendorPrice.subtract(discount);
    }

    public Boolean hasStockAvailable() {
        return quantityAvailable > 0 && status == ListingStatus.ACTIVE;
    }
}
