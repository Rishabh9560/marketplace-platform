package com.logicveda.marketplace.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Inventory Update DTO for product listing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Inventory Update Request")
public class InventoryUpdateDTO {

    @NotNull(message = "Quantity available is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Schema(description = "Quantity available", example = "100")
    private Integer quantityAvailable;

    @Min(value = 0, message = "Reorder level cannot be negative")
    @Schema(description = "Reorder level threshold")
    private Integer reorderLevel;

    @Min(value = 1, message = "Reorder quantity must be at least 1")
    @Schema(description = "Reorder quantity")
    private Integer reorderQuantity;

    @Schema(description = "Reason for inventory update", example = "Stock received from supplier")
    private String reason;
}
