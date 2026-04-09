package com.logicveda.marketplace.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Product entity representing marketplace products.
 * Supports multiple vendors (multi-vendor marketplace).
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_vendor", columnList = "vendor_id"),
    @Index(name = "idx_products_category", columnList = "category_id"),
    @Index(name = "idx_products_status", columnList = "status"),
    @Index(name = "idx_products_slug", columnList = "slug", unique = true),
    @Index(name = "idx_products_tags", columnList = "tags", name = "idx_products_tags_gin")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "slug")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID vendorId;

    @Column
    private UUID categoryId;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false, unique = true, length = 500)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String shortDescription;

    @Column(length = 255)
    private String brand;

    @Column(columnDefinition = "text[]")
    private String[] tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductVariant> variants = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductImage> images = new HashSet<>();

    /**
     * Enum for product status.
     */
    public enum ProductStatus {
        DRAFT,
        PENDING_APPROVAL,
        APPROVED,
        REJECTED,
        ARCHIVED
    }
}
