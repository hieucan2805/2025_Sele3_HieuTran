package com.auto.ht.projects;

import com.auto.ht.base.DriverFactory;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static java.lang.invoke.MethodHandles.lookup;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

public class TestBase {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    @BeforeClass
    public void BeforeClass() {
        DriverFactory.setupDriver();
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
        log.info("Start {} TestNG testcases in {}", getClass().getName(), Configuration.browser);
    }

    @AfterMethod
    public void tearDown() {
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("BASE_URL", Configuration.baseUrl)
                        .put("WebDriver", String.valueOf(getWebDriver()))
                        .put("UserAgent", getUserAgent())
                        .put("isHeadless", String.valueOf(isHeadless()))
                        .build(), System.getProperty("user.dir") + "/allure-results/");

        // Close the browser after each test
        Selenide.closeWebDriver();
    }
}
