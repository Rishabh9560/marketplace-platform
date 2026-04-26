package com.logicveda.marketplace.product.controller;

import com.logicveda.marketplace.common.security.JwtUserPrincipal;
import com.logicveda.marketplace.product.dto.CategoryResponse;
import com.logicveda.marketplace.product.dto.CreateProductRequest;
import com.logicveda.marketplace.product.dto.CreateVariantRequest;
import com.logicveda.marketplace.product.dto.ImageResponse;
import com.logicveda.marketplace.product.dto.ProductResponse;
import com.logicveda.marketplace.product.dto.UpdateProductRequest;
import com.logicveda.marketplace.product.dto.UpdateVariantRequest;
import com.logicveda.marketplace.product.dto.VariantResponse;
import com.logicveda.marketplace.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    // ============= PUBLIC ENDPOINTS (LISTING & SEARCH) =============

    @GetMapping("/{productId}")
    @Operation(summary = "Get product details", description = "Retrieve full product details including variants and images")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProduct(
        @Parameter(description = "Product ID", required = true)
        @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve all products in a category")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
        @Parameter(description = "Category ID", required = true)
        @PathVariable UUID categoryId,
        @Parameter(description = "Page number (0-indexed)")
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by keyword")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
        @Parameter(description = "Search keyword", required = true)
        @RequestParam String keyword,
        @Parameter(description = "Page number")
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }

    @GetMapping("/{productId}/variants")
    @Operation(summary = "Get product variants", description = "Retrieve all variants for a product")
    public ResponseEntity<List<VariantResponse>> getVariants(
        @Parameter(description = "Product ID", required = true)
        @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductVariants(productId));
    }

    @GetMapping("/{productId}/images")
    @Operation(summary = "Get product images", description = "Retrieve all images for a product")
    public ResponseEntity<List<ImageResponse>> getImages(
        @Parameter(description = "Product ID", required = true)
        @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductImages(productId));
    }

    @GetMapping("/categories/all")
    @Operation(summary = "Get all categories", description = "Retrieve all product categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Get category", description = "Retrieve category details")
    public ResponseEntity<CategoryResponse> getCategory(
        @Parameter(description = "Category ID", required = true)
        @PathVariable UUID categoryId) {
        return ResponseEntity.ok(productService.getCategoryById(categoryId));
    }

    // ============= VENDOR ENDPOINTS (PRODUCT MANAGEMENT) =============

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Create product", description = "Create a new product (vendor only)")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        ProductResponse response = productService.createProduct(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update product", description = "Update product details")
    public ResponseEntity<ProductResponse> updateProduct(
        @Parameter(description = "Product ID", required = true)
        @PathVariable UUID productId,
        @Valid @RequestBody UpdateProductRequest request,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        ProductResponse response = productService.updateProduct(productId, request, principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vendor/my-products")
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get my products", description = "Retrieve vendor's own products")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
        @Parameter(description = "Page number")
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.getVendorProducts(principal.getUserId(), pageable));
    }

    @GetMapping("/vendor/low-stock")
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get low stock variants", description = "Retrieve variants running low on inventory")
    public ResponseEntity<List<VariantResponse>> getLowStockVariants(
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(productService.getLowStockVariants(principal.getUserId()));
    }

    // ============= VARIANT MANAGEMENT =============

    @PutMapping("/variants/{variantId}")
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update variant", description = "Update variant details")
    public ResponseEntity<VariantResponse> updateVariant(
        @Parameter(description = "Variant ID", required = true)
        @PathVariable UUID variantId,
        @Valid @RequestBody UpdateVariantRequest request,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        VariantResponse response = productService.updateVariant(variantId, request, principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/variants/{variantId}/adjust-inventory")
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Adjust inventory", description = "Increase or decrease variant stock")
    public ResponseEntity<VariantResponse> adjustInventory(
        @Parameter(description = "Variant ID", required = true)
        @PathVariable UUID variantId,
        @Parameter(description = "Quantity to add/subtract", required = true)
        @RequestParam int quantity,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        VariantResponse response = productService.adjustInventory(variantId, quantity, principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/variants/{variantId}")
    @Operation(summary = "Get variant", description = "Retrieve variant details")
    public ResponseEntity<VariantResponse> getVariant(
        @Parameter(description = "Variant ID", required = true)
        @PathVariable UUID variantId) {
        return ResponseEntity.ok(productService.getVariantById(variantId));
    }

    // ============= IMAGE MANAGEMENT =============

    @PostMapping("/images/{imageId}/set-primary")
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Set primary image", description = "Set an image as primary for product")
    public ResponseEntity<Void> setPrimaryImage(
        @Parameter(description = "Image ID", required = true)
        @PathVariable UUID imageId,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        productService.setPrimaryImage(imageId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasAnyRole('VENDOR')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete image", description = "Remove image from product")
    public ResponseEntity<Void> deleteImage(
        @Parameter(description = "Image ID", required = true)
        @PathVariable UUID imageId,
        Authentication authentication) {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        productService.deleteImage(imageId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}

