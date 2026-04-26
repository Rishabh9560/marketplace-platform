package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Price Update DTO for product listing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Price Update Request")
public class PriceUpdateDTO {

    @NotNull(message = "Vendor price is required")
    @DecimalMin(value = "0.01", message = "Vendor price must be greater than 0")
    @Schema(description = "New vendor selling price", example = "79.99")
    private BigDecimal vendorPrice;

    @DecimalMin(value = "0.00", message = "Discount must be >= 0")
    @DecimalMax(value = "100.00", message = "Discount must be <= 100")
    @Schema(description = "Discount percentage", example = "20.00")
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.00", message = "Shipping cost must be >= 0")
    @Schema(description = "New shipping cost")
    private BigDecimal shippingCost;

    @Schema(description = "Free shipping flag")
    private Boolean freeShipping;

    @Schema(description = "Reason for price change", example = "Seasonal promotion")
    private String reason;
}
