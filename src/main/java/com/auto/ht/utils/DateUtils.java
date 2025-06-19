package com.auto.ht.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DateUtils {

    private static final List<DateTimeFormatter> DATE_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),      // 13-02-2025
            DateTimeFormatter.ofPattern("d-M-yyyy"),        // 3-2-2025
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),      // 13/02/2025
            DateTimeFormatter.ofPattern("d/M/yyyy"),        // 3/2/2025
            DateTimeFormatter.ofPattern("yyyy-M-dd"),        // 2025-06-22
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH), // 13 February 2025
            DateTimeFormatter.ofPattern("dd, MMMM yyyy", Locale.ENGLISH) // 13, February 2025
    );

    /**
     * Generates a random LocalDate between 2010-01-01 and 2030-01-01.
     * <p>
     * Example:
     * <pre>
     *   LocalDate randomDate = DateUtils.getRandomDate();
     *   System.out.println(randomDate); // e.g., 2022-05-17
     * </pre>
     *
     * @return a random LocalDate within the specified range
     */
    public static LocalDate getRandomDate() {
        Random random = new Random();
        int minDay = (int) LocalDate.of(2010, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2030, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt(maxDay - minDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    /**
     * Attempts to parse a date string using multiple predefined formats.
     * <p>
     * Supported formats:
     * <ul>
     *   <li>"dd-MM-yyyy" (e.g., 13-02-2025)</li>
     *   <li>"d-M-yyyy" (e.g., 3-2-2025)</li>
     *   <li>"dd/MM/yyyy" (e.g., 13/02/2025)</li>
     *   <li>"d/M/yyyy" (e.g., 3/2/2025)</li>
     *   <li>"yyyy-M-dd" (e.g., 2025-06-22)</li>
     *   <li>"d MMMM yyyy" (e.g., 13 February 2025)</li>
     *   <li>"dd, MMMM yyyy" (e.g., 13, February 2025)</li>
     * </ul>
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate
     * @throws IllegalArgumentException if the date string doesn't match any supported format
     */
    private static LocalDate parseWithMultipleFormats(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATS)
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }

    /**
     * Parses a date string or keyword and returns a formatted date string.
     * <p>
     * Usage:
     * <ul>
     *   <li>Keywords: "today", "tomorrow", "yesterday" (case-insensitive)</li>
     *   <li>Date strings in supported formats (see parseWithMultipleFormats)</li>
     * </ul>
     * <p>
     * Example:
     * <pre>
     *   String today = DateUtils.parseSelectedDate("today");
     *   String specificDate = DateUtils.parseSelectedDate("13-02-2025");
     * </pre>
     *
     * @param date the date string or keyword to parse
     * @return the formatted date string according to Constants.TIME_FORMAT_CURRENT_DATE
     * @throws IllegalArgumentException if the date format is invalid
     */
    public static String parseSelectedDate(String date) {
        LocalDate localDate = switch (date.toLowerCase()) {
            case "today" -> LocalDate.now();
            case "tomorrow" -> LocalDate.now().plusDays(1);
            case "yesterday" -> LocalDate.now().minusDays(1);
            default -> parseWithMultipleFormats(date);
        };

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT_CURRENT_DATE);
        return localDate.format(outputFormatter);

    }

    /**
     * Calculates a new date by adding or subtracting days from a base date.
     * <p>
     * Example:
     * <pre>
     *   String newDate = DateUtils.getNewDate("13-02-2025", "5");  // returns date 5 days after Feb 13, 2025
     *   String pastDate = DateUtils.getNewDate("13-02-2025", "-3"); // returns date 3 days before Feb 13, 2025
     * </pre>
     *
     * @param rootDate the base date in any supported format (see parseWithMultipleFormats)
     * @param duration number of days to add (positive) or subtract (negative)
     * @return the new date formatted according to Constants.TIME_FORMAT_CURRENT_DATE
     * @throws IllegalArgumentException if rootDate format is invalid
     * @throws NumberFormatException if duration is not a valid number
     */
    public static String getNewDate(String rootDate, String duration){
        LocalDate newDate;
        newDate = parseWithMultipleFormats(rootDate).plusDays(Long.parseLong(duration));
        return parseSelectedDate(String.valueOf(newDate));
    }

    /**
     * Gets the current date and time as a formatted string.
     * <p>
     * Example:
     * <pre>
     *   String now = DateUtils.getCurrentDateTime(); // Returns current date and time
     * </pre>
     *
     * @return the current date and time formatted according to Constants.TIME_FORMAT_CURRENT_DATE_TIME
     */
    public static String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT_CURRENT_DATE_TIME);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Formats a LocalDate using a specified pattern.
     * <p>
     * Example:
     * <pre>
     *   LocalDate date = LocalDate.of(2025, 2, 13);
     *   String formatted = DateUtils.formatDate(date, "dd/MM/yyyy"); // Returns "13/02/2025"
     * </pre>
     *
     * @param date the date to format
     * @param format the date pattern (e.g., "dd-MM-yyyy", "yyyy/MM/dd")
     * @return the formatted date string
     * @throws IllegalArgumentException if the format pattern is invalid
     */
    public static String formatDate(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Formats a date using a predefined FormatStyle (SHORT, MEDIUM, LONG, FULL).
     * <p>
     * Example:
     * <pre>
     *   LocalDate date = LocalDate.of(2025, 2, 13);
     *   String formatted = DateUtils.formatStyleDate(date, FormatStyle.MEDIUM); // Returns e.g., "Feb 13, 2025"
     *   String longFormat = DateUtils.formatStyleDate(date, FormatStyle.LONG); // Returns e.g., "February 13, 2025"
     * </pre>
     *
     * @param date the date to format
     * @param formatStyle the format style (FormatStyle.SHORT, MEDIUM, LONG, or FULL)
     * @return the formatted date string according to the specified style
     */
    public static String formatStyleDate(LocalDate date, FormatStyle formatStyle) {
        return date.format(DateTimeFormatter.ofLocalizedDate(formatStyle));
    }

    /**
     * Gets the month name from a date with the specified text style.
     * <p>
     * Example:
     * <pre>
     *   LocalDate date = LocalDate.of(2025, 2, 13);
     *   String fullName = DateUtils.getMonthByTextStyle(date, TextStyle.FULL); // Returns "February"
     *   String shortName = DateUtils.getMonthByTextStyle(date, TextStyle.SHORT); // Returns "Feb"
     * </pre>
     *
     * @param date the date from which to extract the month
     * @param textStyle the text style for the month name (e.g., TextStyle.FULL, TextStyle.SHORT)
     * @return the month name in English according to the specified text style
     */
    public static String getMonthByTextStyle(LocalDate date, TextStyle textStyle) {
        return date.getMonth().getDisplayName(textStyle, Locale.ENGLISH);
    }

    /**
     * Gets the year of a date as a string.
     * <p>
     * Example:
     * <pre>
     *   LocalDate date = LocalDate.of(2025, 2, 13);
     *   String year = DateUtils.getYearToString(date); // Returns "2025"
     * </pre>
     *
     * @param date the date from which to extract the year
     * @return the year as a string
     */
    public static String getYearToString(LocalDate date) {
        return Integer.toString(date.getYear());
    }
}
