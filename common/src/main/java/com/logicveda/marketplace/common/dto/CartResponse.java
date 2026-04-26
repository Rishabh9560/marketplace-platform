package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for complete cart.
 */
@Schema(name = "CartResponse", description = "Complete shopping cart")
public record CartResponse(
    @Schema(description = "Customer ID")
    UUID customerId,

    @Schema(description = "Cart items")
    List<CartItemResponse> items,

    @Schema(description = "Total items count")
    Integer itemCount,

    @Schema(description = "Subtotal (sum of item prices)")
    BigDecimal subtotal,

    @Schema(description = "Tax amount")
    BigDecimal tax,

    @Schema(description = "Shipping cost")
    BigDecimal shipping,

    @Schema(description = "Total amount")
    BigDecimal total,

    @Schema(description = "Estimated delivery date")
    String estimatedDelivery,

    @Schema(description = "Last updated timestamp")
    Long updatedAt
) implements Serializable {}
