package com.auto.ht.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.remote.DesiredCapabilities;
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
        boolean isGridEnabled = Boolean.parseBoolean(System.getProperty("selenide.gridEnabled", "false"));
        String browser = Configuration.browser;
        String gridURL = System.getProperty("selenide.gridURL");

        // Configure Selenide to use Selenium Grid if enabled
        if (isGridEnabled && gridURL != null && !gridURL.isEmpty()) {
            log.info("Setting up Grid execution with browser: {}, gridURL: {}", browser, gridURL);
            configureSeleniumGrid(browser, gridURL);
        } else {
            log.info("Setting up local execution with browser: {}", browser);
        }

        log.info("Selenide Configuration: browser={}, browserSize={}, timeout={}, baseUrl={},  headless={}, pageLoadStrategy={}, remote={}",
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
            // Close the browser after each test
            Selenide.closeWebDriver();
        }
    }

    /**
     * Configures Selenide to run tests on Selenium Grid with browser-specific capabilities
     *
     * @param browser the browser to use (chrome, edge)
     * @param gridURL the Selenium Grid URL to connect to
     */
    private void configureSeleniumGrid(String browser, String gridURL) {
        Configuration.remote = gridURL;

        // Set up browser-specific capabilities
        if (browser != null) {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            switch (browser.toLowerCase()) {
                case "edge":
                    capabilities.setBrowserName("Microsoft Edge");
                    capabilities.setCapability("enableVNC", true);
                    capabilities.setCapability("enableVideo", false);
                    Configuration.browserCapabilities = capabilities;
                    break;
                case "chrome":
                    capabilities.setBrowserName("Google Chrome");
                    capabilities.setCapability("enableVNC", true);
                    capabilities.setCapability("enableVideo", false);
                    Configuration.browserCapabilities = capabilities;
                    break;
                default:
                    log.warn("Unsupported browser: {}", browser);
                    break;
            }
        } else {
            log.warn("Browser must be specified when using grid");
        }
    }
}