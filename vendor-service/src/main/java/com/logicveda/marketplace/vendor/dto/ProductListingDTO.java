package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ProductListing DTO for API requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Product Listing Information")
public class ProductListingDTO {

    @Schema(description = "Listing ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Vendor ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID vendorId;

    @NotNull(message = "Product ID is required")
    @Schema(description = "Product ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID productId;

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    @Schema(description = "Product name", example = "Wireless Headphones Pro")
    private String productName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Product description")
    private String productDescription;

    @NotBlank(message = "SKU is required")
    @Schema(description = "Stock Keeping Unit", example = "WHP-2024-001")
    private String sku;

    // Pricing
    @NotNull(message = "Vendor price is required")
    @DecimalMin(value = "0.01", message = "Vendor price must be greater than 0")
    @Schema(description = "Vendor's selling price", example = "79.99")
    private BigDecimal vendorPrice;

    @NotNull(message = "Marketplace list price is required")
    @DecimalMin(value = "0.01", message = "Marketplace price must be greater than 0")
    @Schema(description = "Original marketplace list price", example = "99.99")
    private BigDecimal marketplaceList;

    @DecimalMin(value = "0.00", message = "Discount must be >= 0")
    @DecimalMax(value = "100.00", message = "Discount must be <= 100")
    @Schema(description = "Discount percentage", example = "20.00")
    private BigDecimal discountPercentage;

    // Inventory
    @NotNull(message = "Quantity available is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Schema(description = "Quantity available", example = "100")
    private Integer quantityAvailable;

    @Min(value = 0, message = "Reserved quantity cannot be negative")
    @Schema(description = "Quantity reserved for orders", example = "20")
    private Integer quantityReserved;

    @Min(value = 1, message = "Reorder level must be at least 1")
    @Schema(description = "Reorder level threshold", example = "10")
    private Integer reorderLevel;

    @Min(value = 1, message = "Reorder quantity must be at least 1")
    @Schema(description = "Reorder quantity", example = "50")
    private Integer reorderQuantity;

    // Listing Status
    @Schema(description = "Listing status", example = "ACTIVE", allowableValues = {"DRAFT", "ACTIVE", "INACTIVE", "DELISTED", "SUSPENDED"})
    private String status;

    @Schema(description = "Listing is visible to customers", example = "true")
    private Boolean isVisible;

    @Schema(description = "Listing is highlighted/featured", example = "false")
    private Boolean isHighlighted;

    // Shipping
    @DecimalMin(value = "0.00", message = "Shipping cost must be >= 0")
    @Schema(description = "Shipping cost", example = "5.99")
    private BigDecimal shippingCost;

    @Schema(description = "Free shipping available", example = "false")
    private Boolean freeShipping;

    @Min(value = 1, message = "Shipping days must be at least 1")
    @Schema(description = "Days to ship", example = "3")
    private Integer shippingDays;

    // Quality Metrics
    @Schema(description = "Vendor's rating for this product", example = "4.75")
    private BigDecimal vendorRating;

    @Min(value = 0, message = "Total sales cannot be negative")
    @Schema(description = "Total units sold", example = "500")
    private Integer totalSales;

    @Min(value = 0, message = "Total reviews cannot be negative")
    @Schema(description = "Total product reviews", example = "150")
    private Integer totalReviews;

    @Schema(description = "Product images (JSON array of URLs)")
    private String images;

    @Schema(description = "Product features (JSON array)")
    private String features;

    // Analytics
    @Min(value = 0, message = "View count cannot be negative")
    @Schema(description = "Number of views", example = "1500")
    private Integer viewCount;

    @Min(value = 0, message = "Favorite count cannot be negative")
    @Schema(description = "Number of favorites", example = "250")
    private Integer favoriteCount;

    @Schema(description = "Last viewed timestamp")
    private LocalDateTime lastViewedAt;

    // Timestamps
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Listed date")
    private LocalDateTime listedAt;

    @Schema(description = "Delisted date")
    private LocalDateTime delistedAt;

    /**
     * Get final price after discount
     */
    public BigDecimal getDiscountedPrice() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return vendorPrice;
        }
        BigDecimal discount = vendorPrice.multiply(discountPercentage).divide(new BigDecimal(100));
        return vendorPrice.subtract(discount);
    }

    /**
     * Check if product has stock
     */
    public Boolean hasStock() {
        return quantityAvailable != null && quantityAvailable > 0 && "ACTIVE".equals(status);
    }

    /**
     * Create public listing DTO (without vendor-only info)
     */
    public ProductListingDTO toPublicDTO() {
        return ProductListingDTO.builder()
            .id(this.id)
            .productId(this.productId)
            .vendorId(this.vendorId)
            .productName(this.productName)
            .productDescription(this.productDescription)
            .vendorPrice(this.vendorPrice)
            .marketplaceList(this.marketplaceList)
            .discountPercentage(this.discountPercentage)
            .quantityAvailable(this.quantityAvailable)
            .shippingCost(this.shippingCost)
            .freeShipping(this.freeShipping)
            .vendorRating(this.vendorRating)
            .totalSales(this.totalSales)
            .totalReviews(this.totalReviews)
            .images(this.images)
            .features(this.features)
            .createdAt(this.createdAt)
            .build();
    }
}
