package com.auto.ht.components;

import com.auto.ht.helpers.DateHelper;
import com.auto.ht.utils.Constants;
import io.qameta.allure.Step;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class CalendarComponent {
    // Calendar locators
    private final String panelCalendar = "//div[contains(@class,'rdrCalendarWrapper')]";
    private final String labelMonthInCalendar = "//div[@class='rdrMonthName']";
    private final String buttonPrevMonth = "//button[@class='rdrNextPrevButton rdrPprevButton']";
    private final String buttonNextMonth = "//button[@class='rdrNextPrevButton rdrNextButton']";
    private final String labelDateInCalendar = "//div[text()='%s']//following-sibling::div[@class='rdrDays']//span[text()='%s']";
    private final String buttonDateAtCalendar = "//div[@class='rdrMonth' and contains(div,'%s')]//span[text()='%s']";

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
     * Default constructor with default locators
     */
    public CalendarComponent() {
        this.buttonDepartureDate = "//input[@class='MuiInputBase-input MuiOutlinedInput-input' and not(@id='arrivalPlaceDesktop')]//ancestor::div[.//div[@role='button']]/div[@role='button']";
        this.buttonReturnDate = "//img[@src='/static/media/switch.d8860013.svg']/following-sibling::div/following-sibling::div//p";
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
    public void selectDate(String date) {
        String[] dateParts = DateHelper.formatDateForCalendar(date);
        String targetDate = dateParts[0];
        String targetMonth = dateParts[1];
        String dateTmpXpath = String.format(labelDateInCalendar, targetMonth, targetDate);

        // Ensure calendar is open
        if (!$x(panelCalendar).shouldHave(visible, Constants.VERY_SHORT_WAIT).isDisplayed()) {
            $x(buttonReturnDate).click();
        }

        navigateToMonth(targetMonth);
        $x(dateTmpXpath).click();
    }

    /**
     * Selects both departure and return dates
     * @param departureDate Departure date
     * @param returnDate Return date
     */
    @Step("Select departure date {departureDate} and return date {returnDate}")
    public void selectDepartureAndReturnDates(String departureDate, String returnDate) {
        selectDate(departureDate);
        selectDate(returnDate);
    }

    /**
     * Selects departure date and calculates return date based on duration
     * @param departureDate Departure date
     * @param durationDays Number of days to add for the return date
     */
    @Step("Select departure date {departureDate} with duration of {durationDays} days")
    public void selectDepartureAndDuration(String departureDate, String durationDays) {
        selectDate(departureDate);

        // Calculate return date based on departure date + duration
        String returnDateStr = DateHelper.addDaysToDate(departureDate, durationDays);

        // Select the return date
        selectDate(returnDateStr);
    }

    /**
     * Checks if the calendar panel is visible
     * @return true if visible, false otherwise
     */
    public boolean isCalendarVisible() {
        return $x(panelCalendar).isDisplayed();
    }


}
