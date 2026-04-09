package com.logicveda.marketplace.vendor.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for currency and financial operations
 */
@Slf4j
public class CurrencyUtils {

    private static final int CURRENCY_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private CurrencyUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Add two amounts
     */
    public static BigDecimal add(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) {
            amount1 = BigDecimal.ZERO;
        }
        if (amount2 == null) {
            amount2 = BigDecimal.ZERO;
        }
        return amount1.add(amount2).setScale(CURRENCY_SCALE, ROUNDING_MODE);
    }

    /**
     * Subtract two amounts
     */
    public static BigDecimal subtract(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) {
            amount1 = BigDecimal.ZERO;
        }
        if (amount2 == null) {
            amount2 = BigDecimal.ZERO;
        }
        return amount1.subtract(amount2).setScale(CURRENCY_SCALE, ROUNDING_MODE);
    }

    /**
     * Multiply two amounts
     */
    public static BigDecimal multiply(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null || amount2 == null) {
            return BigDecimal.ZERO;
        }
        return amount1.multiply(amount2).setScale(CURRENCY_SCALE, ROUNDING_MODE);
    }

    /**
     * Divide two amounts
     */
    public static BigDecimal divide(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null || amount2 == null || amount2.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amount1.divide(amount2, CURRENCY_SCALE, ROUNDING_MODE);
    }

    /**
     * Calculate percentage
     */
    public static BigDecimal calculatePercentage(BigDecimal amount, BigDecimal percentage) {
        if (amount == null || percentage == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(percentage)
            .divide(new BigDecimal(100), CURRENCY_SCALE, ROUNDING_MODE);
    }

    /**
     * Apply discount
     */
    public static BigDecimal applyDiscount(BigDecimal amount, BigDecimal discountPercentage) {
        if (amount == null || discountPercentage == null) {
            return amount;
        }
        BigDecimal discount = calculatePercentage(amount, discountPercentage);
        return subtract(amount, discount);
    }

    /**
     * Apply commission
     */
    public static BigDecimal applyCommission(BigDecimal amount, BigDecimal commissionPercentage) {
        return calculatePercentage(amount, commissionPercentage);
    }

    /**
     * Calculate final amount after commission
     */
    public static BigDecimal calculateNetAmount(BigDecimal amount, BigDecimal commissionPercentage) {
        BigDecimal commission = applyCommission(amount, commissionPercentage);
        return subtract(amount, commission);
    }

    /**
     * Compare two amounts
     */
    public static int compare(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) {
            amount1 = BigDecimal.ZERO;
        }
        if (amount2 == null) {
            amount2 = BigDecimal.ZERO;
        }
        return amount1.compareTo(amount2);
    }

    /**
     * Check if amount is positive
     */
    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if amount is negative
     */
    public static boolean isNegative(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Check if amount is zero
     */
    public static boolean isZero(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Round to 2 decimal places
     */
    public static BigDecimal round(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(CURRENCY_SCALE, ROUNDING_MODE);
    }

    /**
     * Format currency for display
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%.2f", amount);
    }

    /**
     * Parse currency string to BigDecimal
     */
    public static BigDecimal parseCurrency(String currencyString) {
        try {
            String cleaned = currencyString.replaceAll("[^0-9.]", "");
            return new BigDecimal(cleaned).setScale(CURRENCY_SCALE, ROUNDING_MODE);
        } catch (NumberFormatException e) {
            log.error("Error parsing currency: {}", currencyString, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Convert cents to dollars
     */
    public static BigDecimal centsToDollars(long cents) {
        return new BigDecimal(cents).divide(new BigDecimal(100), CURRENCY_SCALE, ROUNDING_MODE);
    }

    /**
     * Convert dollars to cents
     */
    public static long dollarsToCents(BigDecimal dollars) {
        if (dollars == null) {
            return 0;
        }
        return dollars.multiply(new BigDecimal(100)).longValue();
    }
}
