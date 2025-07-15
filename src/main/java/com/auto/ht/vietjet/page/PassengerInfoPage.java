package com.auto.ht.vietjet.page;

import com.auto.ht.helpers.LocatorHelper;
import com.auto.ht.models.BookingInformationModel;
import com.auto.ht.utils.Constants;
import com.auto.ht.vietjet.enums.Airport;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import java.time.Duration;
import java.util.function.Supplier;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class PassengerInfoPage extends BasePage{
    @Getter
    private final LocatorHelper localeBundle = new LocatorHelper(PassengerInfoPage.class.getSimpleName());

    private final Supplier<SelenideElement> frmPassengerInfoForm = () -> $x("//i[@class = 'fa fa-male']/ancestor::div[contains(@style,'padding-bottom')]");
    private final String labelFrom = "//img[@src='/static/media/departure-icon.25d3557e.svg']//following-sibling::p";
    private final String labelTo = "//img[@src='/static/media/arrival-icon.a05c5d78.svg']//following-sibling::p";
    private final String labelTypeAndPassenger = "//img[@src='/static/media/departure-icon.25d3557e.svg']//parent::div//preceding-sibling::p";


    //Method
    @Step("Verify Passenger Info Form is displayed")
    public boolean verifyPassengerInfoFormIsDisplayed(){
        return frmPassengerInfoForm.get().isDisplayed();
    }

    public String getAirportCode(String airportName) {
        return ($x(airportName).getText().split("\\(")[1].trim()).split("\\)")[0].trim();
    }

    public String getTypeOfFlightText(){
        String tmp_text=  $x(labelTypeAndPassenger).shouldBe(visible, Constants.SHORT_WAIT).getText();
        return tmp_text.split("\\|")[0].trim();
    }

    public String getPassengerText(){
        String tmp_text=  $x(labelTypeAndPassenger).shouldBe(visible, Constants.SHORT_WAIT).getText();
        return tmp_text.split("\\|")[1].trim();
    }

    public boolean verifyTicketInfo(BookingInformationModel ticketInfo) {
//        String fromAirportCode = getAirportCode(Airport.findByName(ticketInfo.getFrom()).getName());
//        String toAirportCode = getAirportCode(Airport.findByName(ticketInfo.getTo()).getName());
//
//        return $x(labelFrom).shouldBe(visible, Constants.SHORT_WAIT)
//                .getText().contains(fromAirportCode) &&
//               $x(labelTo).shouldBe(visible, Constants.SHORT_WAIT)
//                .getText().contains(toAirportCode) &&
//               getTypeOfFlightText().equalsIgnoreCase(ticketInfo.getTypeOfFlight()) &&
//               getPassengerText().equalsIgnoreCase(ticketInfo.getPassenger());
        return false;
    }
}
