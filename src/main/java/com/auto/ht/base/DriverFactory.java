package com.auto.ht.base;

import com.auto.ht.utils.LanguageHelper;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.PropertiesReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DriverFactory {

    public static void setupDriver() {
        PropertiesReader properties = new PropertiesReader("selenide.properties");


        log.info("Load properties to Configuration ");
        // Load properties to Configuration
        Configuration.browser = properties.getProperty("browser", "");
        Configuration.browserSize = properties.getProperty("browserSize", "1920x1080");
        Configuration.timeout = Long.parseLong(properties.getProperty("timeout", "10000"));
        Configuration.baseUrl = properties.getProperty("baseUrl.vj" + LanguageHelper.getLanguage(), "");
        Configuration.headless = Boolean.parseBoolean(properties.getProperty("headless", "false"));
        Configuration.pageLoadStrategy = properties.getProperty("pageLoadStrategy", "normal");
    }

}
