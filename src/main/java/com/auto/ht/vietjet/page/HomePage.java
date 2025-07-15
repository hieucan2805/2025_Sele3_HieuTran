package com.auto.ht.vietjet.page;

import com.auto.ht.components.CalendarComponent;
import com.auto.ht.helpers.LocatorHelper;
import com.auto.ht.models.BookingInformationModel;
import com.auto.ht.models.PassengerModel;
import com.auto.ht.utils.*;
import com.auto.ht.vietjet.enums.TypeFlight;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class HomePage extends BasePage {
    @Getter
    private final LocatorHelper localeBundle = new LocatorHelper(HomePage.class.getSimpleName());

    // Calendar component for reusable calendar operations
    private final CalendarComponent calendarComponent = new CalendarComponent();
    // Calendar component with custom locators for a specific scenario if needed

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

    //Actions block
    @Step("Select the {typeFlight} Flight")
    public void clickTypeOfFlight(String typeFlight) {
        String type_xpath = TypeFlight.fromName(typeFlight).getXPathKey();
        String typeFlight_newXpath = localeBundle.updateLocatorWithDynamicText(typeOfFlight, type_xpath);

        $x(typeFlight_newXpath).click();
    }

    @Step("Select the {from} Airport and {to} Airport")
    public void selectAirport(String from, String to) {
//        String fromPort = Airport.findByName(from);
//        String toPort = Airport.findByName(to);

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
    public void selectDateInCalendar(String date) {
        calendarComponent.selectDate(date);
    }

    public void clickDepartureDateCalendar() {
        calendarComponent.openDepartureDateCalendar();
    }

    public void clickReturnDateCalendar() {
        calendarComponent.openReturnDateCalendar();
    }

    public void selectDepartureDateAndReturnDate(String deptDate, String returnDate) {
        calendarComponent.selectDepartureAndReturnDates(deptDate, returnDate);
    }

    public void selectDepartureDateAndDuration(String deptDate, String duration) {
        calendarComponent.selectDepartureAndDuration(deptDate, duration);
    }

    @Step("Select passenger")
    public void inputPassenger(PassengerModel passenger) {
        inputPassenger("adults", passenger.getAdults());
        inputPassenger("children", passenger.getChild());
        inputPassenger("baby", passenger.getBaby());
    }

    public void inputPassenger(String passenger, String number) {
        String labelXpath = String.format(labelPassenger, passenger);
        String buttonIncreaseXpath = String.format(buttonIncreasePassenger, passenger);
        String buttonDecreaseXpath = String.format(buttonDecreasePassenger, passenger);
        String currentCount = $x(labelXpath).shouldBe(visible, Constants.SHORT_WAIT).getText();

        if (!$x(buttonSpecialAssistanceRequest).isDisplayed())
            $x(dropdownPassenger).click();

        while (Integer.parseInt(currentCount) != Integer.parseInt(number)) {
            if (Integer.parseInt(currentCount) < Integer.parseInt(number)) {
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

    public void clickSearch() {
        $x(buttonSearchFlight).shouldBe(visible, Constants.VERY_SHORT_WAIT).click();
    }

    @Step("Fill information to search")
    public void fillFlightInfo(BookingInformationModel bookingInformationModel) {
        clickTypeOfFlight(bookingInformationModel.getType());

        selectAirport(bookingInformationModel.getFrom(), bookingInformationModel.getTo());
        if (bookingInformationModel.getDuration().isEmpty()) {
            selectDateInCalendar(bookingInformationModel.getDepartureDate());
        } else {
            selectDepartureDateAndDuration(bookingInformationModel.getDepartureDate(), bookingInformationModel.getDuration());
        }

        inputPassenger(bookingInformationModel.getPassenger());
        collapsePassengerPanel();
    }

    @Step("Search Flight with information")
    public void searchFlightWithInfo(BookingInformationModel bookingInformationModel) {
        fillFlightInfo(bookingInformationModel);

        clickSearch();
    }
}
