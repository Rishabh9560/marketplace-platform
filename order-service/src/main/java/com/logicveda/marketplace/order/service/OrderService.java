package com.logicveda.marketplace.order.service;

import com.logicveda.marketplace.common.exception.BusinessException;
import com.logicveda.marketplace.common.exception.ResourceNotFoundException;
import com.logicveda.marketplace.common.event.OrderCreatedEvent;
import com.logicveda.marketplace.common.event.OrderStatusUpdatedEvent;
import com.logicveda.marketplace.common.service.EventPublisher;
import com.logicveda.marketplace.order.entity.Order;
import com.logicveda.marketplace.order.entity.OrderItem;
import com.logicveda.marketplace.order.repository.OrderItemRepository;
import com.logicveda.marketplace.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Order Service
 * Handles order creation, management, and state transitions
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EventPublisher eventPublisher;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    // ============= ORDER MANAGEMENT =============

    /**
     * Generate order number in format: ORD-YYYYMMDD-XXXXX
     */
    public String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        long timestamp = System.currentTimeMillis();
        String suffix = String.format("%05d", timestamp % 100000);
        String datePrefix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("ORD-%s-%s", datePrefix, suffix);
    }

    /**
     * Create a new order from checkout data
     */
    public Order createOrder(UUID customerId, CreateOrderRequest request) {
        log.info("Creating order for customer: {}", customerId);

        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }

        // Calculate totals
        BigDecimal subtotal = request.items().stream()
            .map(item -> item.getLineTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = subtotal
            .add(request.shippingCost() != null ? request.shippingCost() : BigDecimal.ZERO)
            .add(request.taxAmount() != null ? request.taxAmount() : BigDecimal.ZERO)
            .subtract(request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO);

        // Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setSubtotal(subtotal);
        order.setShippingCost(request.shippingCost() != null ? request.shippingCost() : BigDecimal.ZERO);
        order.setTaxAmount(request.taxAmount() != null ? request.taxAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(request.shippingAddress());
        order.setShippingCity(request.shippingCity());
        order.setShippingState(request.shippingState());
        order.setShippingPostalCode(request.shippingPostalCode());
        order.setBillingAddress(request.billingAddress());
        order.setBillingCity(request.billingCity());
        order.setBillingState(request.billingState());
        order.setBillingPostalCode(request.billingPostalCode());
        order.setNotes(request.notes());

        order = orderRepository.save(order);
        
        // Create order items
        for (CreateOrderItemRequest itemRequest : request.items()) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(itemRequest.getProductId());
            item.setVariantId(itemRequest.getVariantId());
            item.setProductName(itemRequest.getProductName());
            item.setSku(itemRequest.getSku());
            item.setVendorId(itemRequest.getVendorId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setLineTotal(itemRequest.getLineTotal());
            item.setFulfillmentStatus(OrderItem.FulfillmentStatus.PENDING);

            orderItemRepository.save(item);
        }

        log.info("Order created successfully: {} with ID: {}", order.getOrderNumber(), order.getId());

        // TODO: Publish OrderCreatedEvent to Kafka for notifications and fulfillment
        // OrderCreatedEvent event = OrderCreatedEvent.builder()
        //     .orderId(order.getId().toString())
        //     .customerId(customerId.toString())
        //     .vendorId(request.items().get(0).getVendorId().toString())  // Primary vendor
        //     .totalAmount(totalAmount)
        //     .status("PENDING")
        //     .createdAt(LocalDateTime.now())
        //     .itemCount(request.items().size())
        //     .build();
        // eventPublisher.publishOrderCreated(event);
        log.info("Order publishing skipped for order: {} (TODO: implement)", order.getId());

        return order;
    }

    /**
     * Get order by ID (with ownership check)
     */
    public Order getOrderById(UUID orderId, UUID customerId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getCustomerId().equals(customerId)) {
            throw new BusinessException("Unauthorized: You don't own this order");
        }

        return order;
    }

    /**
     * Get order by order number
     */
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
    }

    /**
     * Get customer's orders
     */
    public Page<Order> getCustomerOrders(UUID customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    /**
     * Mark order as paid (after payment confirmation)
     */
    public Order confirmPayment(UUID orderId, UUID customerId) {
        Order order = getOrderById(orderId, customerId);

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException("Can only confirm payment for PENDING orders");
        }

        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setPaidAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);
        log.info("Payment confirmed for order: {}", orderId);

        // TODO: Publish OrderConfirmed event to Kafka for fulfillment service
        return order;
    }

    /**
     * Cancel order (customer cancellation)
     */
    public Order cancelOrder(UUID orderId, UUID customerId, String reason) {
        Order order = getOrderById(orderId, customerId);

        if (!order.canBeCancelled()) {
            throw new BusinessException(
                "Cannot cancel order in " + order.getStatus() + " status. Can only cancel PENDING or CONFIRMED orders");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setNotes((order.getNotes() != null ? order.getNotes() + "\n" : "") + "Cancelled: " + reason);
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);
        log.info("Order cancelled: {}", orderId);

        // TODO: Publish OrderCancelled event to Kafka for refund processing
        return order;
    }

    /**
     * Update order shipping information
     */
    public Order updateShippingInfo(UUID orderId, UUID customerId, UpdateShippingRequest request) {
        Order order = getOrderById(orderId, customerId);

        order.setShippingAddress(request.shippingAddress() != null ? request.shippingAddress() : order.getShippingAddress());
        order.setShippingCity(request.shippingCity() != null ? request.shippingCity() : order.getShippingCity());
        order.setShippingState(request.shippingState() != null ? request.shippingState() : order.getShippingState());
        order.setShippingPostalCode(request.shippingPostalCode() != null ? request.shippingPostalCode() : order.getShippingPostalCode());

        order = orderRepository.save(order);
        log.info("Shipping info updated for order: {}", orderId);

        return order;
    }

    /**
     * Update order status (internal/admin use)
     */
    public Order updateOrderStatus(UUID orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Order.OrderStatus previousStatus = order.getStatus();

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        // Set time stamps based on status
        if (newStatus == Order.OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
        } else if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        } else if (newStatus == Order.OrderStatus.CANCELLED) {
            order.setCancelledAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);
        log.info("Order status updated to {} for order: {}", newStatus, orderId);

        // TODO: Publish OrderStatusUpdatedEvent to Kafka
        // OrderStatusUpdatedEvent event = OrderStatusUpdatedEvent.builder()
        //     .orderId(orderId.toString())
        //     .customerId(order.getCustomerId().toString())
        //     .previousStatus(previousStatus.toString())
        //     .newStatus(newStatus.toString())
        //     .updatedAt(LocalDateTime.now())
        //     .reason("Order status updated to " + newStatus)
        //     .build();
        // eventPublisher.publishOrderStatusUpdated(event);
        log.info("Order status publishing skipped for order: {} ({} -> {})", orderId, previousStatus, newStatus);

        return order;
    }

    /**
     * Update tracking information
     */
    public Order updateTrackingInfo(UUID orderId, String trackingNumber, String carrier) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setTrackingNumber(trackingNumber);
        order.setShippingCarrier(carrier);
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);
        log.info("Tracking info updated for order: {} - Carrier: {}, Tracking: {}", orderId, carrier, trackingNumber);

        return order;
    }

    // ============= ORDER ITEM MANAGEMENT =============

    /**
     * Get all items in an order
     */
    public List<OrderItem> getOrderItems(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    /**
     * Get vendor's items in an order
     */
    public List<OrderItem> getVendorOrderItems(UUID orderId, UUID vendorId) {
        return orderItemRepository.findByOrderIdAndVendorId(orderId, vendorId);
    }

    /**
     * Update fulfillment status for order item
     */
    public OrderItem updateItemFulfillmentStatus(UUID itemId, OrderItem.FulfillmentStatus status) {
        OrderItem item = orderItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        item.setFulfillmentStatus(status);
        item = orderItemRepository.save(item);

        log.info("Item fulfillment status updated: {} to {}", itemId, status);
        return item;
    }

    // ============= DTOs =============

    public record CreateOrderRequest(
        List<CreateOrderItemRequest> items,
        BigDecimal shippingCost,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        String shippingAddress,
        String shippingCity,
        String shippingState,
        String shippingPostalCode,
        String billingAddress,
        String billingCity,
        String billingState,
        String billingPostalCode,
        String notes
    ) {}

    public static class CreateOrderItemRequest {
        private UUID productId;
        private UUID variantId;
        private String productName;
        private String sku;
        private UUID vendorId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;

        public UUID getProductId() { return productId; }
        public UUID getVariantId() { return variantId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public UUID getVendorId() { return vendorId; }
        public Integer getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getLineTotal() { return lineTotal; }
    }

    public record UpdateShippingRequest(
        String shippingAddress,
        String shippingCity,
        String shippingState,
        String shippingPostalCode
    ) {}
}
