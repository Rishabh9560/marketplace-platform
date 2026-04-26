package com.logicveda.marketplace.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

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
