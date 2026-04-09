package com.logicveda.marketplace.order.controller;

import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import com.logicveda.marketplace.order.entity.Order;
import com.logicveda.marketplace.order.entity.OrderItem;
import com.logicveda.marketplace.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    // ============= CUSTOMER ENDPOINTS =============

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Create order", description = "Create a new order from checkout")
    public ResponseEntity<Order> createOrder(
        @RequestBody OrderService.CreateOrderRequest request,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Order order = orderService.createOrder(principal.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get my orders", description = "Retrieve current user's orders")
    public ResponseEntity<Page<Order>> getMyOrders(
        @Parameter(description = "Page number")
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getCustomerOrders(principal.getUserId(), pageable));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR', 'ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get order details", description = "Retrieve specific order details with items")
    public ResponseEntity<Order> getOrder(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Order order = orderService.getOrderById(orderId, principal.getUserId());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/confirm-payment")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Confirm payment", description = "Confirm payment received for order")
    public ResponseEntity<Order> confirmPayment(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Order order = orderService.confirmPayment(orderId, principal.getUserId());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Cancel order", description = "Cancel pending or confirmed order")
    public ResponseEntity<Order> cancelOrder(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId,
        @Parameter(description = "Cancellation reason", required = true)
        @RequestParam String reason,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Order order = orderService.cancelOrder(orderId, principal.getUserId(), reason);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/shipping")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update shipping info", description = "Update shipping address before shipment")
    public ResponseEntity<Order> updateShippingInfo(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId,
        @RequestBody OrderService.UpdateShippingRequest request,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Order order = orderService.updateShippingInfo(orderId, principal.getUserId(), request);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'VENDOR', 'ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get order items", description = "Retrieve all items in an order")
    public ResponseEntity<List<OrderItem>> getOrderItems(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId) {

        List<OrderItem> items = orderService.getOrderItems(orderId);
        return ResponseEntity.ok(items);
    }

    // ============= VENDOR ENDPOINTS =============

    @GetMapping("/vendor/pending-fulfillment")
    @PreAuthorize("hasRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get pending items", description = "Get vendor's items pending fulfillment")
    public ResponseEntity<List<OrderItem>> getPendingFulfillment(
        @Parameter(description = "Page number")
        @RequestParam(defaultValue = "0") @Min(0) int page,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        // TODO: Implement vendor-specific pending items query
        return ResponseEntity.ok(List.of());
    }

    // ============= ADMIN ENDPOINTS =============

    @PostMapping("/{orderId}/admin/status")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update order status", description = "Update order status (admin only)")
    public ResponseEntity<Order> updateOrderStatus(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId,
        @Parameter(description = "New status", required = true)
        @RequestParam Order.OrderStatus status) {

        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/admin/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update tracking", description = "Update shipping tracking information")
    public ResponseEntity<Order> updateTracking(
        @Parameter(description = "Order ID", required = true)
        @PathVariable UUID orderId,
        @Parameter(description = "Tracking number", required = true)
        @RequestParam String trackingNumber,
        @Parameter(description = "Shipping carrier", required = true)
        @RequestParam String carrier) {

        Order order = orderService.updateTrackingInfo(orderId, trackingNumber, carrier);
        return ResponseEntity.ok(order);
    }
}
