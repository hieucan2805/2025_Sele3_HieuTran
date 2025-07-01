package com.auto.ht.base;

import com.auto.ht.utils.Constants;
import com.auto.ht.helpers.LazyPropertiesHelper;
import com.codeborne.selenide.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;

@Slf4j
public class DriverFactory {

    public static void setupDriver() {
        setupDriver(false, null, null);
    }

    public static void setupDriver(String browser) {
        setupDriver(false, browser, null);
    }

    private static final String PROPERTIES_FILE = Constants.PROPERTIES_FILE;

    public static void setupDriver(boolean gridEnabled, String browser, String gridURL) {
        log.info("Load properties to Configuration ");
        // Load properties to Configuration
        String defaultBrowser = LazyPropertiesHelper.getProperty(PROPERTIES_FILE, "browser", "");
        Configuration.browser = browser != null && !browser.isEmpty() ? browser : defaultBrowser;
        Configuration.browserSize = LazyPropertiesHelper.getProperty(PROPERTIES_FILE, "browserSize", "1366x768");
        Configuration.timeout = Long.parseLong(LazyPropertiesHelper.getProperty(PROPERTIES_FILE, "timeout", "4000"));
        Configuration.headless = Boolean.parseBoolean(LazyPropertiesHelper.getProperty(PROPERTIES_FILE, "headless", "false"));
        Configuration.pageLoadStrategy = LazyPropertiesHelper.getProperty(PROPERTIES_FILE, "pageLoadStrategy", "normal");

        // Selenium Grid configuration
        if (gridEnabled && gridURL != null) {
            log.info("Setting up Grid execution with URL: {}", gridURL);
            Configuration.remote = gridURL;
            DesiredCapabilities capabilities = new DesiredCapabilities();

            if (browser == null) {
                throw new IllegalArgumentException("Browser must be specified either as a parameter or in the properties file.");
            }
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
        }

        log.info("Selenide Configuration: browser={}, browserSize={}, timeout={}, baseUrl={}, headless={}, pageLoadStrategy={}, remote={}",
                Configuration.browser,
                Configuration.browserSize,
                Configuration.timeout,
                Configuration.baseUrl,
                Configuration.headless,
                Configuration.pageLoadStrategy,
                Configuration.remote);
    }
}
