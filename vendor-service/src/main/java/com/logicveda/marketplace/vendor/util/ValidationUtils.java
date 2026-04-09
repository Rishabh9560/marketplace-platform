package com.logicveda.marketplace.vendor.util;

import com.logicveda.marketplace.vendor.constants.VendorConstants;
import com.logicveda.marketplace.vendor.exception.VendorException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern CURRENCY_PATTERN = Pattern.compile(
        "^\\d+(\\.\\d{2})?$"
    );

    private static final Pattern SKU_PATTERN = Pattern.compile(
        "^[A-Z0-9-]{3,100}$"
    );

    private static final Pattern PAYOUT_PERIOD_PATTERN = Pattern.compile(
        "^\\d{6}$"
    );

    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate business name
     */
    public static void validateBusinessName(String businessName) {
        if (businessName == null || businessName.trim().isEmpty()) {
            throw new IllegalArgumentException("Business name cannot be empty");
        }
        if (businessName.length() < VendorConstants.MIN_BUSINESS_NAME_LENGTH) {
            throw new IllegalArgumentException(
                "Business name must be at least " + VendorConstants.MIN_BUSINESS_NAME_LENGTH + " characters"
            );
        }
        if (businessName.length() > VendorConstants.MAX_BUSINESS_NAME_LENGTH) {
            throw new IllegalArgumentException(
                "Business name cannot exceed " + VendorConstants.MAX_BUSINESS_NAME_LENGTH + " characters"
            );
        }
    }

    /**
     * Validate product name
     */
    public static void validateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (productName.length() < VendorConstants.MIN_PRODUCT_NAME_LENGTH) {
            throw new IllegalArgumentException(
                "Product name must be at least " + VendorConstants.MIN_PRODUCT_NAME_LENGTH + " characters"
            );
        }
        if (productName.length() > VendorConstants.MAX_PRODUCT_NAME_LENGTH) {
            throw new IllegalArgumentException(
                "Product name cannot exceed " + VendorConstants.MAX_PRODUCT_NAME_LENGTH + " characters"
            );
        }
    }

    /**
     * Validate SKU format
     */
    public static void validateSKU(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
        if (!SKU_PATTERN.matcher(sku).matches()) {
            throw new IllegalArgumentException("SKU must be 3-100 characters (alphanumeric and hyphens)");
        }
    }

    /**
     * Validate currency amount
     */
    public static void validateCurrencyAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid amount: must be a positive number");
        }
    }

    /**
     * Validate commission rate
     */
    public static void validateCommissionRate(BigDecimal rate) {
        if (rate == null) {
            throw new IllegalArgumentException("Commission rate cannot be null");
        }
        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(VendorConstants.MAX_COMMISSION_RATE) > 0) {
            throw new IllegalArgumentException(
                "Commission rate must be between 0 and " + VendorConstants.MAX_COMMISSION_RATE
            );
        }
    }

    /**
     * Validate discount percentage
     */
    public static void validateDiscountPercentage(BigDecimal discount) {
        if (discount == null) {
            return; // Discount is optional
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(VendorConstants.MAX_DISCOUNT_PERCENTAGE) > 0) {
            throw new IllegalArgumentException(
                "Discount percentage must be between 0 and " + VendorConstants.MAX_DISCOUNT_PERCENTAGE
            );
        }
    }

    /**
     * Validate inventory quantity
     */
    public static void validateInventoryQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Invalid quantity: must be a positive integer");
        }
    }

    /**
     * Validate shipping days
     */
    public static void validateShippingDays(Integer days) {
        if (days == null || days < VendorConstants.MIN_SHIPPING_DAYS || 
            days > VendorConstants.MAX_SHIPPING_DAYS) {
            throw new IllegalArgumentException(
                "Shipping days must be between " + VendorConstants.MIN_SHIPPING_DAYS + 
                " and " + VendorConstants.MAX_SHIPPING_DAYS
            );
        }
    }

    /**
     * Validate payout period format (YYYYMM)
     */
    public static void validatePayoutPeriod(String period) {
        if (period == null || !PAYOUT_PERIOD_PATTERN.matcher(period).matches()) {
            throw new IllegalArgumentException("Invalid payout period: must be in YYYYMM format");
        }
    }

    /**
     * Validate price comparison (vendorPrice should be <= marketplaceList)
     */
    public static void validatePriceComparison(BigDecimal vendorPrice, BigDecimal marketplaceList) {
        if (vendorPrice == null || marketplaceList == null) {
            return;
        }
        if (vendorPrice.compareTo(marketplaceList) > 0) {
            throw new IllegalArgumentException(
                "Vendor price cannot be greater than marketplace list price"
            );
        }
    }

    /**
     * Check if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validate UUID format
     */
    public static boolean isValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validate positive number
     */
    public static boolean isPositive(BigDecimal number) {
        return number != null && number.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validate non-negative number
     */
    public static boolean isNonNegative(BigDecimal number) {
        return number != null && number.compareTo(BigDecimal.ZERO) >= 0;
    }
}
