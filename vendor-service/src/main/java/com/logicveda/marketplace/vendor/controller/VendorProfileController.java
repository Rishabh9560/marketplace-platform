package com.logicveda.marketplace.vendor.controller;

import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.dto.UpdateVendorProfileDTO;
import com.logicveda.marketplace.vendor.service.VendorProfileService;
import com.logicveda.marketplace.vendor.service.VendorStatisticsService;
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

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST Controller for vendor profile management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendors", description = "Vendor profile management endpoints")
public class VendorProfileController {

    private final VendorProfileService vendorService;
    private final VendorStatisticsService statisticsService;

    /**
     * Register new vendor
     */
    @PostMapping("/register")
    @Operation(summary = "Register new vendor", description = "Create a new vendor profile")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> registerVendor(
            @Valid @RequestBody VendorProfileDTO vendorDTO) {
        log.info("Register vendor request: {}", vendorDTO.getBusinessName());

        VendorProfileDTO registeredVendor = vendorService.registerVendor(vendorDTO);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(registeredVendor, "Vendor registered successfully"));
    }

    /**
     * Get vendor by ID
     */
    @GetMapping("/{vendorId}")
    @Operation(summary = "Get vendor details", description = "Retrieve vendor profile by ID")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> getVendor(
            @PathVariable 
            @Parameter(description = "Vendor ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID vendorId) {
        log.info("Get vendor request: {}", vendorId);

        VendorProfileDTO vendor = vendorService.getVendorById(vendorId);

        return ResponseEntity.ok(ApiResponse.success(vendor, "Vendor retrieved successfully"));
    }

    /**
     * Get vendor by user ID
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get vendor by user ID", description = "Retrieve vendor profile by user ID")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> getVendorByUser(
            @PathVariable UUID userId) {
        log.info("Get vendor by user request: {}", userId);

        VendorProfileDTO vendor = vendorService.getVendorByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success(vendor, "Vendor retrieved successfully"));
    }

    /**
     * Update vendor profile
     */
    @PutMapping("/{vendorId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Update vendor profile", description = "Update vendor profile information")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> updateVendor(
            @PathVariable UUID vendorId,
            @Valid @RequestBody UpdateVendorProfileDTO updateDTO) {
        log.info("Update vendor request: {}", vendorId);

        VendorProfileDTO updatedVendor = vendorService.updateVendor(vendorId, updateDTO);

        return ResponseEntity.ok(ApiResponse.success(updatedVendor, "Vendor updated successfully"));
    }

    /**
     * Get active vendors (paginated)
     */
    @GetMapping
    @Operation(summary = "Get active vendors", description = "Retrieve list of active vendors")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getActiveVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get active vendors request - Page: {}, Size: {}", page, size);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = vendorService.getActiveVendors(pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Active vendors retrieved successfully"));
    }

    /**
     * Get verified vendors
     */
    @GetMapping("/verified")
    @Operation(summary = "Get verified vendors", description = "Retrieve list of KYC verified vendors")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getVerifiedVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get verified vendors request - Page: {}, Size: {}", page, size);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = vendorService.getVerifiedVendors(pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Verified vendors retrieved successfully"));
    }

    /**
     * Get high-rated vendors
     */
    @GetMapping("/high-rated")
    @Operation(summary = "Get high-rated vendors", description = "Retrieve vendors with high ratings")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getHighRatedVendors(
            @RequestParam(defaultValue = "4.0") BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get high-rated vendors request - Min Rating: {}, Page: {}", minRating, page);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = vendorService.getHighRatedVendors(minRating, pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "High-rated vendors retrieved successfully"));
    }

    /**
     * Search vendors by name
     */
    @GetMapping("/search")
    @Operation(summary = "Search vendors", description = "Search vendors by business name")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> searchVendors(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Search vendors request - Name: {}", name);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = vendorService.searchVendorsByName(name, pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Vendors search results"));
    }

    /**
     * Get vendors by city
     */
    @GetMapping("/city/{city}")
    @Operation(summary = "Get vendors by city", description = "Retrieve vendors from specific city")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getVendorsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get vendors by city request - City: {}", city);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = vendorService.getVendorsByCity(city, pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Vendors in city retrieved successfully"));
    }

    /**
     * Get top earning vendors
     */
    @GetMapping("/top-earning")
    @Operation(summary = "Get top earning vendors", description = "Retrieve vendors by earnings")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getTopEarningVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get top earning vendors request");

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = vendorService.getTopEarningVendors(pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Top earning vendors retrieved successfully"));
    }

    /**
     * Suspend vendor
     */
    @PostMapping("/{vendorId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspend vendor", description = "Suspend vendor account")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> suspendVendor(
            @PathVariable UUID vendorId,
            @RequestParam String reason) {
        log.warn("Suspend vendor request: {} - Reason: {}", vendorId, reason);

        VendorProfileDTO vendor = vendorService.suspendVendor(vendorId, reason);

        return ResponseEntity.ok(ApiResponse.success(vendor, "Vendor suspended successfully"));
    }

    /**
     * Unsuspend vendor
     */
    @PostMapping("/{vendorId}/unsuspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unsuspend vendor", description = "Restore suspended vendor account")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> unsuspendVendor(
            @PathVariable UUID vendorId) {
        log.info("Unsuspend vendor request: {}", vendorId);

        VendorProfileDTO vendor = vendorService.unsuspendVendor(vendorId);

        return ResponseEntity.ok(ApiResponse.success(vendor, "Vendor unsuspended successfully"));
    }

    /**
     * Get vendor statistics
     */
    @GetMapping("/{vendorId}/statistics")
    @Operation(summary = "Get vendor statistics", description = "Retrieve vendor performance statistics")
    public ResponseEntity<ApiResponse<com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO>> getVendorStatistics(
            @PathVariable UUID vendorId) {
        log.info("Get vendor statistics request: {}", vendorId);

        com.logicveda.marketplace.vendor.dto.VendorStatisticsDTO stats = statisticsService.getVendorStatistics(vendorId);

        return ResponseEntity.ok(ApiResponse.success(stats, "Vendor statistics retrieved successfully"));
    }

    /**
     * Check vendor health
     */
    @GetMapping("/{vendorId}/health")
    @Operation(summary = "Check vendor health", description = "Validate vendor compliance and health")
    public ResponseEntity<ApiResponse<Object>> checkVendorHealth(
            @PathVariable UUID vendorId) {
        log.info("Check vendor health request: {}", vendorId);

        boolean canSell = vendorService.canVendorSellProducts(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final boolean canSellProducts = canSell;
            },
            "Vendor health checked successfully"
        ));
    }

    /**
     * Get total vendor count
     */
    @GetMapping("/stats/total")
    @Operation(summary = "Get total vendor count", description = "Retrieve total number of active vendors")
    public ResponseEntity<ApiResponse<Object>> getTotalVendorCount() {
        log.info("Get total vendor count request");

        long count = vendorService.getTotalVendorCount();

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final long totalVendors = count;
            },
            "Total vendor count retrieved successfully"
        ));
    }
}
