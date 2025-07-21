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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.getUserAgent;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;
import static java.lang.invoke.MethodHandles.lookup;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());
    private String tempUserDataDir; // To store user data dir path for cleanup

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

            // Clean up any temporary user data directory
            cleanupTempUserDataDir();

        } catch (Exception e) {
            log.error("Error in tearDown: {}", e.getMessage());
        } finally {
            // Close the browser after each test
            Selenide.closeWebDriver();
        }
    }

    /**
     * Cleans up any temporary Chrome user data directory that was created
     */
    private void cleanupTempUserDataDir() {
        if (tempUserDataDir != null && !tempUserDataDir.isEmpty()) {
            try {
                Path userDataDirPath = Paths.get(tempUserDataDir);
                if (Files.exists(userDataDirPath)) {
                    log.info("Cleaning up temporary Chrome user data directory: {}", tempUserDataDir);
                    // Recursively delete the directory and all its contents using try-with-resources
                    try (Stream<Path> pathStream = Files.walk(userDataDirPath)) {
                        pathStream.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(file -> {
                                boolean deleted = file.delete();
                                if (!deleted) {
                                    log.warn("Failed to delete file: {}", file.getAbsolutePath());
                                }
                            });
                    }
                    log.info("Successfully cleaned up Chrome user data directory");
                }
            } catch (IOException e) {
                log.error("Failed to clean up Chrome user data directory: {}", e.getMessage());
                log.debug("Full stack trace for Chrome user data directory cleanup failure", e);
            }
        }
    }

    /**
     * Configures Chrome browser for local execution with unique user data directory
     */
    private void configureChromeForLocalExecution() {
        // Create a unique user data directory for this test run
        String uniqueId = UUID.randomUUID().toString();
        tempUserDataDir = Paths.get(System.getProperty("java.io.tmpdir"), "chrome_profile_" + uniqueId).toString();

        log.info("Setting up Chrome with unique user data directory: {}", tempUserDataDir);

        // Create Chrome options with unique user data directory
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=" + tempUserDataDir);

        // Add other useful Chrome options for CI environments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");

        // Set the browser capabilities with our options
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        Configuration.browserCapabilities = capabilities;
    }

    /**
     * Configures Selenide to run tests on Selenium Grid with browser-specific capabilities
     *
     * @param browser the browser to use (chrome, edge)
     * @param remoteUrl the Selenium Grid URL to connect to
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

                    // Generate a unique user data directory for remote Chrome sessions
                    String uniqueId = UUID.randomUUID().toString();
                    chromeOptions.addArguments("--user-data-dir=/tmp/chrome_profile_" + uniqueId);

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

