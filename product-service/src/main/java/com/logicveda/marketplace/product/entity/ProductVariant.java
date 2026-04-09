package com.logicveda.marketplace.product.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.JsonType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ProductVariant entity representing different versions of a product.
 * Examples: different color, size, storage capacity, etc.
 */
@Entity
@Table(name = "product_variants", indexes = {
    @Index(name = "idx_product_variants_product", columnList = "product_id"),
    @Index(name = "idx_product_variants_sku", columnList = "sku", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "sku")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(length = 255)
    private String name;

    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(JsonType.class)
    private JsonNode attributes;  // { "color": "Red", "size": "L", "storage": "128GB" }

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;

    @Column
    private Integer weightGrams;

    @Column(columnDefinition = "text[]")
    private String[] imageUrls;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductImage> images = new HashSet<>();

    /**
     * Check if inventory is low.
     */
    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity <= lowStockThreshold;
    }

    /**
     * Check if item is in stock.
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
}
