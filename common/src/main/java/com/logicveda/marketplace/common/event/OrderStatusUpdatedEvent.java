package com.logicveda.marketplace.common.event;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when order status is updated
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatusUpdatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID orderId;
    private UUID customerId;
    private UUID vendorId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime updatedAt;
    private String reason;
    private String customerEmail;

    public String getEventId() {
        return "order.status.updated." + orderId;
    }
}
