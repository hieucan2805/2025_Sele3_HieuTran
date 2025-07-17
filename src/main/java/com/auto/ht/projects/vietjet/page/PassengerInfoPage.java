package com.auto.ht.projects.vietjet.page;

import com.auto.ht.helpers.LocatorHelper;
import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.models.PassengerModel;
import com.auto.ht.utils.Constants;
import io.qameta.allure.Step;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class PassengerInfoPage extends BasePage {
    @Getter
    private final LocatorHelper localeBundle = new LocatorHelper(PassengerInfoPage.class.getSimpleName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PassengerInfoPage.class);

    private final String frmPassengerInfoForm = "//i[@class = 'fa fa-male']/ancestor::div[contains(@style,'padding-bottom')]";
    private final String labelFrom = "//img[@src='/static/media/departure-icon.25d3557e.svg']//following-sibling::p";
    private final String labelDestination = "//img[@src='/static/media/arrival-icon.a05c5d78.svg']//following-sibling::p";
    private final String labelTypeAndPassenger = "//img[@src='/static/media/departure-icon.25d3557e.svg']//parent::div//preceding-sibling::p";

    //Method
    @Step("Verify Passenger Info Form is displayed")
    public boolean verifyPassengerInfoFormIsDisplayed() {
        log.info("Verifying Passenger Info Form is displayed");
        return $x(frmPassengerInfoForm).shouldBe(visible, Constants.MEDIUM_WAIT).isDisplayed();
    }

    @Step("Verify From Airport")
    public boolean verifyFromAirport(String airportCode) {
        log.info("Verifying From Airport with code: {}", airportCode);
        String fromAirport = $x(labelFrom).shouldBe(visible, Constants.SHORT_WAIT).getText();
        return fromAirport.contains(airportCode);
    }

    @Step("Verify Destination Airport")
    public boolean verifyDestinationAirport(String airportName) {
        log.info("Verifying Destination Airport with name: {}", airportName);
        String toAirport = $x(labelDestination).shouldBe(visible, Constants.SHORT_WAIT).getText();
        return toAirport.contains(airportName);
    }

    public String getTypeOfFlightText() {
        String tmp_text = $x(labelTypeAndPassenger).shouldBe(visible, Constants.SHORT_WAIT).getText();
        return tmp_text.split("\\|")[0].trim();
    }

    @Step("Verify Type of Flight")
    public boolean verifyTypeOfFlight(String typeOfFlight) {
        log.info("Verifying Type of Flight: {}", typeOfFlight);
        String flightType = getTypeOfFlightText();
        return flightType.equalsIgnoreCase(typeOfFlight);
    }

    public PassengerModel getPassengerInfo() {
        String tmp_text = ($x(labelTypeAndPassenger).shouldBe(visible, Constants.SHORT_WAIT).getText()).split("\\|")[1].trim();
        // Extract passenger information from the text
        if (tmp_text.isEmpty()) {
            return new PassengerModel("0", "0", "0");
        }
        // Remove all alphabet characters and spaces, keeping only digits and commas
        tmp_text = tmp_text.replaceAll("[a-zA-Z\\s]", "");
        return new PassengerModel(tmp_text);
    }

    @Step("Verify Passenger Information")
    public boolean verifyPassengerInfo(PassengerModel passengerInfo) {
        log.info("Verifying Passenger Information");
        PassengerModel actualPassengerInfo = getPassengerInfo();
        return actualPassengerInfo.getAdults().equals(passengerInfo.getAdults()) &&
                actualPassengerInfo.getChild().equals(passengerInfo.getChild()) &&
                actualPassengerInfo.getBaby().equals(passengerInfo.getBaby());
    }

    @Step("Verify Ticket Information")
    public boolean verifyTicketInfo(BookingInformationModel ticketInfo) {
        return verifyFromAirport(ticketInfo.getFrom()) &&
                verifyDestinationAirport(ticketInfo.getTo()) &&
                verifyTypeOfFlight(ticketInfo.getType()) &&
                verifyPassengerInfo(ticketInfo.getPassenger());
    }
}