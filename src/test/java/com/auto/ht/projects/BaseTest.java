package com.auto.ht.projects;

import com.auto.ht.utils.PropertyLoader;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodHandles.lookup;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    @BeforeClass
    public static void BeforeClass() {
        // Load properties from selenide.properties
        PropertyLoader.loadProperties("selenide.properties");

        log.info("Starting tests in {}", BaseTest.class.getName());

        // Enable Allure reports
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }
}
