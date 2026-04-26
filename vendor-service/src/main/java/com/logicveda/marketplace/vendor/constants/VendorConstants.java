package com.logicveda.marketplace.vendor.constants;

/**
 * Constants for vendor service
 */
public class VendorConstants {

    // KYC Constants
    public static final String KYC_PENDING = "PENDING";
    public static final String KYC_SUBMITTED = "SUBMITTED";
    public static final String KYC_VERIFIED = "VERIFIED";
    public static final String KYC_REJECTED = "REJECTED";
    public static final String KYC_SUSPENDED = "SUSPENDED";

    // Listing Status Constants
    public static final String LISTING_DRAFT = "DRAFT";
    public static final String LISTING_ACTIVE = "ACTIVE";
    public static final String LISTING_INACTIVE = "INACTIVE";
    public static final String LISTING_DELISTED = "DELISTED";
    public static final String LISTING_SUSPENDED = "SUSPENDED";

    // Payout Status Constants
    public static final String PAYOUT_PENDING = "PENDING";
    public static final String PAYOUT_SCHEDULED = "SCHEDULED";
    public static final String PAYOUT_PROCESSING = "PROCESSING";
    public static final String PAYOUT_COMPLETED = "COMPLETED";
    public static final String PAYOUT_FAILED = "FAILED";
    public static final String PAYOUT_CANCELLED = "CANCELLED";
    public static final String PAYOUT_ON_HOLD = "ON_HOLD";

    // Default Values
    public static final java.math.BigDecimal DEFAULT_COMMISSION_RATE = new java.math.BigDecimal("10.00");
    public static final int DEFAULT_SHIPPING_DAYS = 3;
    public static final int DEFAULT_REORDER_LEVEL = 10;
    public static final int DEFAULT_REORDER_QUANTITY = 50;

    // Validation Constants
    public static final int MIN_BUSINESS_NAME_LENGTH = 3;
    public static final int MAX_BUSINESS_NAME_LENGTH = 255;
    public static final int MIN_PRODUCT_NAME_LENGTH = 3;
    public static final int MAX_PRODUCT_NAME_LENGTH = 255;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MIN_SHIPPING_DAYS = 1;
    public static final int MAX_SHIPPING_DAYS = 30;
    public static final java.math.BigDecimal MAX_DISCOUNT_PERCENTAGE = new java.math.BigDecimal("100.00");
    public static final java.math.BigDecimal MAX_COMMISSION_RATE = new java.math.BigDecimal("100.00");

    // Payout Constants
    public static final String PAYOUT_PERIOD_FORMAT = "yyyyMM";
    public static final int MAX_PAYOUT_RETRY_COUNT = 3;
    public static final long MINIMUM_PAYOUT_AMOUNT = 1000; // in cents: $10.00

    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE = 0;

    // Cache Keys
    public static final String CACHE_VENDOR_PREFIX = "vendor:";
    public static final String CACHE_LISTING_PREFIX = "listing:";
    public static final String CACHE_PAYOUT_PREFIX = "payout:";
    public static final int CACHE_TTL_MINUTES = 30;

    // API Paths
    public static final String API_V1_VENDOR = "/api/v1/vendors";
    public static final String API_V1_LISTINGS = "/api/v1/listings";
    public static final String API_V1_PAYOUTS = "/api/v1/payouts";

    // Response Messages
    public static final String VENDOR_CREATED = "Vendor profile created successfully";
    public static final String VENDOR_UPDATED = "Vendor profile updated successfully";
    public static final String VENDOR_DELETED = "Vendor profile deleted successfully";
    public static final String KYC_SUBMISSION_MESSAGE = "KYC submitted successfully";
    public static final String LISTING_CREATED = "Product listing created successfully";
    public static final String LISTING_UPDATED = "Product listing updated successfully";
    public static final String INVENTORY_UPDATED = "Inventory updated successfully";
    public static final String PRICE_UPDATED = "Price updated successfully";
    public static final String PAYOUT_INITIATED = "Payout initiated successfully";

    // Premium Vendor Constants
    public static final java.math.BigDecimal PREMIUM_VENDOR_MIN_RATING = new java.math.BigDecimal("4.50");
    public static final int PREMIUM_VENDOR_MIN_REVIEWS = 100;
    public static final int PREMIUM_VENDOR_MIN_SALES = 500;
    public static final java.math.BigDecimal PREMIUM_COMMISSION_RATE_DISCOUNT = new java.math.BigDecimal("2.00");

    // Rate Limiting
    public static final int RATE_LIMIT_PER_MINUTE = 60;
    public static final int RATE_LIMIT_LARGE_BATCH = 1000;

    private VendorConstants() {
        // Private constructor to prevent instantiation
    }
}
