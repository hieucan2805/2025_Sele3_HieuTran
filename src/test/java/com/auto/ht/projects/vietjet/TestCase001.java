package com.auto.ht.projects.vietjet;

import com.auto.ht.base.BaseTest;
import com.auto.ht.projects.vietjet.dataprovider.VietJetTestcasesDataProvider;
import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.page.HomePage;
import com.auto.ht.projects.vietjet.page.PassengerInfoPage;
import com.auto.ht.projects.vietjet.page.SelectFlightPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class TestCase001 extends BaseTest {

    private final HomePage homePage = new HomePage();
    private final SelectFlightPage selectFightPage = new SelectFlightPage();
    private final PassengerInfoPage passengerInfoPage = new PassengerInfoPage();
    private final SoftAssert softAssert = new SoftAssert();

    @Test(dataProvider = "testcase001Data", dataProviderClass = VietJetTestcasesDataProvider.class)
    public void vietJetTest(BookingInformationModel info) {
        //Go to Home Page and search for flight
        homePage.openHomePage();
        homePage.searchFlightWithInfo(info);

        //Choose the cheapest flight
        selectFightPage.chooseCheapestTicketAndContinue();

        //Verify the Passenger Info Form is displayed
        Assert.assertTrue(passengerInfoPage.verifyPassengerInfoFormIsDisplayed(),
                "❌ Missing the Passenger Information Form.");

        //Verify Ticket Information
        softAssert.assertEquals(PassengerInfoPage.getTypeOfFlightText(), info.getType(),
                "❌ Mismatch in Type of Flight title. Expected: [" + info.getType() + "], but Found: [" + PassengerInfoPage.getTypeOfFlightText() + "]");
        softAssert.assertEquals(PassengerInfoPage.getFromAirport(), info.getFrom(),
                "❌ Mismatch in From Airport. Expected: [" + info.getFrom() + "], but Found: [" + PassengerInfoPage.getFromAirport() + "]");
        softAssert.assertEquals(PassengerInfoPage.getDestinationAirport(), info.getTo(),
                "❌ Mismatch in Destination Airport. Expected: [" + info.getTo() + "], but Found: [" + PassengerInfoPage.getDestinationAirport() + "]");
        softAssert.assertEquals(PassengerInfoPage.getPassengerInfo().toString(), info.getPassenger().toString(),
                "❌ Mismatch in Passenger Info. Expected: [" + info.getPassenger().toString() + "], but Found: [" + PassengerInfoPage.getPassengerInfo().toString() + "]");
        softAssert.assertAll();
    }
}
