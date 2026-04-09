package com.logicveda.marketplace.vendor.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for vendor-related errors
 */
@Getter
public class VendorException extends RuntimeException {

    private final HttpStatus statusCode;
    private final String errorCode;

    public VendorException(String message, String errorCode, HttpStatus statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public VendorException(String message, String errorCode, HttpStatus statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public static VendorException vendorNotFound(String vendorId) {
        return new VendorException(
            "Vendor not found with ID: " + vendorId,
            "VENDOR_NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }

    public static VendorException userNotVendor(String userId) {
        return new VendorException(
            "User is not registered as vendor: " + userId,
            "USER_NOT_VENDOR",
            HttpStatus.FORBIDDEN
        );
    }

    public static VendorException vendorSuspended(String vendorId) {
        return new VendorException(
            "Vendor account is suspended: " + vendorId,
            "VENDOR_SUSPENDED",
            HttpStatus.FORBIDDEN
        );
    }

    public static VendorException kycNotVerified(String vendorId) {
        return new VendorException(
            "KYC verification required for vendor: " + vendorId,
            "KYC_NOT_VERIFIED",
            HttpStatus.FORBIDDEN
        );
    }

    public static VendorException kycAlreadySubmitted(String vendorId) {
        return new VendorException(
            "KYC already submitted for vendor: " + vendorId,
            "KYC_ALREADY_SUBMITTED",
            HttpStatus.CONFLICT
        );
    }

    public static VendorException kycInvalidStatus(String vendorId, String status) {
        return new VendorException(
            String.format("Invalid KYC status transition for vendor %s: %s", vendorId, status),
            "KYC_INVALID_STATUS",
            HttpStatus.BAD_REQUEST
        );
    }

    public static VendorException productListingNotFound(String listingId) {
        return new VendorException(
            "Product listing not found: " + listingId,
            "LISTING_NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }

    public static VendorException payoutRecordNotFound(String payoutId) {
        return new VendorException(
            "Payout record not found: " + payoutId,
            "PAYOUT_NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }

    public static VendorException insufficientBalance(String vendorId) {
        return new VendorException(
            "Insufficient balance for payout: " + vendorId,
            "INSUFFICIENT_BALANCE",
            HttpStatus.BAD_REQUEST
        );
    }

    public static VendorException invalidInventoryAmount(int quantity) {
        return new VendorException(
            "Invalid inventory quantity: " + quantity,
            "INVALID_INVENTORY",
            HttpStatus.BAD_REQUEST
        );
    }

    public static VendorException invalidPriceUpdate(String reason) {
        return new VendorException(
            "Invalid price update: " + reason,
            "INVALID_PRICE",
            HttpStatus.BAD_REQUEST
        );
    }

    public static VendorException payoutAlreadyProcessed(String payoutId) {
        return new VendorException(
            "Payout already processed: " + payoutId,
            "PAYOUT_ALREADY_PROCESSED",
            HttpStatus.CONFLICT
        );
    }

    public static VendorException duplicateVendorEmail(String email) {
        return new VendorException(
            "Email already registered: " + email,
            "DUPLICATE_EMAIL",
            HttpStatus.CONFLICT
        );
    }

    public static VendorException duplicateTaxId(String taxId) {
        return new VendorException(
            "Tax ID already registered: " + taxId,
            "DUPLICATE_TAX_ID",
            HttpStatus.CONFLICT
        );
    }
}
