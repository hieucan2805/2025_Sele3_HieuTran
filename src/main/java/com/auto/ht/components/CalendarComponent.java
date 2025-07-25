package com.auto.ht.components;

import com.auto.ht.helpers.DateHelper;
import com.auto.ht.helpers.LanguageHelper;
import com.auto.ht.utils.Constants;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

@Slf4j
public class CalendarComponent {
    Locale locale = LanguageHelper.getLocale();

    // Calendar locators
    private final String panelCalendar = "//div[contains(@class,'rdrCalendarWrapper')]";
    private final String labelMonthInCalendar = "//div[@class='rdrMonthName']";
    private final String buttonPrevMonth = "//button[@class='rdrNextPrevButton rdrPprevButton']";
    private final String buttonNextMonth = "//button[@class='rdrNextPrevButton rdrNextButton']";
    private final String labelDateInCalendar = "//div[text()='%s']//following-sibling::div[@class='rdrDays']//span[text()='%s']";
    private final String buttonDateAtCalendar = "//div[@class='rdrMonth' and contains(div,'%s')]//span[text()='%s']";

    // Default locators for date buttons

    // Customizable trigger buttons
    private String buttonDepartureDate;
    private String buttonReturnDate;

    /**
     * Constructor with custom trigger button locators
     * @param departureDateButtonLocator XPath locator for the departure date button
     * @param returnDateButtonLocator XPath locator for the return date button
     */
    public CalendarComponent(String departureDateButtonLocator, String returnDateButtonLocator) {
        this.buttonDepartureDate = departureDateButtonLocator;
        this.buttonReturnDate = returnDateButtonLocator;
    }

    /**
     * Opens the departure date calendar
     */
    @Step("Open departure date calendar")
    public void openDepartureDateCalendar() {
        $x(buttonDepartureDate).shouldBe(visible, Constants.SHORT_WAIT).click();
        $x(panelCalendar).shouldBe(visible, Constants.SHORT_WAIT);
    }

    /**
     * Opens the return date calendar
     */
    @Step("Open return date calendar")
    public void openReturnDateCalendar() {
        $x(buttonReturnDate).shouldBe(visible, Constants.SHORT_WAIT).click();
        $x(panelCalendar).shouldBe(visible, Constants.SHORT_WAIT);
    }

    /**
     * Navigates to the specified month in the calendar
     * @param month Month to navigate to
     */
    @Step("Navigate to {month}")
    public void navigateToMonth(String month) {
        $x(labelMonthInCalendar).shouldBe(visible, Constants.VERY_SHORT_WAIT);

        while (!($x(labelMonthInCalendar).getText().trim()).equalsIgnoreCase(month)) {
            $x(buttonNextMonth).click();
            $x(labelMonthInCalendar).shouldHave(visible, Constants.VERY_SHORT_WAIT);
        }
    }

    /**
     * Selects a specific date in the calendar
     * @param date Date to select in format supported by DateHelper.formatDateForCalendar
     */
    @Step("Select {date} in Calendar")
    public void selectDate(LocalDate date) {
        // Validate that the date is not in the past
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            log.warn("Cannot select a date in the past: {}. Using today's date instead.", date);
            date = today; // Use today's date if the requested date is in the past
        }

        // Format day and month/year separately to avoid issues with split
        String targetDate = date.format(DateTimeFormatter.ofPattern("d").withLocale(locale));
        String targetMonthAndYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale));

        log.info("Selecting date: {} (day: {}, month/year: {})", date, targetDate, targetMonthAndYear);

        String dateTmpXpath = String.format(labelDateInCalendar, targetMonthAndYear, targetDate);
        log.debug("Using XPath: {}", dateTmpXpath);

        // Ensure calendar is open
        if (!$x(panelCalendar).is(visible)) {
            $x(buttonReturnDate).click();
        }

        navigateToMonth(targetMonthAndYear);

        // Check if the date element exists and is clickable
        if (!$x(dateTmpXpath).exists()) {
            log.warn("Date element not found. This might be because the date is not available for selection.");
            // Try alternative XPath if the first one fails
            String alternativeXPath = String.format(buttonDateAtCalendar, targetMonthAndYear, targetDate);
            log.info("Trying alternative XPath: {}", alternativeXPath);
            $x(alternativeXPath).click();
        } else {
            $x(dateTmpXpath).click();
        }
    }

    /**
     * Selects both departure and return dates with validation
     * @param departureDate Departure date
     * @param returnDate Return date
     */
    @Step("Select departure date {departureDate} and return date {returnDate}")
    public void selectDepartureAndReturnDates(LocalDate departureDate, LocalDate returnDate) {
        // Ensure return date is not before departure date
        if (returnDate.isBefore(departureDate)) {
            log.warn("Return date {} is before departure date {}. Adjusting return date.",
                    returnDate, departureDate);
            returnDate = departureDate.plusDays(1); // Default to next day if invalid
        }

        selectDate(departureDate);
        selectDate(returnDate);
    }

    /**
     * Selects departure date and calculates return date based on duration
     * @param departureDate Departure date
     * @param durationDays Number of days to add for the return date
     */
    @Step("Select departure date {departureDate} with duration of {durationDays} days")
    public void selectDepartureAndDuration(LocalDate departureDate, int durationDays) {
        // Validate duration
        if (durationDays <= 0) {
            log.warn("Invalid duration: {}. Using 1 day instead.", durationDays);
            durationDays = 1; // Minimum 1 day duration
        }

        selectDate(departureDate);

        // Calculate return date based on departure date + duration
        LocalDate returnDate = departureDate.plusDays(durationDays);

        // Select the return date
        selectDate(returnDate);
    }

    /**
     * Checks if the calendar panel is visible
     * @return true if visible, false otherwise
     */
    public boolean isCalendarVisible() {
        return $x(panelCalendar).isDisplayed();
    }

    /**
     * Gets the appropriate DateTimeFormatter based on the current language setting
     * @return DateTimeFormatter configured for the current language
     */
    private DateTimeFormatter getLocalizedDateFormatter() {
        String language = System.getProperty("selenide.language", "en");

        // Create proper locale based on language
        Locale locale;
        switch (language.toLowerCase()) {
            case "vi" -> locale = new Locale("vi", "VN");  // Vietnamese (Vietnam)
            case "en" -> locale = Locale.ENGLISH;          // English
            default -> locale = Locale.ENGLISH;            // Default to English
        }

        // Use a consistent pattern but let the Locale handle the month names and formatting
        return DateTimeFormatter.ofPattern(Constants.TIME_FORMAT_CURRENT_DATE).withLocale(locale);
    }
}
