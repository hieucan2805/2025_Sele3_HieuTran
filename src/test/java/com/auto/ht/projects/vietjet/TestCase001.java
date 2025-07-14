package com.auto.ht.projects.vietjet;

import com.auto.ht.base.BaseTest;
import com.auto.ht.models.BookingInformationModel;
import com.auto.ht.models.PassengerModel;
import com.auto.ht.vietjet.page.HomePage;
import com.auto.ht.vietjet.page.SelectFlightPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestCase001 extends BaseTest {

    private final HomePage homePage = new HomePage();
    private final SelectFlightPage selectFightPage = new SelectFlightPage();

    @DataProvider(name = "flightSearchDataProvider")
    public Object[][] flightSearchDataProvider() {
        return new Object[][]{
                {new BookingInformationModel("Round Trip",
                        "Thành phố Hồ Chí Minh", "Hà Nội", "tomorrow", "3",
                        new PassengerModel("2", "0", "0"))}
        };
    }

    @Test(dataProvider = "flightSearchDataProvider")
    public void vietJetTest(BookingInformationModel info) {
        homePage.openHomePage();

        homePage.searchFlightWithInfo(info);

        selectFightPage.cancelAds();

        selectFightPage.chooseCheapestTicketAndContinue();
    }
}
