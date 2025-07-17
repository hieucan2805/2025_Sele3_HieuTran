package com.auto.ht.projects.vietjet;

import com.auto.ht.base.BaseTest;
import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.page.HomePage;
import com.auto.ht.projects.vietjet.page.PassengerInfoPage;
import com.auto.ht.projects.vietjet.page.SelectFlightPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCase001 extends BaseTest {

    private final HomePage homePage = new HomePage();
    private final SelectFlightPage selectFightPage = new SelectFlightPage();
    private final PassengerInfoPage passengerInfoPage = new PassengerInfoPage();

    @Test(dataProvider = "flightSearchDataProvider", dataProviderClass = com.auto.ht.projects.vietjet.dataprovider.TestCase001Provider.class)
    public void vietJetTest(BookingInformationModel info) {
        homePage.openHomePage();

        homePage.searchFlightWithInfo(info);

        selectFightPage.cancelAds();

        selectFightPage.chooseCheapestTicketAndContinue();

        //Verify the Passenger Info Form is displayed
        Assert.assertTrue(passengerInfoPage.verifyPassengerInfoFormIsDisplayed());

        //Verify Ticket Information
        Assert.assertTrue(passengerInfoPage.verifyTicketInfo(info));
    }
}
