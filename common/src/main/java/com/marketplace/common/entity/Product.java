package com.marketplace.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;
    private String shortDescription;

    @Column(nullable = false)
    private Double price;

    private Double comparePrice;
    private Double costPrice;

    private String tags;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    private ProductVisibility visibility = ProductVisibility.DRAFT;

    private Boolean featured = false;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    private Integer lowStockThreshold = 10;
    private Boolean trackInventory = true;

    private Double rating = 0.0;
    private Integer reviewCount = 0;

    private String imageUrls;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ProductStatus {
        ACTIVE, INACTIVE, DRAFT, ARCHIVED
    }

    public enum ProductVisibility {
        PUBLIC, PRIVATE, DRAFT
    }
}
