package com.auto.ht.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Browser configuration helper for Selenide tests
 */
public class BrowserConfig {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    /**
     * Configures Chrome browser for local execution
     */
    public static void configureChrome() {
        log.info("Configuring Chrome browser for local execution");
        
        // Setup Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        
        // Add headless mode if configured
        if (Configuration.headless) {
            options.addArguments("--headless=new");
        }

        // Set browser capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        Configuration.browserCapabilities = capabilities;
    }

    /**
     * Configures Selenide to run tests on Selenium Grid
     * 
     * @param browser the browser to use (chrome, edge)
     * @param remoteUrl the Selenium Grid URL to connect to
     */
    public static void configureGrid(String browser, String remoteUrl) {
        log.info("Setting up Grid execution with browser: {}, remoteUrl: {}", browser, remoteUrl);
        Configuration.remote = remoteUrl;

        // Set up browser-specific capabilities
        if (browser != null) {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            switch (browser.toLowerCase()) {
                case "edge":
                    capabilities.setBrowserName("Microsoft Edge");
                    capabilities.setCapability("enableVNC", true);
                    capabilities.setCapability("enableVideo", false);
                    break;
                case "chrome":
                    capabilities.setBrowserName("Google Chrome");
                    capabilities.setCapability("enableVNC", true);
                    capabilities.setCapability("enableVideo", false);

                    // Add Chrome-specific options for Grid
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                    break;
                default:
                    log.warn("Unsupported browser: {}", browser);
                    break;
            }
            
            Configuration.browserCapabilities = capabilities;
        } else {
            log.warn("Browser must be specified when using grid");
        }
    }
}
