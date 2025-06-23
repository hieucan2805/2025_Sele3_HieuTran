package com.auto.ht.base;

import com.auto.ht.utils.LanguageHelper;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.PropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;

@Slf4j
public class DriverFactory {

    public static void setupDriver() {
        setupDriver(false, null, null);
    }

    public static void setupDriver(boolean gridEnabled, String browser, String gridURL) {
        PropertiesReader properties = new PropertiesReader("selenide.properties");


        log.info("Load properties to Configuration ");
        // Load properties to Configuration
        Configuration.browser = browser != null ? browser : properties.getProperty("browser", "");
        Configuration.browserSize = properties.getProperty("browserSize", "1920x1080");
        Configuration.timeout = Long.parseLong(properties.getProperty("timeout", "10000"));
        Configuration.baseUrl = properties.getProperty("baseUrl.vj" + LanguageHelper.getLanguage(), "");
        Configuration.headless = Boolean.parseBoolean(properties.getProperty("headless", "false"));
        Configuration.pageLoadStrategy = properties.getProperty("pageLoadStrategy", "normal");

        // Selenium Grid configuration
        if (gridEnabled && gridURL != null) {
            log.info("Setting up Grid execution with URL: {}", gridURL);
            Configuration.remote = gridURL;
            DesiredCapabilities capabilities = new DesiredCapabilities();

            if (browser.equalsIgnoreCase("chrome")) {
                capabilities.setBrowserName("chrome");
                capabilities.setCapability("enableVNC", true);
                capabilities.setCapability("enableVideo", false);
                Configuration.browserCapabilities = capabilities;
            } else if (browser.equalsIgnoreCase("edge")) {
                capabilities.setBrowserName("MicrosoftEdge");
                capabilities.setCapability("enableVNC", true);
                capabilities.setCapability("enableVideo", false);
                Configuration.browserCapabilities = capabilities;
            }
        }
    }
}
