package com.logicveda.marketplace.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
