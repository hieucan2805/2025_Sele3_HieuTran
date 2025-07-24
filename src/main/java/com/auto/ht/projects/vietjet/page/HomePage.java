package com.auto.ht.projects.vietjet.page;

import com.auto.ht.components.CalendarComponent;
import com.auto.ht.helpers.DateHelper;
import com.auto.ht.helpers.LocatorHelper;
import com.auto.ht.projects.vietjet.enums.FlightType;
import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.models.PassengerModel;
import com.auto.ht.utils.*;
import io.qameta.allure.Step;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class HomePage extends BasePage {
    @Getter
    private final LocatorHelper localeBundle = new LocatorHelper(HomePage.class.getSimpleName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HomePage.class);
    private final CalendarComponent calendarComponent = new CalendarComponent();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String typeOfFlight = "//span[text()='%s']";
    private final String inputFrom = "//input[@class='MuiInputBase-input MuiOutlinedInput-input' and not(@id)]";
    private final String buttonDepartureDate = "//input[@class='MuiInputBase-input MuiOutlinedInput-input' and not(@id='arrivalPlaceDesktop')]//ancestor::div[.//div[@role='button']]/div[@role='button']";
    private final String inputDestination = "//input[@class='MuiInputBase-input MuiOutlinedInput-input' and @id]";
    private final String buttonReturnDate = "//img[@src='/static/media/switch.d8860013.svg']/following-sibling::div/following-sibling::div//p";
    private final String optionAirportName = "//div[@id='panel1a-content']//div[text()='%s']";
    private final String dropdownPassenger = "//input[starts-with(@id,'input-base-custom-')]";
    private final String buttonDecreasePassenger = "//div[div[div[img[@alt='%s']]]]//button[1]";
    private final String labelPassenger = "//div[div[div[img[@alt='%s']]]]//button[1]//following-sibling::span[@weight]";
    private final String buttonIncreasePassenger = "//div[div[div[img[@alt='%s']]]]//button[2]";
    private final String buttonSpecialAssistanceRequest = "//span[@customcolor='hint']";
    private final String buttonSearchFlight = "//button[@tabindex='0']//span[text()]/parent::button";
    private final String labelCheapestFare = "//h3[text()='%s']";

    //Actions block
    @Step("Select the {flightType} Flight")
    public void clickTypeOfFlight(FlightType flightType) {
        String newXpath;
        if (flightType == FlightType.ONE_WAY) {
            newXpath = localeBundle.updateLocatorWithDynamicText(typeOfFlight, "radio.oneWay");
        } else {
            newXpath = localeBundle.updateLocatorWithDynamicText(typeOfFlight, "radio.roundTrip");
        }
        $x(newXpath).click();
    }

    @Step("Select the {from} Airport and {to} Airport")
    public void selectAirport(String from, String to) {
        inputFromLocation(from);
        clickOptionAirportName(from);

        inputDestinationLocation(to);
        clickOptionAirportName(to);
    }

    public void inputFromLocation(String location) {
        $x(inputFrom).shouldBe(visible, Constants.VERY_SHORT_WAIT).click();
        $x(inputFrom).setValue(location);
    }

    public void inputDestinationLocation(String location) {
        $x(inputDestination).shouldBe(visible, Constants.VERY_SHORT_WAIT).click();
        $x(inputDestination).setValue(location);
    }

    public void clickOptionAirportName(String airport) {
        String formatedOptionAirportName = String.format(optionAirportName, airport);
        $x(formatedOptionAirportName).shouldBe(visible, Constants.VERY_SHORT_WAIT).click();
    }

    @Step("Select {date} in Calendar")
    public void selectDateInCalendar(LocalDate date) {
        calendarComponent.selectDate(date);
    }

    public void clickDepartureDateCalendar() {
        calendarComponent.openDepartureDateCalendar();
    }

    public void clickReturnDateCalendar() {
        calendarComponent.openReturnDateCalendar();
    }

    public void selectDepartureDateAndReturnDate(LocalDate deptDate, LocalDate returnDate) {
        calendarComponent.selectDepartureAndReturnDates(deptDate, returnDate);
    }

    public void selectDepartureDateAndDuration(LocalDate deptDate, int duration) {
        calendarComponent.selectDepartureAndDuration(deptDate, duration);
    }

    @Step("Select passenger")
    public void inputPassenger(PassengerModel passenger) {
        inputPassenger("adults", passenger.getAdults());
        inputPassenger("children", passenger.getChild());
        inputPassenger("baby", passenger.getBaby());
    }

    private void inputPassenger(String passenger, int number) {
        String labelXpath = String.format(labelPassenger, passenger);
        String buttonIncreaseXpath = String.format(buttonIncreasePassenger, passenger);
        String buttonDecreaseXpath = String.format(buttonDecreasePassenger, passenger);
        String currentCount = $x(labelXpath).shouldBe(visible, Constants.SHORT_WAIT).getText();

        if (!$x(buttonSpecialAssistanceRequest).isDisplayed())
            $x(dropdownPassenger).click();

        while (Integer.parseInt(currentCount) != number) {
            if (Integer.parseInt(currentCount) < number) {
                $x(buttonIncreaseXpath).click(); // Click "+" if less
            } else {
                $x(buttonDecreaseXpath).click(); // Click "-" if more
            }

            $x(labelXpath).shouldNotHave(text(currentCount), Constants.VERY_SHORT_WAIT);
            currentCount = $x(labelXpath).getText();
        }
    }

    public void collapsePassengerPanel() {
        if ($x(buttonSpecialAssistanceRequest).isDisplayed())
            $x(dropdownPassenger).click();
    }

    public void clickCheapestFlightCheckbox() {
        String newXpath = localeBundle.updateLocatorWithDynamicText(labelCheapestFare, "text.cheapestFare");
        $x(newXpath).shouldBe(visible, Constants.SHORT_WAIT).click();
    }

    public void clickSearch() {
        $x(buttonSearchFlight).shouldBe(visible, Constants.VERY_SHORT_WAIT).click();
    }

    @Step("Fill information to search")
    public void fillFlightInfo(BookingInformationModel bookingInformationModel) {
        clickTypeOfFlight(bookingInformationModel.getType());

        selectAirport(bookingInformationModel.getFrom(), bookingInformationModel.getTo());
        if (bookingInformationModel.getDuration() == 0 || bookingInformationModel.getType() == FlightType.ONE_WAY) {
            selectDateInCalendar(bookingInformationModel.getDepartureDate());
        } else {
            selectDepartureDateAndDuration(bookingInformationModel.getDepartureDate(), bookingInformationModel.getDuration());
        }

        inputPassenger(bookingInformationModel.getPassenger());
        collapsePassengerPanel();
    }

    @Step("Fill information to search to find the cheapest flight")
    public void fillFlightInfoWithRange(BookingInformationModel bookingInformationModel) {
        clickTypeOfFlight(bookingInformationModel.getType());

        selectAirport(bookingInformationModel.getFrom(), bookingInformationModel.getTo());

        if (bookingInformationModel.getDuration() == 0 || bookingInformationModel.getType() == FlightType.ONE_WAY) {
            selectDateInCalendar(findStartDate(bookingInformationModel.getRange()));
        } else {
            selectDepartureDateAndDuration(findStartDate(bookingInformationModel.getRange()), bookingInformationModel.getDuration()
            );
        }

        inputPassenger(bookingInformationModel.getPassenger());
        collapsePassengerPanel();

        clickCheapestFlightCheckbox();
    }

    @Step("Search Flight with information")
    public void searchFlightWithInfo(BookingInformationModel bookingInformationModel) {
        if (bookingInformationModel.getDepartureDate() != null) {
            log.info("Searching flight with departure date: {}", bookingInformationModel.getDepartureDate());
            fillFlightInfo(bookingInformationModel);
        } else {
            log.info("Searching flight with range: {}", bookingInformationModel.getRange());
            fillFlightInfoWithRange(bookingInformationModel);
        }
        clickSearch();
    }

    /**
     * Format a LocalDate to the format required by the calendar component
     *
     * @param date LocalDate to format
     * @return Formatted date string
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    public LocalDate findStartDate(String range) {
        // Use the new DateHelper method to get the start date from range
        return DateHelper.getDateFromRange(range);
    }

    public String findEndDate(String range) {
        // Use the new DateHelper method to get the end date from range
        LocalDate startDateOfRange = DateHelper.getDateFromRange(range);
        LocalDate endDateOfRange = DateHelper.getEndDateFromRange(range, startDateOfRange);
        return formatDate(endDateOfRange);
    }

    /**
     * Find both start and end dates based on a range description
     *
     * @param range The range description (e.g., "next 7 days")
     * @return An array containing [startDate, endDate] as strings
     */
    public String[] findStartAndEndDates(String range) {
        LocalDate[] dates = DateHelper.getDatesFromRange(range);
        return new String[]{
                dates[0].toString(),
                dates[1].toString()
        };
    }
}
