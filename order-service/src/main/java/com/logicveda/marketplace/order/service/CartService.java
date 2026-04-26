package com.logicveda.marketplace.order.service;

import com.logicveda.marketplace.order.dto.AddToCartRequest;
import com.logicveda.marketplace.order.dto.CartDTO;
import com.logicveda.marketplace.order.dto.CartItemDTO;
import com.logicveda.marketplace.order.dto.UpdateCartItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Cart Service
 * Handles shopping cart operations using Redis for temporary storage
 * Carts are stored with a 7-day TTL
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_PREFIX = "cart:";
    private static final long CART_TTL_DAYS = 7;

    /**
     * Get user's cart
     */
    public CartDTO getCart(UUID customerId) {
        log.info("Fetching cart for customer: {}", customerId);
        
        Map<Object, Object> rawItems = redisTemplate.opsForHash().entries(getCartKey(customerId));
        Map<String, CartItemDTO> cartItems = new HashMap<>();
        if (rawItems != null) {
            rawItems.forEach((k, v) -> {
                if (v instanceof CartItemDTO) {
                    cartItems.put(k.toString(), (CartItemDTO) v);
                }
            });
        }
        
        if (cartItems.isEmpty()) {
            return new CartDTO(
                customerId.toString(),
                new ArrayList<>(),
                0,
                0,
                0,
                0,
                0,
                "5-7 business days",
                System.currentTimeMillis()
            );
        }

        return buildCartDTO(customerId, cartItems);
    }

    /**
     * Add item to cart
     */
    public CartDTO addToCart(UUID customerId, AddToCartRequest request) {
        log.info("Adding item to cart for customer: {} - Variant: {}", customerId, request.getVariantId());
        
        String cartKey = getCartKey(customerId);
        String itemKey = "item:" + UUID.randomUUID().toString();

        CartItemDTO cartItem = CartItemDTO.builder()
            .itemId(itemKey)
            .variantId(request.getVariantId())
            .productId(request.getVariantId())
            .productName("Product")
            .sku("SKU-" + request.getVariantId().substring(0, 8))
            .quantity(request.getQuantity())
            .price(100.0) // Default price, would be fetched from product service in production
            .totalPrice(request.getQuantity() * 100.0)
            .vendorId(request.getVendorId())
            .vendorName("Vendor")
            .addedAt(System.currentTimeMillis())
            .build();

        redisTemplate.opsForHash().put(cartKey, itemKey, cartItem);
        redisTemplate.expire(cartKey, CART_TTL_DAYS, TimeUnit.DAYS);

        log.info("Item added to cart: {}", itemKey);

        Map<Object, Object> rawItems = redisTemplate.opsForHash().entries(cartKey);
        Map<String, CartItemDTO> cartItems = new HashMap<>();
        if (rawItems != null) {
            rawItems.forEach((k, v) -> {
                if (v instanceof CartItemDTO) {
                    cartItems.put(k.toString(), (CartItemDTO) v);
                }
            });
        }

        return buildCartDTO(customerId, cartItems);
    }

    /**
     * Update cart item quantity
     */
    public CartDTO updateCartItem(UUID customerId, UpdateCartItemRequest request) {
        log.info("Updating cart item: {} for customer: {}", request.getItemId(), customerId);
        
        String cartKey = getCartKey(customerId);
        CartItemDTO cartItem = (CartItemDTO) redisTemplate.opsForHash().get(cartKey, request.getItemId());

        if (cartItem == null) {
            log.warn("Cart item not found: {}", request.getItemId());
            throw new RuntimeException("Cart item not found");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem.setTotalPrice(request.getQuantity() * cartItem.getPrice());
        
        redisTemplate.opsForHash().put(cartKey, request.getItemId(), cartItem);
        redisTemplate.expire(cartKey, CART_TTL_DAYS, TimeUnit.DAYS);

        Map<Object, Object> rawItems = redisTemplate.opsForHash().entries(cartKey);
        Map<String, CartItemDTO> cartItems = new HashMap<>();
        if (rawItems != null) {
            rawItems.forEach((k, v) -> {
                if (v instanceof CartItemDTO) {
                    cartItems.put(k.toString(), (CartItemDTO) v);
                }
            });
        }

        return buildCartDTO(customerId, cartItems);
    }

    /**
     * Remove item from cart
     */
    public CartDTO removeFromCart(UUID customerId, String itemId) {
        log.info("Removing item from cart: {} for customer: {}", itemId, customerId);
        
        String cartKey = getCartKey(customerId);
        redisTemplate.opsForHash().delete(cartKey, itemId);

        Map<Object, Object> rawItems = redisTemplate.opsForHash().entries(cartKey);
        Map<String, CartItemDTO> cartItems = new HashMap<>();
        if (rawItems != null) {
            rawItems.forEach((k, v) -> {
                if (v instanceof CartItemDTO) {
                    cartItems.put(k.toString(), (CartItemDTO) v);
                }
            });
        }

        if (cartItems.isEmpty()) {
            log.info("Cart is now empty for customer: {}", customerId);
        }

        return buildCartDTO(customerId, cartItems.isEmpty() ? new HashMap<>() : cartItems);
    }

    /**
     * Clear entire cart
     */
    public void clearCart(UUID customerId) {
        log.info("Clearing cart for customer: {}", customerId);
        String cartKey = getCartKey(customerId);
        redisTemplate.delete(cartKey);
    }

    /**
     * Get cart item count
     */
    public int getCartItemCount(UUID customerId) {
        String cartKey = getCartKey(customerId);
        Long size = redisTemplate.opsForHash().size(cartKey);
        return (size != null ? size.intValue() : 0);
    }

    /**
     * Build CartDTO from cart items
     */
    private CartDTO buildCartDTO(UUID customerId, Map<String, CartItemDTO> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return new CartDTO(
                customerId.toString(),
                new ArrayList<>(),
                0,
                0,
                0,
                0,
                0,
                "5-7 business days",
                System.currentTimeMillis()
            );
        }

        List<CartItemDTO> items = cartItems.values().stream()
            .sorted(Comparator.comparingLong(CartItemDTO::getAddedAt))
            .collect(Collectors.toList());

        double subtotal = items.stream()
            .mapToDouble(CartItemDTO::getTotalPrice)
            .sum();

        double tax = subtotal * 0.18; // 18% tax
        double shipping = subtotal > 500 ? 0 : 50; // Free shipping for orders above 500
        double total = subtotal + tax + shipping;

        return new CartDTO(
            customerId.toString(),
            items,
            items.size(),
            subtotal,
            tax,
            shipping,
            total,
            "5-7 business days",
            System.currentTimeMillis()
        );
    }

    private String getCartKey(UUID customerId) {
        return CART_PREFIX + customerId.toString();
    }
}
