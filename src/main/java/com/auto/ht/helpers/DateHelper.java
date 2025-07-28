package com.auto.ht.helpers;

import com.auto.ht.utils.Constants;

import java.lang.module.Configuration;
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
        String year = String.valueOf(date.getYear());

        return new String[] { day, month + " " + year };
    }

    /**
     * Get a start date based on a range description like "next 7 days", "next 2 weeks", etc.
     *
     * @param range The range description (e.g., "next 7 days")
     * @return The calculated start date as a LocalDate
     */
    public static LocalDate getDateFromRange(String range) {
        if (range == null || range.trim().isEmpty()) {
            throw new IllegalArgumentException("Range string cannot be null or empty");
        }

        String[] rangeParts = range.split(" ");
        if (rangeParts.length < 3 || !rangeParts[0].equalsIgnoreCase("next")) {
            throw new IllegalArgumentException("Invalid range format. Expected 'next X unit' format");
        }

        String rangeUnit = rangeParts[rangeParts.length - 1].toLowerCase();
        int rangeValue = Integer.parseInt(rangeParts[1]);
        LocalDate today = LocalDate.now();

        return switch (rangeUnit) {
            case "day", "days" -> today.plusDays(1);
            case "week", "weeks" -> today.plusWeeks(1).with(java.time.DayOfWeek.MONDAY);
            case "month", "months" -> today.plusMonths(1).withDayOfMonth(1);
            default -> throw new IllegalArgumentException("Unsupported range unit: " + rangeUnit);
        };
    }

    /**
     * Extract the duration value from a range string (e.g., "next 7 days" returns 7)
     *
     * @param range The range description (e.g., "next 7 days")
     * @return The duration value as an integer
     */
    public static int getDurationFromRange(String range) {
        if (range == null || range.trim().isEmpty()) {
            throw new IllegalArgumentException("Range string cannot be null or empty");
        }

        String[] rangeParts = range.split(" ");
        if (rangeParts.length < 3 || !rangeParts[0].equalsIgnoreCase("next")) {
            throw new IllegalArgumentException("Invalid range format. Expected 'next X unit' format");
        }

        try {
            return Integer.parseInt(rangeParts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in range: " + range, e);
        }
    }

    /**
     * Get an end date based on a range description and its start date
     *
     * @param range The range description (e.g., "next 7 days")
     * @param startDate The start date of the range
     * @return The calculated end date as a LocalDate
     */
    public static LocalDate getEndDateFromRange(String range, LocalDate startDate) {
        if (range == null || range.trim().isEmpty()) {
            throw new IllegalArgumentException("Range string cannot be null or empty");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        String[] rangeParts = range.split(" ");
        if (rangeParts.length < 3) {
            throw new IllegalArgumentException("Invalid range format. Expected 'next X unit' format");
        }

        String rangeUnit = rangeParts[rangeParts.length - 1].toLowerCase();
        int rangeValue = Integer.parseInt(rangeParts[1]);

        switch (rangeUnit) {
            case "day":
            case "days":
                return startDate.plusDays(rangeValue);
            case "week":
            case "weeks":
                return startDate.plusWeeks(rangeValue);
            case "month":
            case "months":
                return startDate.plusMonths(rangeValue);
            default:
                throw new IllegalArgumentException("Unsupported range unit: " + rangeUnit);
        }
    }

    /**
     * Get both start and end dates from a range description.
     *
     * @param range The range description (e.g., "next 7 days")
     * @return Array containing [startDate, endDate] as LocalDate objects
     */
    public static LocalDate[] getDatesFromRange(String range) {
        LocalDate startDate = getDateFromRange(range);
        LocalDate endDate = getEndDateFromRange(range, startDate);
        return new LocalDate[] { startDate, endDate };
    }
}
