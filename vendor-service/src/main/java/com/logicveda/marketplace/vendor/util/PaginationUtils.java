package com.logicveda.marketplace.vendor.util;

import com.logicveda.marketplace.vendor.constants.VendorConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for pagination operations
 */
public class PaginationUtils {

    private PaginationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Create pageable with default size
     */
    public static Pageable createPageable(int page) {
        return createPageable(page, VendorConstants.DEFAULT_PAGE_SIZE, null);
    }

    /**
     * Create pageable with custom size
     */
    public static Pageable createPageable(int page, int size) {
        return createPageable(page, size, null);
    }

    /**
     * Create pageable with sorting
     */
    public static Pageable createPageable(int page, int size, Sort sort) {
        // Validate page number
        if (page < 0) {
            page = VendorConstants.DEFAULT_PAGE;
        }

        // Validate and adjust page size
        if (size <= 0) {
            size = VendorConstants.DEFAULT_PAGE_SIZE;
        }
        if (size > VendorConstants.MAX_PAGE_SIZE) {
            size = VendorConstants.MAX_PAGE_SIZE;
        }

        if (sort != null) {
            return PageRequest.of(page, size, sort);
        }
        return PageRequest.of(page, size);
    }

    /**
     * Create pageable with sorting by field
     */
    public static Pageable createPageable(int page, int size, String sortBy, Sort.Direction direction) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return createPageable(page, size);
        }

        Sort sort = Sort.by(direction != null ? direction : Sort.Direction.DESC, sortBy);
        return createPageable(page, size, sort);
    }

    /**
     * Create pageable for ascending sort
     */
    public static Pageable createPageableAsc(int page, int size, String sortBy) {
        return createPageable(page, size, sortBy, Sort.Direction.ASC);
    }

    /**
     * Create pageable for descending sort
     */
    public static Pageable createPageableDesc(int page, int size, String sortBy) {
        return createPageable(page, size, sortBy, Sort.Direction.DESC);
    }

    /**
     * Validate pagination parameters
     */
    public static void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        if (size > VendorConstants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                "Page size cannot exceed " + VendorConstants.MAX_PAGE_SIZE
            );
        }
    }

    /**
     * Get offset from page and size
     */
    public static int getOffset(int page, int size) {
        return page * size;
    }

    /**
     * Get total pages
     */
    public static int getTotalPages(int totalElements, int pageSize) {
        return (totalElements + pageSize - 1) / pageSize;
    }
}
