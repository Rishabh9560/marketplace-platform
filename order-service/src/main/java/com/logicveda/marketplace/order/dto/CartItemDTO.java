package com.logicveda.marketplace.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private String itemId;
    private String variantId;
    private String productId;
    private String productName;
    private String sku;
    private int quantity;
    private double price;
    private double totalPrice;
    private String vendorId;
    private String vendorName;
    private long addedAt;
}
