package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Request DTO for adding item to cart.
 */
@Schema(name = "AddToCartRequest", description = "Request to add item to cart")
public record AddToCartRequest(
    @Schema(description = "Product variant ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Variant ID cannot be null")
    UUID variantId,

    @Schema(description = "Quantity to add", example = "2")
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    Integer quantity,

    @Schema(description = "Vendor ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Vendor ID cannot be null")
    UUID vendorId
) implements Serializable {}
