package com.logicveda.marketplace.vendor.controller;

import com.logicveda.marketplace.vendor.dto.*;
import com.logicveda.marketplace.vendor.service.ProductListingService;
import com.logicveda.marketplace.vendor.util.ApiResponse;
import com.logicveda.marketplace.vendor.util.PaginationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST Controller for product listing management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
@Tag(name = "Product Listings", description = "Product listing management endpoints")
public class ProductListingController {

    private final ProductListingService listingService;

    /**
     * Create new product listing
     */
    @PostMapping
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Create product listing", description = "Create new product listing for vendor")
    public ResponseEntity<ApiResponse<ProductListingDTO>> createListing(
            @Valid @RequestBody ProductListingDTO listingDTO) {
        log.info("Create listing request - Product: {}", listingDTO.getProductName());

        ProductListingDTO createdListing = listingService.createListing(listingDTO);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(createdListing, "Product listing created successfully"));
    }

    /**
     * Get listing by ID
     */
    @GetMapping("/{listingId}")
    @Operation(summary = "Get listing details", description = "Retrieve product listing by ID")
    public ResponseEntity<ApiResponse<ProductListingDTO>> getListing(
            @PathVariable 
            @Parameter(description = "Listing ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID listingId) {
        log.info("Get listing request: {}", listingId);

        ProductListingDTO listing = listingService.getListingById(listingId);

        return ResponseEntity.ok(ApiResponse.success(listing, "Listing retrieved successfully"));
    }

    /**
     * Update product listing
     */
    @PutMapping("/{listingId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Update listing", description = "Update product listing information")
    public ResponseEntity<ApiResponse<ProductListingDTO>> updateListing(
            @PathVariable UUID listingId,
            @Valid @RequestBody ProductListingDTO listingDTO) {
        log.info("Update listing request: {}", listingId);

        ProductListingDTO updatedListing = listingService.updateListing(listingId, listingDTO);

        return ResponseEntity.ok(ApiResponse.success(updatedListing, "Listing updated successfully"));
    }

    /**
     * Publish listing (DRAFT -> ACTIVE)
     */
    @PostMapping("/{listingId}/publish")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Publish listing", description = "Publish listing to marketplace")
    public ResponseEntity<ApiResponse<ProductListingDTO>> publishListing(
            @PathVariable UUID listingId) {
        log.info("Publish listing request: {}", listingId);

        ProductListingDTO publishedListing = listingService.publishListing(listingId);

        return ResponseEntity.ok(ApiResponse.success(publishedListing, "Listing published successfully"));
    }

    /**
     * Delist product (ACTIVE -> DELISTED)
     */
    @PostMapping("/{listingId}/delist")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Delist product", description = "Remove product from marketplace")
    public ResponseEntity<ApiResponse<ProductListingDTO>> delistListing(
            @PathVariable UUID listingId) {
        log.info("Delist listing request: {}", listingId);

        ProductListingDTO delistedListing = listingService.delistListing(listingId);

        return ResponseEntity.ok(ApiResponse.success(delistedListing, "Listing delisted successfully"));
    }

    /**
     * Update inventory
     */
    @PatchMapping("/{listingId}/inventory")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Update inventory", description = "Update product inventory and reorder levels")
    public ResponseEntity<ApiResponse<ProductListingDTO>> updateInventory(
            @PathVariable UUID listingId,
            @Valid @RequestBody InventoryUpdateDTO inventoryDTO) {
        log.info("Update inventory request: {} - Quantity: {}", listingId, inventoryDTO.getQuantityAvailable());

        ProductListingDTO updatedListing = listingService.updateInventory(listingId, inventoryDTO);

        return ResponseEntity.ok(ApiResponse.success(updatedListing, "Inventory updated successfully"));
    }

    /**
     * Update pricing
     */
    @PatchMapping("/{listingId}/price")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Update pricing", description = "Update product price, discount, and shipping")
    public ResponseEntity<ApiResponse<ProductListingDTO>> updatePrice(
            @PathVariable UUID listingId,
            @Valid @RequestBody PriceUpdateDTO priceDTO) {
        log.info("Update price request: {} - Price: {}", listingId, priceDTO.getVendorPrice());

        ProductListingDTO updatedListing = listingService.updatePrice(listingId, priceDTO);

        return ResponseEntity.ok(ApiResponse.success(updatedListing, "Price updated successfully"));
    }

    /**
     * Get vendor's listings
     */
    @GetMapping("/vendor/{vendorId}")
    @Operation(summary = "Get vendor listings", description = "Retrieve all listings for a vendor")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getVendorListings(
            @PathVariable UUID vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get vendor listings request - Vendor: {}, Page: {}", vendorId, page);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getVendorListings(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Vendor listings retrieved successfully"));
    }

    /**
     * Get vendor's active listings
     */
    @GetMapping("/vendor/{vendorId}/active")
    @Operation(summary = "Get active listings", description = "Retrieve active listings for a vendor")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getVendorActiveListings(
            @PathVariable UUID vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get vendor active listings request - Vendor: {}", vendorId);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getVendorActiveListings(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Active listings retrieved successfully"));
    }

    /**
     * Get low stock listings
     */
    @GetMapping("/vendor/{vendorId}/low-stock")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get low stock items", description = "Retrieve listings with low inventory")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getLowStockListings(
            @PathVariable UUID vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get low stock listings request - Vendor: {}", vendorId);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getLowStockListings(vendorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Low stock listings retrieved successfully"));
    }

    /**
     * Get active marketplace listings
     */
    @GetMapping("/marketplace")
    @Operation(summary = "Get marketplace listings", description = "Retrieve active listings from marketplace")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getActiveMarketplaceListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get active marketplace listings request - Page: {}", page);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getActiveMarketplaceListings(pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Marketplace listings retrieved successfully"));
    }

    /**
     * Get discounted listings
     */
    @GetMapping("/discounted")
    @Operation(summary = "Get discounted items", description = "Retrieve listings with active discounts")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getDiscountedListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get discounted listings request - Page: {}", page);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getDiscountedListings(pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Discounted listings retrieved successfully"));
    }

    /**
     * Get listings in price range
     */
    @GetMapping("/price-range")
    @Operation(summary = "Get listings by price", description = "Retrieve listings within price range")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getListingsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get listings by price range request - Range: {} to {}", minPrice, maxPrice);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getListingsByPriceRange(minPrice, maxPrice, pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Listings in price range retrieved successfully"));
    }

    /**
     * Get best-selling listings
     */
    @GetMapping("/best-selling")
    @Operation(summary = "Get best-selling items", description = "Retrieve top selling products")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getBestSellingListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get best-selling listings request");

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getBestSellingListings(pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Best-selling listings retrieved successfully"));
    }

    /**
     * Get popular listings
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular items", description = "Retrieve most viewed products")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> getPopularListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get popular listings request");

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.getPopularListings(pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Popular listings retrieved successfully"));
    }

    /**
     * Search listings by name
     */
    @GetMapping("/search")
    @Operation(summary = "Search listings", description = "Search products by name")
    public ResponseEntity<ApiResponse<Page<ProductListingDTO>>> searchListings(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Search listings request - Name: {}", name);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<ProductListingDTO> listings = listingService.searchListings(name, pageable);

        return ResponseEntity.ok(ApiResponse.success(listings, "Search results retrieved successfully"));
    }

    /**
     * Increment view count
     */
    @PostMapping("/{listingId}/view")
    @Operation(summary = "Record view", description = "Track product view analytics")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(
            @PathVariable UUID listingId) {
        log.debug("Increment view count request: {}", listingId);

        listingService.incrementViewCount(listingId);

        return ResponseEntity.ok(ApiResponse.success(null, "View recorded successfully"));
    }

    /**
     * Get vendor inventory summary
     */
    @GetMapping("/vendor/{vendorId}/inventory-summary")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get inventory summary", description = "Retrieve vendor inventory statistics")
    public ResponseEntity<ApiResponse<Object>> getVendorInventorySummary(
            @PathVariable UUID vendorId) {
        log.info("Get vendor inventory summary request: {}", vendorId);

        long totalListings = listingService.getTotalListingsForVendor(vendorId);
        long activeListings = listingService.getActiveListingsCountForVendor(vendorId);
        BigDecimal totalInventory = listingService.getTotalInventoryForVendor(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final long total = totalListings;
                public final long active = activeListings;
                public final BigDecimal totalQuantity = totalInventory;
            },
            "Inventory summary retrieved successfully"
        ));
    }

    /**
     * Get marketplace statistics
     */
    @GetMapping("/marketplace/stats")
    @Operation(summary = "Get marketplace stats", description = "Retrieve marketplace statistics")
    public ResponseEntity<ApiResponse<Object>> getMarketplaceStats() {
        log.info("Get marketplace statistics request");

        long totalListings = listingService.getTotalMarketplaceListings();
        long activeListings = listingService.getTotalActiveListings();

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final long totalListings = totalListings;
                public final long activeListings = activeListings;
            },
            "Marketplace statistics retrieved successfully"
        ));
    }
}
