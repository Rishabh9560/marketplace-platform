package com.logicveda.marketplace.order.repository;

import com.logicveda.marketplace.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for OrderItem entity
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    
    /**
     * Find all items in an order
     */
    List<OrderItem> findByOrderId(UUID orderId);
    
    /**
     * Find items for a specific vendor in an order
     */
    List<OrderItem> findByOrderIdAndVendorId(UUID orderId, UUID vendorId);
    
    /**
     * Find items by vendor ID (for vendor dashboard)
     */
    List<OrderItem> findByVendorId(UUID vendorId);
}
