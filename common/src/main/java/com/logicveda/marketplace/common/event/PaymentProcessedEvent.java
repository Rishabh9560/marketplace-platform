package com.logicveda.marketplace.common.event;

import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a payment is processed
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentProcessedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private UUID vendorId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime processedAt;
    private String customerEmail;
    private String vendorEmail;

    public String getEventId() {
        return "payment.processed." + paymentId;
    }
}
