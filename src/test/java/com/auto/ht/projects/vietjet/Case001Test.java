package com.auto.ht.projects.vietjet;



import com.auto.ht.base.BaseTest;
import com.auto.ht.vietjet.page.HomePage;
import com.codeborne.selenide.Selenide;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.sleep;

public class Case001Test extends BaseTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Case001Test.class);
    private final HomePage homePage = new HomePage();

    @Test
    public void vietJetTest() {
        homePage.openHomePage();

        // Log thread ID to demonstrate parallel execution
        log.info("TestCase001 is running in thread: " + Thread.currentThread().getId());

        sleep(5000); // Wait for the page to load

        System.out.println(Selenide.$x("//input[@name='promotionCode']").getAttribute("placeholder"));

        // Adding a failing assertion to test retry mechanism
//        log.info("Intentionally failing test to demonstrate retry mechanism");
//        Assert.fail("This test is intentionally failed to demonstrate the retry mechanism");
    }
}
