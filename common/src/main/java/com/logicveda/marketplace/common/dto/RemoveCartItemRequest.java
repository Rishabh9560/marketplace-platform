package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * Request DTO for removing cart item.
 */
@Schema(name = "RemoveCartItemRequest", description = "Remove item from cart")
public record RemoveCartItemRequest(
    @Schema(description = "Item ID to remove")
    @NotBlank(message = "Item ID cannot be blank")
    String itemId
) implements Serializable {}
