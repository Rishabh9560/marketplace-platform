package com.logicveda.marketplace.vendor.controller;

import com.logicveda.marketplace.vendor.dto.KYCSubmissionRequestDTO;
import com.logicveda.marketplace.vendor.dto.VendorProfileDTO;
import com.logicveda.marketplace.vendor.service.KYCService;
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
import java.util.UUID;
import java.util.Map;

/**
 * REST Controller for KYC (Know Your Customer) verification
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC Verification", description = "Know Your Customer verification endpoints")
public class KYCController {

    private final KYCService kycService;

    /**
     * Submit KYC documents
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Submit KYC", description = "Submit KYC documents for verification")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> submitKYC(
            @Valid @RequestBody KYCSubmissionRequestDTO kycRequest) {
        // Extract vendor ID from authentication principal (should be vendor ID or user ID)
        Object principal = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        String vendorIdStr = null;
        
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            vendorIdStr = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            vendorIdStr = (String) principal;
        }
        
        if (vendorIdStr == null) {
            throw new IllegalArgumentException("Unable to extract vendor ID from authentication context");
        }
        
        UUID vendorId;
        try {
            vendorId = UUID.fromString(vendorIdStr);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse vendor ID as UUID: {}", vendorIdStr);
            throw new IllegalArgumentException("Invalid vendor ID format: " + vendorIdStr);
        }
        
        log.info("Submit KYC request - Vendor: {}", vendorId);

        VendorProfileDTO vendor = kycService.submitKYC(vendorId, kycRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(vendor, "KYC submitted successfully. Awaiting admin verification."));
    }

    /**
     * Verify KYC documents
     */
    @PostMapping("/{vendorId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify KYC", description = "Approve vendor KYC documents")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> verifyKYC(
            @PathVariable 
            @Parameter(description = "Vendor ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID vendorId) {
        log.info("Verify KYC request - Vendor: {}", vendorId);

        VendorProfileDTO vendor = kycService.verifyKYC(vendorId);

        return ResponseEntity.ok(ApiResponse.success(vendor, "KYC verified successfully"));
    }

    /**
     * Reject KYC documents
     */
    @PostMapping("/{vendorId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject KYC", description = "Reject vendor KYC documents with reason")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> rejectKYC(
            @PathVariable UUID vendorId,
            @RequestParam String reason) {
        log.info("Reject KYC request - Vendor: {}, Reason: {}", vendorId, reason);

        VendorProfileDTO vendor = kycService.rejectKYC(vendorId, reason);

        return ResponseEntity.ok(ApiResponse.success(vendor, "KYC rejected. Vendor can resubmit."));
    }

    /**
     * Request KYC resubmission
     */
    @PostMapping("/{vendorId}/resubmit-request")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Request resubmission", description = "Request vendor to resubmit KYC")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> requestKYCResubmission(
            @PathVariable UUID vendorId,
            @RequestParam String reason) {
        log.info("Request KYC resubmission - Vendor: {}", vendorId);

        VendorProfileDTO vendor = kycService.requestKYCResubmission(vendorId, reason);

        return ResponseEntity.ok(ApiResponse.success(vendor, "Resubmission requested successfully"));
    }

    /**
     * Get KYC status
     */
    @GetMapping("/{vendorId}/status")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Get KYC status", description = "Retrieve KYC verification status for vendor")
    public ResponseEntity<ApiResponse<Object>> getKYCStatus(
            @PathVariable UUID vendorId) {
        log.info("Get KYC status request - Vendor: {}", vendorId);

        boolean isVerified = kycService.isKYCVerified(vendorId);
        String status = kycService.getKYCStatus(vendorId);

        Map<String, Object> statusResponse = new java.util.HashMap<>();
        statusResponse.put("verified", isVerified);
        statusResponse.put("status", status);
        statusResponse.put("rejectionReason", null);

        return ResponseEntity.ok(ApiResponse.success(
            statusResponse,
            "KYC status retrieved successfully"
        ));
    }

    /**
     * Check KYC requirements
     */
    @GetMapping("/{vendorId}/requirements-check")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    @Operation(summary = "Check KYC requirements", description = "Validate if vendor meets KYC requirements")
    public ResponseEntity<ApiResponse<Object>> checkKYCRequirements(
            @PathVariable UUID vendorId) {
        log.info("Check KYC requirements - Vendor: {}", vendorId);

        final boolean requirementsMet = kycService.areKYCRequirementsMet(vendorId);

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final boolean metRequirements = requirementsMet;
            },
            "KYC requirements checked successfully"
        ));
    }

    /**
     * Get pending KYC submissions
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending KYC", description = "Retrieve all pending KYC submissions")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getPendingKYCSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get pending KYC submissions request - Page: {}", page);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = kycService.getPendingKYCSubmissions(pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Pending KYC submissions retrieved successfully"));
    }

    /**
     * Get rejected KYC submissions
     */
    @GetMapping("/rejected")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get rejected KYC", description = "Retrieve all rejected KYC submissions")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getRejectedKYCSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get rejected KYC submissions request - Page: {}", page);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = kycService.getRejectedKYCSubmissions(pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Rejected KYC submissions retrieved successfully"));
    }

    /**
     * Get verified vendors
     */
    @GetMapping("/verified")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get verified vendors", description = "Retrieve all verified vendors")
    public ResponseEntity<ApiResponse<Page<VendorProfileDTO>>> getVerifiedVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Get verified vendors request - Page: {}", page);

        Pageable pageable = PaginationUtils.createPageable(page, size);
        Page<VendorProfileDTO> vendors = kycService.getVerifiedVendors(pageable);

        return ResponseEntity.ok(ApiResponse.success(vendors, "Verified vendors retrieved successfully"));
    }

    /**
     * Get KYC statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get KYC statistics", description = "Retrieve KYC verification statistics")
    public ResponseEntity<ApiResponse<Object>> getKYCStatistics() {
        log.info("Get KYC statistics request");

        long totalVerified = kycService.getVerifiedVendorCount();
        long totalPending = kycService.getPendingKYCCount();
        long totalRejected = kycService.getRejectedKYCCount();

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final long verifiedVendors = totalVerified;
                public final long pendingSubmissions = totalPending;
                public final long rejectedSubmissions = totalRejected;
            },
            "KYC statistics retrieved successfully"
        ));
    }

    /**
     * Renew KYC verification
     */
    @PostMapping("/{vendorId}/renew")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Renew KYC", description = "Renew KYC verification for vendor")
    public ResponseEntity<ApiResponse<VendorProfileDTO>> renewKYCVerification(
            @PathVariable UUID vendorId) {
        log.info("Renew KYC verification request - Vendor: {}", vendorId);

        VendorProfileDTO vendor = kycService.renewKYCVerification(vendorId);

        return ResponseEntity.ok(ApiResponse.success(vendor, "KYC verification renewed successfully"));
    }

    /**
     * Bulk verify KYC submissions
     */
    @PostMapping("/bulk/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bulk verify KYC", description = "Verify multiple KYC submissions at once")
    public ResponseEntity<ApiResponse<Object>> bulkVerifyKYC(
            @RequestBody java.util.List<UUID> vendorIds) {
        log.info("Bulk verify KYC request - Count: {}", vendorIds.size());

        int verifiedCount = vendorIds.stream()
            .mapToInt(vendorId -> {
                try {
                    kycService.verifyKYC(vendorId);
                    return 1;
                } catch (Exception e) {
                    log.warn("Failed to verify KYC for vendor: {}", vendorId, e);
                    return 0;
                }
            })
            .sum();

        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final int totalRequested = vendorIds.size();
                public final int successfullyVerified = verifiedCount;
            },
            "Bulk KYC verification completed"
        ));
    }

    /**
     * Export KYC report
     */
    @GetMapping("/report/export")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export KYC report", description = "Export KYC verification statistics report")
    public ResponseEntity<ApiResponse<Object>> exportKYCReport(
            @RequestParam(defaultValue = "pdf") String format) {
        log.info("Export KYC report request - Format: {}", format);

        final String exportFormat = format;
        return ResponseEntity.ok(ApiResponse.success(
            new Object() {
                public final String format = exportFormat;
                public final String status = "READY_FOR_DOWNLOAD";
            },
            "KYC report ready for download"
        ));
    }

    /**
     * Upload KYC document
     */
    @PostMapping("/documents/upload")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Upload KYC document", description = "Upload a single KYC document")
    public ResponseEntity<ApiResponse<Object>> uploadKYCDocument(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam("documentType") String documentType) {
        log.info("Upload KYC document request - Type: {}, Size: {} bytes", documentType, file.getSize());

        String uploadedUrl = kycService.uploadDocument(file, documentType);

        Map<String, Object> uploadResponse = new java.util.HashMap<>();
        uploadResponse.put("url", uploadedUrl);
        uploadResponse.put("documentType", documentType);
        uploadResponse.put("fileSize", file.getSize());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
            uploadResponse,
            "Document uploaded successfully"
        ));
    }
}
