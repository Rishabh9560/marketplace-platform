package com.logicveda.marketplace.order.repository;

import com.logicveda.marketplace.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Find all orders for a customer
     */
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    
    /**
     * Find pending orders (awaiting payment)
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    /**
     * Find orders by customer and status
     */
    Page<Order> findByCustomerIdAndStatus(UUID customerId, Order.OrderStatus status, Pageable pageable);
}
