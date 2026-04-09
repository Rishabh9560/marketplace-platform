package com.logicveda.marketplace.product.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTOs for Product operations.
 */

/**
 * Request DTO for creating a new product.
 */
@Schema(name = "CreateProductRequest", description = "Create new product request")
public record CreateProductRequest(
        @Schema(description = "Product name", example = "Apple iPhone 15 Pro")
        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 500, message = "Product name must be 3-500 characters")
        String name,

        @Schema(description = "Product description")
        String description,

        @Schema(description = "Short description")
        String shortDescription,

        @Schema(description = "Brand name", example = "Apple")
        String brand,

        @Schema(description = "Category ID")
        UUID categoryId,

        @Schema(description = "Product tags")
        List<String> tags,

        @Schema(description = "Product variants")
        @NotEmpty(message = "At least one variant is required")
        List<CreateVariantRequest> variants
) {}

/**
 * Request DTO for creating product variant.
 */
@Schema(name = "CreateVariantRequest", description = "Create product variant request")
public record CreateVariantRequest(
        @Schema(description = "Variant SKU", example = "IPHONE15PRO-256GB-SPACE")
        @NotBlank(message = "SKU is required")
        @Size(min = 3, max = 100, message = "SKU must be 3-100 characters")
        String sku,

        @Schema(description = "Variant name", example = "256GB Space Black")
        String name,

        @Schema(description = "Variant attributes (color, size, etc.)")
        Map<String, String> attributes,

        @Schema(description = "Selling price", example = "99999.00")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @Schema(description = "Original price for comparison", example = "109999.00")
        BigDecimal compareAtPrice,

        @Schema(description = "Cost price", example = "75000.00")
        BigDecimal costPrice,

        @Schema(description = "Stock quantity", example = "100")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @Schema(description = "Low stock threshold", example = "10")
        @Min(value = 1, message = "Low stock threshold must be at least 1")
        Integer lowStockThreshold,

        @Schema(description = "Product images URLs")
        List<String> imageUrls
) {}

/**
 * Request DTO for updating product.
 */
@Schema(name = "UpdateProductRequest", description = "Update product request")
public record UpdateProductRequest(
        @Schema(description = "Product name")
        String name,

        @Schema(description = "Product description")
        String description,

        @Schema(description = "Short description")
        String shortDescription,

        @Schema(description = "Brand name")
        String brand,

        @Schema(description = "Category ID")
        UUID categoryId,

        @Schema(description = "Product tags")
        List<String> tags,

        @Schema(description = "Updated variants")
        List<UpdateVariantRequest> variants
) {}

/**
 * Request DTO for updating variant.
 */
@Schema(name = "UpdateVariantRequest", description = "Update product variant request")
public record UpdateVariantRequest(
        @Schema(description = "Variant ID")
        UUID id,

        @Schema(description = "Variant name")
        String name,

        @Schema(description = "Selling price")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal price,

        @Schema(description = "Original price")
        BigDecimal compareAtPrice,

        @Schema(description = "Stock quantity")
        @Min(value = 0)
        Integer stockQuantity,

        @Schema(description = "Product images URLs")
        List<String> imageUrls
) {}

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

/**
 * Response DTO for product variant.
 */
@Schema(name = "VariantResponse", description = "Product variant response")
public record VariantResponse(
        @Schema(description = "Variant ID")
        UUID id,

        @Schema(description = "SKU")
        String sku,

        @Schema(description = "Variant name")
        String name,

        @Schema(description = "Attributes")
        JsonNode attributes,

        @Schema(description = "Price")
        BigDecimal price,

        @Schema(description = "Compare at price")
        BigDecimal compareAtPrice,

        @Schema(description = "Stock quantity")
        Integer stockQuantity,

        @Schema(description = "Is in stock")
        Boolean inStock,

        @Schema(description = "Is low stock")
        Boolean lowStock,

        @Schema(description = "Image URLs")
        String[] imageUrls
) {}

/**
 * Response DTO for product image.
 */
@Schema(name = "ImageResponse", description = "Product image response")
public record ImageResponse(
        @Schema(description = "Image ID")
        UUID id,

        @Schema(description = "Image URL")
        String url,

        @Schema(description = "Alt text")
        String altText,

        @Schema(description = "Is primary image")
        Boolean isPrimary,

        @Schema(description = "Sort order")
        Integer sortOrder
) {}

/**
 * Response DTO for category.
 */
@Schema(name = "CategoryResponse", description = "Category response")
public record CategoryResponse(
        @Schema(description = "Category ID")
        UUID id,

        @Schema(description = "Category name")
        String name,

        @Schema(description = "Category slug")
        String slug,

        @Schema(description = "Category description")
        String description,

        @Schema(description = "Category image URL")
        String imageUrl,

        @Schema(description = "Is active")
        Boolean isActive,

        @Schema(description = "Sort order")
        Integer sortOrder
) {}
