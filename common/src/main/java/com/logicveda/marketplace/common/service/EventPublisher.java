package com.logicveda.marketplace.common.service;

import com.logicveda.marketplace.common.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service to publish events to Kafka topics
 * Used across all microservices to emit domain events
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Publish order created event
     */
    public void publishOrderCreated(OrderCreatedEvent event) {
        publishEvent("order.created", event.getEventId(), event);
    }

    /**
     * Publish order status updated event
     */
    public void publishOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        publishEvent("order.status.updated", event.getEventId(), event);
    }

    /**
     * Publish payment processed event
     */
    public void publishPaymentProcessed(PaymentProcessedEvent event) {
        publishEvent("payment.processed", event.getEventId(), event);
    }

    /**
     * Publish product created event
     */
    public void publishProductCreated(ProductCreatedEvent event) {
        publishEvent("product.created", event.getEventId(), event);
    }

    /**
     * Publish vendor KYC event
     */
    public void publishVendorKyc(Object event) {
        publishEvent("vendor.kyc", "vendor.kyc.event", event);
    }

    /**
     * Generic event publisher
     */
    private void publishEvent(String topic, String partitionKey, Object event) {
        try {
            Message<Object> message = MessageBuilder
                .withPayload(event)
                .setHeader("kafka_messageKey", partitionKey)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("timestamp", LocalDateTime.now().format(dateFormatter))
                .build();

            kafkaTemplate.send(message);
            log.info("Event published to topic={}, key={}, event={}", topic, partitionKey, event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Failed to publish event to topic={}, key={}", topic, partitionKey, e);
            // In production, implement retry logic or dead-letter queue
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
