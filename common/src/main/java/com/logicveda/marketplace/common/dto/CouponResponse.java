package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Response DTO for coupon validation.
 */
@Schema(name = "CouponResponse", description = "Coupon validation response")
public record CouponResponse(
    @Schema(description = "Coupon ID")
    String couponId,

    @Schema(description = "Coupon code")
    String code,

    @Schema(description = "Discount percentage")
    BigDecimal discountPercentage,

    @Schema(description = "Maximum discount amount")
    BigDecimal maxDiscount,

    @Schema(description = "Minimum order value")
    BigDecimal minOrderValue,

    @Schema(description = "Valid from date")
    String validFrom,

    @Schema(description = "Valid till date")
    String validTill,

    @Schema(description = "Is active")
    Boolean isActive
) implements Serializable {}
