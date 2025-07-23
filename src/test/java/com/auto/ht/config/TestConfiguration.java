package com.auto.ht.config;

import com.codeborne.selenide.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Manages test configuration and settings
 */
public class TestConfiguration {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());
    private static final String DEFAULT_PROPERTIES_FILE = "src/test/resources/selenide.properties";
    private static final Properties properties = new Properties();
    private static final WebDriverManager webDriverManager = new WebDriverManager();

    /**
     * Initializes the test configuration from properties files
     *
     * @param testClassName The name of the test class
     * @param testMethodName The name of the test method
     */
    public static void initializeConfiguration(String testClassName, String testMethodName) {
        // Load properties and configure Selenide
        loadProperties();
        handleProjectSpecificUrl();
        logConfiguration();

        // Configure WebDriver based on settings
        webDriverManager.configureWebDriver();
    }
    
    /**
     * Loads properties from the properties file and sets them as system properties for Selenide to use
     */
    private static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(DEFAULT_PROPERTIES_FILE)) {
            properties.load(fis);
            log.info("Loaded properties from {}", DEFAULT_PROPERTIES_FILE);

            // Set all selenide.* properties as system properties for Selenide to pick up
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                // Only set system property if it's not already set (to respect command-line overrides)
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            log.warn("Could not load properties from {}: {}", DEFAULT_PROPERTIES_FILE, e.getMessage());
        }
    }
    
    /**
     * Handles project-specific URL configuration based on project name
     * This is custom functionality not built into Selenide
     */
    private static void handleProjectSpecificUrl() {
        String projectName = getProperty("project.name", "");
        if (!projectName.isEmpty()) {
            String projectSpecificUrl = getProperty("selenide.url." + projectName, "");
            if (!projectSpecificUrl.isEmpty()) {
                // Set both the system property and Configuration property
                System.setProperty("selenide.baseUrl", projectSpecificUrl);
                Configuration.baseUrl = projectSpecificUrl;
                log.info("Set baseUrl to {} for project {}", projectSpecificUrl, projectName);
            }
        }
    }

    /**
     * Logs the key configuration values for debugging
     */
    private static void logConfiguration() {
        log.info("Selenide configuration: browser={}, baseUrl={}, remote={}, headless={}",
                Configuration.browser,
                Configuration.baseUrl,
                Configuration.remote,
                Configuration.headless);
    }

    /**
     * Gets a property value with a default fallback
     *
     * @param key the property key
     * @param defaultValue the default value if not found
     * @return the property value or default
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
