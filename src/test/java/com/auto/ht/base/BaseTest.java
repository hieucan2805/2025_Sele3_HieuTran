package com.auto.ht.base;

import com.auto.ht.config.TestConfiguration;
import com.auto.ht.config.WebDriverManager;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;
import static java.lang.invoke.MethodHandles.lookup;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());
    private String tempUserDataDir; // To store user data dir path for cleanup
    private final WebDriverManager webDriverManager = new WebDriverManager();

    @BeforeMethod
    public void setup() {
        // Initialize configuration from properties
        TestConfiguration.initializeConfiguration();

        // Configure WebDriver based on settings
        tempUserDataDir = webDriverManager.configureWebDriver();

        // Log configuration
        log.info("Selenide Configuration: browser={}, browserSize={}, timeout={}, baseUrl={}, headless={}, pageLoadStrategy={}, remote={}",
                Configuration.browser,
                Configuration.browserSize,
                Configuration.timeout,
                Configuration.baseUrl,
                Configuration.headless,
                Configuration.pageLoadStrategy,
                Configuration.remote);

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

            // Clean up any temporary user data directory using the WebDriverManager
            webDriverManager.cleanupTempUserDataDir(tempUserDataDir);

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