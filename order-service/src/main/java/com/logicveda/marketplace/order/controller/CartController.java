package com.logicveda.marketplace.order.controller;

import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import com.logicveda.marketplace.order.dto.CartDTO;
import com.logicveda.marketplace.order.dto.AddToCartRequest;
import com.logicveda.marketplace.order.dto.UpdateCartItemRequest;
import com.logicveda.marketplace.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart", description = "Shopping cart endpoints")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get user's cart", description = "Retrieve the current user's shopping cart")
    public ResponseEntity<CartDTO> getCart(Authentication authentication) {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        CartDTO cart = cartService.getCart(principal.getUserId());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Add item to cart", description = "Add a product variant to the shopping cart")
    public ResponseEntity<CartDTO> addToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication) {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        CartDTO cart = cartService.addToCart(principal.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @PutMapping("/items")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an item in the cart")
    public ResponseEntity<CartDTO> updateCartItem(
            @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        CartDTO cart = cartService.updateCartItem(principal.getUserId(), request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Remove item from cart", description = "Remove a product from the shopping cart")
    public ResponseEntity<CartDTO> removeFromCart(
            @PathVariable String itemId,
            Authentication authentication) {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        CartDTO cart = cartService.removeFromCart(principal.getUserId(), itemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        cartService.clearCart(principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get cart item count", description = "Get the number of items in the cart")
    public ResponseEntity<Object> getCartItemCount(Authentication authentication) {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        int count = cartService.getCartItemCount(principal.getUserId());
        return ResponseEntity.ok(new Object() {
            public final int itemCount = count;
        });
    }
}
