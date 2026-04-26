package com.logicveda.marketplace.common.event;

import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a new order is created
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID orderId;
    private UUID customerId;
    private UUID vendorId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private String customerEmail;
    private String vendorEmail;
    private Integer itemCount;

    public String getEventId() {
        return "order.created." + orderId;
    }
}
