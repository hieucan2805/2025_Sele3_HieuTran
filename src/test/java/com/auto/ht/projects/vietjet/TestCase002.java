package com.auto.ht.projects.vietjet;

import com.auto.ht.base.BaseTest;
import com.auto.ht.projects.vietjet.dataprovider.VietJetTestcasesDataProvider;
import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.page.HomePage;

import com.codeborne.selenide.Selenide;
import org.testng.annotations.Test;

public class TestCase002 extends BaseTest {

    private final HomePage homePage = new HomePage();


    @Test(dataProvider = "testcase002Data", dataProviderClass = VietJetTestcasesDataProvider.class)
    public void vietJetTest(BookingInformationModel info) {

        //Go to Home Page and search for flight
        homePage.openHomePage();
        homePage.searchFlightWithInfo(info);


    }
}
