package com.logicveda.marketplace.common.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class for string operations.
 */
public class StringUtils {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern NON_SLUG_PATTERN = Pattern.compile("[^a-z0-9\\-]");

    /**
     * Generate URL-friendly slug from text.
     * Example: "Apple iPhone 15 Pro" -> "apple-iphone-15-pro"
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        // Convert to lowercase
        String slug = input.toLowerCase().trim();

        // Normalize unicode characters (remove accents)
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("[\\p{Mn}]", "");

        // Replace whitespace with hyphens
        slug = WHITESPACE_PATTERN.matcher(slug).replaceAll("-");

        // Remove non-alphanumeric characters except hyphens
        slug = NON_SLUG_PATTERN.matcher(slug).replaceAll("");

        // Remove consecutive hyphens
        slug = slug.replaceAll("-+", "-");

        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }

    /**
     * Mask email for privacy (e.g., user***@example.com)
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            localPart = "*".repeat(Math.max(1, localPart.length()));
        } else {
            localPart = localPart.charAt(0) + "*".repeat(localPart.length() - 2) + localPart.charAt(localPart.length() - 1);
        }

        return localPart + "@" + domain;
    }
}
