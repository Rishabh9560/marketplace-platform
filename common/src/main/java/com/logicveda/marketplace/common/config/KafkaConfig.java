package com.logicveda.marketplace.common.config;

import com.logicveda.marketplace.common.event.*;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Configuration for Event Streaming
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ============ Topics ============

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order.created")
            .partitions(3)
            .replicas(1)
            .config("retention.ms", "604800000") // 7 days
            .build();
    }

    @Bean
    public NewTopic orderStatusUpdatedTopic() {
        return TopicBuilder.name("order.status.updated")
            .partitions(3)
            .replicas(1)
            .config("retention.ms", "604800000")
            .build();
    }

    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name("payment.processed")
            .partitions(3)
            .replicas(1)
            .config("retention.ms", "604800000")
            .build();
    }

    @Bean
    public NewTopic productCreatedTopic() {
        return TopicBuilder.name("product.created")
            .partitions(3)
            .replicas(1)
            .config("retention.ms", "604800000")
            .build();
    }

    @Bean
    public NewTopic vendorKycTopic() {
        return TopicBuilder.name("vendor.kyc")
            .partitions(2)
            .replicas(1)
            .config("retention.ms", "2592000000") // 30 days
            .build();
    }

    // ============ Admin ============

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    // ============ Producer Configuration ============

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ============ Consumer Configuration ============

    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory() {
        return buildConsumerFactory(OrderCreatedEvent.class);
    }

    @Bean
    public ConsumerFactory<String, OrderStatusUpdatedEvent> orderStatusUpdatedConsumerFactory() {
        return buildConsumerFactory(OrderStatusUpdatedEvent.class);
    }

    @Bean
    public ConsumerFactory<String, PaymentProcessedEvent> paymentProcessedConsumerFactory() {
        return buildConsumerFactory(PaymentProcessedEvent.class);
    }

    @Bean
    public ConsumerFactory<String, ProductCreatedEvent> productCreatedConsumerFactory() {
        return buildConsumerFactory(ProductCreatedEvent.class);
    }

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> eventClass) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "marketplace-" + eventClass.getSimpleName().toLowerCase());
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, eventClass.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.logicveda.marketplace.common.event");
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), 
            new JsonDeserializer<>(eventClass, false));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrderCreatedEvent>> 
        orderCreatedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        factory.setConcurrency(3);
        factory.setConsumerFactory(orderCreatedConsumerFactory());
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrderStatusUpdatedEvent>> 
        orderStatusUpdatedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderStatusUpdatedEvent> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        factory.setConcurrency(3);
        factory.setConsumerFactory(orderStatusUpdatedConsumerFactory());
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PaymentProcessedEvent>> 
        paymentProcessedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentProcessedEvent> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());
        factory.setConcurrency(3);
        factory.setConsumerFactory(paymentProcessedConsumerFactory());
        return factory;
    }
}
