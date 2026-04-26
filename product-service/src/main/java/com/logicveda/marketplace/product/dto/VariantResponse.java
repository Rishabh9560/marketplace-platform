package com.logicveda.marketplace.product.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

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
