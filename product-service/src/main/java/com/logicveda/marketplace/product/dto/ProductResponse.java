package com.logicveda.marketplace.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for product details.
 */
@Schema(name = "ProductResponse", description = "Product details response")
public record ProductResponse(
        @Schema(description = "Product ID")
        UUID id,

        @Schema(description = "Product name")
        String name,

        @Schema(description = "Product slug")
        String slug,

        @Schema(description = "Product description")
        String description,

        @Schema(description = "Brand name")
        String brand,

        @Schema(description = "Vendor ID")
        UUID vendorId,

        @Schema(description = "Category ID")
        UUID categoryId,

        @Schema(description = "Category name")
        String categoryName,

        @Schema(description = "Product status")
        String status,

        @Schema(description = "Is featured")
        Boolean isFeatured,

        @Schema(description = "Average rating")
        BigDecimal averageRating,

        @Schema(description = "Review count")
        Integer reviewCount,

        @Schema(description = "Product tags")
        String[] tags,

        @Schema(description = "Product variants")
        List<VariantResponse> variants,

        @Schema(description = "Product images")
        List<ImageResponse> images,

        @Schema(description = "Created at")
        String createdAt,

        @Schema(description = "Updated at")
        String updatedAt
) {}
