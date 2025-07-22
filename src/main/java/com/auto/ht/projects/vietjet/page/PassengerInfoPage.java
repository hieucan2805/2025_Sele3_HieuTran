package com.auto.ht.projects.vietjet.page;

import com.auto.ht.helpers.LocatorHelper;
import com.auto.ht.projects.vietjet.enums.FlightType;
import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.models.PassengerModel;
import com.auto.ht.utils.Constants;
import io.qameta.allure.Step;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String getFromAirport() {
        log.info("Getting From Airport");
        return extractAirportCode($x(labelFrom).shouldBe(visible, Constants.SHORT_WAIT).getText());
    }

    @Step("Verify From Airport")
    public boolean verifyFromAirport(String airportCode) {
        log.info("Verifying From Airport with code: {}", airportCode);
        return getFromAirport().contains(airportCode);
    }

    public String getDestinationAirport() {
        log.info("Getting Destination Airport");
        return extractAirportCode($x(labelDestination).shouldBe(visible, Constants.SHORT_WAIT).getText());
    }

    @Step("Verify Destination Airport")
    public boolean verifyDestinationAirport(String airportName) {
        log.info("Verifying Destination Airport with name: {}", airportName);
        return getDestinationAirport().contains(airportName);
    }

    public FlightType getTypeOfFlight() {
        String tmp_text = $x(labelTypeAndPassenger).shouldBe(visible, Constants.SHORT_WAIT).getText();
        String flightTypeStr = (tmp_text.split("\\|")[0].trim()).replaceAll("[^A-Z]", "").toLowerCase().replace("flight", "");
        return FlightType.fromName(flightTypeStr);
    }

    @Step("Verify Type of Flight")
    public boolean verifyTypeOfFlight(FlightType expectedFlightType) {
        log.info("Verifying Type of Flight: {}", expectedFlightType.getName());
        FlightType actualFlightType = getTypeOfFlight();
        return actualFlightType == expectedFlightType;
    }

    public PassengerModel getPassengerInfo() {
        String tmp_text = ($x(labelTypeAndPassenger).shouldBe(visible, Constants.SHORT_WAIT).getText()).split("\\|")[1].trim();
        // Extract passenger information from the text
        if (tmp_text.isEmpty()) {
            return new PassengerModel(1, 0, 0);
        }
        // Remove all alphabet characters and spaces, keeping only digits and commas
        tmp_text = tmp_text.replaceAll("[a-zA-Z\\s]", "");
        return new PassengerModel(tmp_text);
    }

    @Step("Verify Passenger Information")
    public boolean verifyPassengerInfo(PassengerModel passengerInfo) {
        log.info("Verifying Passenger Information");
        PassengerModel actualPassengerInfo = getPassengerInfo();
        return actualPassengerInfo.getAdults() == passengerInfo.getAdults() &&
                actualPassengerInfo.getChild() == passengerInfo.getChild() &&
                actualPassengerInfo.getBaby() == passengerInfo.getBaby();
    }

    @Step("Verify Ticket Information")
    public boolean verifyTicketInfo(BookingInformationModel ticketInfo) {
        return verifyFromAirport(ticketInfo.getFrom()) &&
                verifyDestinationAirport(ticketInfo.getTo()) &&
                verifyTypeOfFlight(ticketInfo.getType()) &&
                verifyPassengerInfo(ticketInfo.getPassenger());
    }

    public static String extractAirportCode(String fullText) {
        if (fullText == null || fullText.isEmpty()) {
            throw new IllegalArgumentException("Input text is null or empty.");
        }

        // Regex tìm 3 chữ in hoa nằm trong dấu ngoặc, ví dụ: ( HAN )
        Pattern pattern = Pattern.compile("\\((\\s*[A-Z]{3})\\s*\\)");
        Matcher matcher = pattern.matcher(fullText);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            throw new IllegalArgumentException("There is no airport code in: [" + fullText + "]");
        }
    }
}