package com.auto.ht.projects.vietjet;

import com.auto.ht.base.BaseTest;
import com.auto.ht.vietjet.page.HomePage;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.sleep;

public class Case002Test extends BaseTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Case002Test.class);
    private final HomePage homePage = new HomePage();

    @Test
    public void secondVietJetTest() {
        homePage.openHomePage();
        log.info("Running second test in parallel");
        sleep(5000); // Wait for the page to load
        
        // Adding a log message to track parallel execution
        log.info("TestCase002 is running in thread: " + Thread.currentThread().getId());
    }
}
