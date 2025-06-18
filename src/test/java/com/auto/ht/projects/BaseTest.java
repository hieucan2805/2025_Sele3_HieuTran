package com.auto.ht.projects;

import com.auto.ht.utils.PropertyLoader;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static java.lang.invoke.MethodHandles.lookup;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    @BeforeClass
    public void beforeClass() {
        // Load properties from selenide.properties
        PropertyLoader.loadProperties("selenide.properties");
        log.info("Starting tests in {}", BaseTest.class.getName());

        // Enable Allure reports (add listener only once)
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @BeforeMethod
    public void beforeMethod(){
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Browser", getWebDriver().getClass().getName())
                        .put("URL", Configuration.baseUrl)
                        .put("Headless", String.valueOf(isHeadless()))
                        .put("UserAgent", getUserAgent())
                        .build(), System.getProperty("user.dir") + "/target/allure-results/");
    }

    @AfterMethod
    public void tearDown(){
        Selenide.closeWebDriver();
    }
}
