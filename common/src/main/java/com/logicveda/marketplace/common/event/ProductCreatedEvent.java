package com.logicveda.marketplace.common.event;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a product is created
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID productId;
    private UUID vendorId;
    private String productName;
    private String sku;
    private String category;
    private LocalDateTime createdAt;

    public String getEventId() {
        return "product.created." + productId;
    }
}
