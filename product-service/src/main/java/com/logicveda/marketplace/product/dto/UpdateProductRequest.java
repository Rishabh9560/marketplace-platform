package com.logicveda.marketplace.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

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
