package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for cart item.
 */
@Schema(name = "CartItemResponse", description = "Cart item details")
public record CartItemResponse(
    @Schema(description = "Cart item ID")
    String itemId,

    @Schema(description = "Product variant ID")
    UUID variantId,

    @Schema(description = "Product ID")
    UUID productId,

    @Schema(description = "Product name")
    String productName,

    @Schema(description = "SKU")
    String sku,

    @Schema(description = "Quantity in cart")
    Integer quantity,

    @Schema(description = "Unit price")
    BigDecimal price,

    @Schema(description = "Total price for this item")
    BigDecimal totalPrice,

    @Schema(description = "Vendor ID")
    UUID vendorId,

    @Schema(description = "Vendor name")
    String vendorName,

    @Schema(description = "Added at timestamp")
    Long addedAt
) implements Serializable {}
