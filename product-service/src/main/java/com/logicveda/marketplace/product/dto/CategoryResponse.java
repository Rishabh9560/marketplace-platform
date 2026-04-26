package com.logicveda.marketplace.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

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
