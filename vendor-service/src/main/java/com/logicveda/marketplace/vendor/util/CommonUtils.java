package com.logicveda.marketplace.vendor.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class for common operations
 */
@Slf4j
@Component
public class CommonUtils {

    private CommonUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generate UUID
     */
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }

    /**
     * Generate UUID string
     */
    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }

    /**
     * Convert string to UUID
     */
    public static UUID parseUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", uuid);
            return null;
        }
    }

    /**
     * Check if string is a valid UUID
     */
    public static boolean isValidUUID(String uuid) {
        return parseUUID(uuid) != null;
    }

    /**
     * Sanitize string input
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("[^a-zA-Z0-9\\s-_@.]", "");
    }

    /**
     * Mask sensitive information (email)
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "*".repeat(Math.min(username.length(), 1)) + "@" + domain;
        }

        return username.charAt(0) + "*".repeat(username.length() - 2) + username.charAt(username.length() - 1) + "@" + domain;
    }

    /**
     * Mask account number
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        int visibleLength = Math.min(4, accountNumber.length());
        return "*".repeat(accountNumber.length() - visibleLength) + 
               accountNumber.substring(accountNumber.length() - visibleLength);
    }

    /**
     * Format currency
     */
    public static String formatCurrency(java.math.BigDecimal amount, String currency) {
        if (amount == null) {
            return null;
        }
        return String.format("%s %.2f", currency != null ? currency : "$", amount);
    }

    /**
     * Get environment variable with default
     */
    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Retry logic helper
     */
    public static <T> T retryOperation(RetryableOperation<T> operation, int maxRetries) throws Exception {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                return operation.execute();
            } catch (Exception e) {
                attempts++;
                if (attempts >= maxRetries) {
                    throw e;
                }
                log.warn("Retry attempt {} of {}: {}", attempts, maxRetries, e.getMessage());
                Thread.sleep(1000 * attempts); // Exponential backoff
            }
        }
        return null;
    }

    /**
     * Interface for retryable operations
     */
    @FunctionalInterface
    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}
