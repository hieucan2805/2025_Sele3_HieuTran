package com.auto.ht.base;

import com.auto.ht.config.BrowserConfig;
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

    @BeforeMethod
    public void setup() {
        // Use Selenide's Configuration.remote directly instead of reading from properties file
        String remoteUrl = Configuration.remote;

        // Configure browser based on remote URL setting
        if (remoteUrl != null && !remoteUrl.isEmpty() && !remoteUrl.equalsIgnoreCase("false")) {
            BrowserConfig.configureGrid(Configuration.browser, remoteUrl);
        } else {
            log.info("Setting up local execution with browser: {}", Configuration.browser);
            Configuration.remote = null;

            // Configure Chrome browser if selected
            if (Configuration.browser.equalsIgnoreCase("chrome")) {
                BrowserConfig.configureChrome();
            }
        }

        log.info("Selenide Configuration: browser={}, browserSize={}, timeout={}, baseUrl={}, headless={}, pageLoadStrategy={}, remote={}",
                Configuration.browser,
                Configuration.browserSize,
                Configuration.timeout,
                Configuration.baseUrl,
                Configuration.headless,
                Configuration.pageLoadStrategy,
                Configuration.remote);

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
