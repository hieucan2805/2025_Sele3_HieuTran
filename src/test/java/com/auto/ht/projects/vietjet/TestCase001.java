package com.auto.ht.projects.vietjet;



import com.auto.ht.projects.TestBase;
import com.auto.ht.vietjet.page.HomePage;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.sleep;

public class TestCase001 extends TestBase {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TestCase001.class);
    private final HomePage homePage = new HomePage();

    @Test
    public void vietJetTest() {
        homePage.openHomePage();

        sleep(5000); // Wait for the page to load
    }
}
