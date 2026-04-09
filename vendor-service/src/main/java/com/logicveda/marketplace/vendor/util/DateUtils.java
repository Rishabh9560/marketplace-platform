package com.logicveda.marketplace.vendor.util;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date/time operations
 */
public class DateUtils {

    private static final DateTimeFormatter PAYOUT_PERIOD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get current payout period (YYYYMM format)
     */
    public static String getCurrentPayoutPeriod() {
        return YearMonth.now().format(PAYOUT_PERIOD_FORMATTER);
    }

    /**
     * Get payout period from LocalDateTime
     */
    public static String getPayoutPeriod(LocalDateTime dateTime) {
        if (dateTime == null) {
            return getCurrentPayoutPeriod();
        }
        return YearMonth.from(dateTime).format(PAYOUT_PERIOD_FORMATTER);
    }

    /**
     * Get payout period for previous month
     */
    public static String getPreviousMonthPayoutPeriod() {
        return YearMonth.now().minusMonths(1).format(PAYOUT_PERIOD_FORMATTER);
    }

    /**
     * Get payout period for next month
     */
    public static String getNextMonthPayoutPeriod() {
        return YearMonth.now().plusMonths(1).format(PAYOUT_PERIOD_FORMATTER);
    }

    /**
     * Convert payout period string to YearMonth
     */
    public static YearMonth parsePayoutPeriod(String period) {
        return YearMonth.parse(period, PAYOUT_PERIOD_FORMATTER);
    }

    /**
     * Check if given period is in the past
     */
    public static boolean isPastPeriod(String period) {
        YearMonth periodMonth = parsePayoutPeriod(period);
        return periodMonth.isBefore(YearMonth.now());
    }

    /**
     * Check if given period is current month
     */
    public static boolean isCurrentPeriod(String period) {
        return period.equals(getCurrentPayoutPeriod());
    }

    /**
     * Format LocalDateTime for display
     */
    public static String formatDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Get month name from payout period
     */
    public static String getMonthName(String period) {
        YearMonth yearMonth = parsePayoutPeriod(period);
        return yearMonth.getMonth().toString() + " " + yearMonth.getYear();
    }

    /**
     * Calculate days difference between two dates
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Check if date is overdue
     */
    public static boolean isOverdue(LocalDateTime dueDate) {
        if (dueDate == null) {
            return false;
        }
        return dueDate.isBefore(LocalDateTime.now());
    }
}
