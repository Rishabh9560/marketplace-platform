package com.logicveda.marketplace.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

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
