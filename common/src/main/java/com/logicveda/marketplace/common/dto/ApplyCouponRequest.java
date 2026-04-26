package com.logicveda.marketplace.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * Request DTO for applying coupon.
 */
@Schema(name = "ApplyCouponRequest", description = "Apply coupon code to cart")
public record ApplyCouponRequest(
    @Schema(description = "Coupon code")
    @NotBlank(message = "Coupon code cannot be blank")
    String couponCode
) implements Serializable {}
