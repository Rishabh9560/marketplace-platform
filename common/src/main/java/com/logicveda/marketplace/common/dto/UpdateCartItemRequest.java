package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * Request DTO for updating cart item quantity.
 */
@Schema(name = "UpdateCartItemRequest", description = "Update cart item quantity")
public record UpdateCartItemRequest(
    @Schema(description = "Item ID in cart")
    @NotBlank(message = "Item ID cannot be blank")
    String itemId,

    @Schema(description = "New quantity", example = "3")
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    Integer quantity
) implements Serializable {}
