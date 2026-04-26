package com.logicveda.marketplace.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
