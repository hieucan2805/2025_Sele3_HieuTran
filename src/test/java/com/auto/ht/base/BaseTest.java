package com.auto.ht.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.chrome.ChromeOptions;
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
        // Use Selenide's Configuration.remote directly instead of reading from properties file
        String remoteUrl = Configuration.remote;

        // Configure Selenide to use Selenium Grid if remoteUrl is provided
        if (remoteUrl != null && !remoteUrl.isEmpty() && !remoteUrl.equalsIgnoreCase("false")) {
            log.info("Setting up Grid execution with browser: {}, remoteUrl: {}", Configuration.browser, remoteUrl);
            configureSeleniumGrid(Configuration.browser, remoteUrl);
        } else {
            log.info("Setting up local execution with browser: {}", Configuration.browser);
            // Important: Set remote to null for local execution
            Configuration.remote = null;

            // For local Chrome execution, set unique user data directory
            if (Configuration.browser.equalsIgnoreCase("chrome")) {
                configureChromeForLocalExecution();
            }
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

            // no cleanup needed
        } catch (Exception e) {
            log.error("Error in tearDown: {}", e.getMessage());
        } finally {
            try {
                if (getWebDriver() != null) {
                    Selenide.closeWebDriver();
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * Configures Chrome browser for local execution
     */
    private void configureChromeForLocalExecution() {
        // Simplified Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        if (Configuration.headless) {
            options.addArguments("--headless=new");
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        Configuration.browserCapabilities = capabilities;
    }

    /**
     * Configures Selenide to run tests on Selenium Grid
     */
    private void configureSeleniumGrid(String browser, String remoteUrl) {
        Configuration.remote = remoteUrl;

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

                    // Add Chrome-specific options to prevent session creation issues
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");

                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
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
