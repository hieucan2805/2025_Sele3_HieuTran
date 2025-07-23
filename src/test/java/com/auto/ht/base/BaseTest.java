package com.auto.ht.base;

import com.auto.ht.config.TestCleanupListener;
import com.auto.ht.config.TestConfiguration;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;
import static java.lang.invoke.MethodHandles.lookup;

@Listeners(TestCleanupListener.class)
public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    @BeforeMethod
    public void setup() {
        // Get the current test method name
        String testMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        // Initialize configuration from properties and set up WebDriver
        TestConfiguration.initializeConfiguration(getClass().getName(), testMethodName);

        // Setup Allure reporting
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
        log.info("Thread ID: {} - Starting {} test method in {}",
                Thread.currentThread().threadId(),
                getClass().getName(),
                Configuration.browser);
    }

    @AfterMethod
    public void tearDown() {
        try {
            log.info("Thread ID: {} - Finishing test method and cleaning up", Thread.currentThread().threadId());

            // Only write environment data if WebDriver exists
            if (getWebDriver() != null) {
                allureEnvironmentWriter(
                        ImmutableMap.<String, String>builder()
                                .put("BASE_URL", Configuration.baseUrl)
                                .put("WebDriver", String.valueOf(getWebDriver()))
                                .put("UserAgent", getUserAgent())
                                .put("isHeadless", String.valueOf(isHeadless()))
                                .put("Remote", String.valueOf(Configuration.remote != null))
                                .build(), System.getProperty("user.dir") + "/allure-results/");
            }

        } catch (Exception e) {
            log.error("Error in tearDown: {}", e.getMessage());
        } finally {
            // Only close browser if it was started
            try {
                if (getWebDriver() != null) {
                    Selenide.closeWebDriver();
                }
            } catch (Exception ignored) {
                // No webdriver to close
            }
        }
    }
}