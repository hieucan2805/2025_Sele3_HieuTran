package com.auto.ht.projects;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.invoke.MethodHandles.lookup;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    @BeforeClass
    public static void BeforeClass() {
        // Load properties from selenide.properties
        try (InputStream input = BaseTest.class.getClassLoader().getResourceAsStream("selenide.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
                log.info("Loaded properties from selenide.properties");
            } else {
                log.warn("selenide.properties not found in resources");
            }
        } catch (IOException ex) {
            log.error("Error loading selenide.properties", ex);
        }

        log.info("Starting tests in {}", BaseTest.class.getName());

        // Enable Allure reports
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }
}
