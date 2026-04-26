package com.logicveda.marketplace.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private String customerId;
    private List<CartItemDTO> items;
    private int itemCount;
    private double subtotal;
    private double tax;
    private double shipping;
    private double total;
    private String estimatedDelivery;
    private long updatedAt;
}
