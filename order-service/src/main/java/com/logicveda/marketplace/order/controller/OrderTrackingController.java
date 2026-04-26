package com.logicveda.marketplace.order.controller;

import com.logicveda.marketplace.order.service.OrderTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * WebSocket Controller for Real-Time Order Tracking
 * Enables customers to receive live updates on order status
 * 
 * Connection URL: ws://localhost:8003/ws/tracking
 * Subscribe to: /topic/orders/{orderId}
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Tracking WebSocket", description = "Real-time order tracking via WebSocket")
public class OrderTrackingController {

    private final OrderTrackingService trackingService;

    /**
     * Subscribe to order tracking updates
     * WebSocket endpoint: /ws/tracking
     * Message mapping: /app/tracking/{orderId}/subscribe
     */
    @MessageMapping("/tracking/{orderId}/subscribe")
    @SubscribeMapping("/topic/orders/{orderId}")
    public void subscribeToOrderTracking(
            @DestinationVariable String orderId,
            SimpMessageHeaderAccessor headerAccessor) {
        
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId == null) {
            userId = "anonymous-" + System.currentTimeMillis();
        }

        log.info("User {} subscribed to order tracking: {}", userId, orderId);
        trackingService.registerUserForOrder(orderId, userId);
        
        // Store userId in session for later use
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("orderId", orderId);
    }

    /**
     * Unsubscribe from order tracking
     * Message mapping: /app/tracking/{orderId}/unsubscribe
     */
    @MessageMapping("/tracking/{orderId}/unsubscribe")
    public void unsubscribeFromOrderTracking(
            @DestinationVariable String orderId,
            SimpMessageHeaderAccessor headerAccessor) {
        
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            log.info("User {} unsubscribed from order tracking: {}", userId, orderId);
            trackingService.unregisterUserForOrder(orderId, userId);
        }
    }

    /**
     * Receive tracking updates for an order
     * This is sent via MessageTemplate when status changes
     * Example: 
     * {
     *   "orderId": "order-123",
     *   "status": "SHIPPED",
     *   "message": "Your order has been shipped",
     *   "timestamp": 1234567890
     * }
     */
    @MessageMapping("/tracking/{orderId}/update")
    @SendTo("/topic/orders/{orderId}")
    public OrderTrackingService.OrderStatusUpdateEvent trackingUpdate(
            @DestinationVariable String orderId,
            OrderTrackingService.OrderStatusUpdateEvent update) {
        
        log.info("Tracking update received for order: {}", orderId);
        return update;
    }

    // ============= REST ENDPOINTS FOR TRACKING INFO =============

    /**
     * Get current order tracking status via REST
     */
    @RestController
    @RequestMapping("/api/v1/orders/{orderId}/tracking")
    @RequiredArgsConstructor
    @Slf4j
    public static class OrderTrackingRestController {
        
        private final OrderTrackingService trackingService;

        @GetMapping("/status")
        @Operation(summary = "Get tracking status", description = "Get current tracking status for order")
        public ResponseEntity<Map<String, Object>> getTrackingStatus(
                @PathVariable String orderId) {
            
            log.info("Fetching tracking status for order: {}", orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("activeListeners", trackingService.getActiveListenerCount(orderId));
            response.put("hasActiveListeners", trackingService.hasActiveListeners(orderId));
            response.put("lastUpdate", System.currentTimeMillis());
            response.put("message", "Connect to WebSocket endpoint for real-time updates");
            response.put("wsEndpoint", "ws://localhost:8003/ws/tracking");
            response.put("subscription", "/app/tracking/" + orderId + "/subscribe");

            return ResponseEntity.ok(response);
        }

        @GetMapping("/listeners-count")
        @Operation(summary = "Get listener count", description = "Get number of active WebSocket listeners for order")
        public ResponseEntity<Map<String, Object>> getListenerCount(
                @PathVariable String orderId) {
            
            int count = trackingService.getActiveListenerCount(orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("activeListeners", count);

            return ResponseEntity.ok(response);
        }
    }
}

/**
 * WebSocket Configuration for Spring
 * Needed in main application config:
 * 
 * @Configuration
 * @EnableWebSocketMessageBroker
 * public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 *
 *     @Override
 *     public void configureMessageBroker(MessageBrokerRegistry config) {
 *         config.enableSimpleBroker("/topic", "/queue");
 *         config.setApplicationDestinationPrefixes("/app");
 *     }
 *
 *     @Override
 *     public void registerStompEndpoints(StompEndpointRegistry registry) {
 *         registry.addEndpoint("/ws/tracking")
 *             .setAllowedOrigins("*")
 *             .withSockJS();
 *     }
 * }
 */
