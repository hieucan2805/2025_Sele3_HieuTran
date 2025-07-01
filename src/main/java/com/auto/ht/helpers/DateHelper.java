package com.auto.ht.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Helper class for date-related operations in test automation.
 */
public class DateHelper {

    /**
     * Parses a date string or keyword to a LocalDate object.
     * Supports keywords like "today", "tomorrow", "yesterday" and common date formats.
     *
     * @param dateString The date string to parse
     * @return The parsed LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }

        // Handle special date keywords
        switch (dateString.toLowerCase()) {
            case "today":
                return LocalDate.now();
            case "tomorrow":
                return LocalDate.now().plusDays(1);
            case "yesterday":
                return LocalDate.now().minusDays(1);
            default:
                // Try to parse the date string with multiple formats
                try {
                    // First try the standard format "d MMMM yyyy"
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
                    return LocalDate.parse(dateString, formatter);
                } catch (Exception e1) {
                    try {
                        // Then try "dd-MM-yyyy"
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        return LocalDate.parse(dateString, formatter);
                    } catch (Exception e2) {
                        try {
                            // Then try "yyyy-MM-dd"
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            return LocalDate.parse(dateString, formatter);
                        } catch (Exception e3) {
                            try {
                                // Then try "d/M/yyyy"
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                                return LocalDate.parse(dateString, formatter);
                            } catch (Exception e4) {
                                throw new IllegalArgumentException("Unable to parse date: " + dateString);
                            }
                        }
                    }
                }
        }
    }

    /**
     * Format a LocalDate to a string with the specified pattern.
     *
     * @param date The date to format
     * @param pattern The pattern to use for formatting
     * @return The formatted date string
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (pattern == null || pattern.isEmpty()) {
            pattern = "d MMMM yyyy"; // Default pattern
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return date.format(formatter);
    }

    /**
     * Get the month name from a LocalDate with the specified text style.
     *
     * @param date The date
     * @param style The text style (e.g., TextStyle.FULL, TextStyle.SHORT)
     * @return The month name
     */
    public static String getMonthName(LocalDate date, TextStyle style) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.getMonth().getDisplayName(style, Locale.ENGLISH);
    }

    /**
     * Get a date that is a certain number of days after another date.
     *
     * @param startDate The starting date string or keyword
     * @param days The number of days to add
     * @return The formatted date string for the future date
     */
    public static String addDaysToDate(String startDate, String days) {
        LocalDate date = parseDate(startDate);
        LocalDate futureDate = date.plusDays(Long.parseLong(days));
        return formatDate(futureDate, "d MMMM yyyy");
    }

    /**
     * Format a date for the calendar UI in the application.
     * Returns a 2-element array with [day, month name]
     *
     * @param dateString The date string or keyword
     * @return Array with day and month name
     */
    public static String[] formatDateForCalendar(String dateString) {
        LocalDate date = parseDate(dateString);
        String day = String.valueOf(date.getDayOfMonth());
        String month = getMonthName(date, TextStyle.FULL);

        return new String[] { day, month };
    }
}
