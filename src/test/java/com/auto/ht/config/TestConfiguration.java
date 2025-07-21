package com.auto.ht.config;

import com.codeborne.selenide.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Manages test configuration and settings
 */
public class TestConfiguration {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());
    private static final String DEFAULT_PROPERTIES_FILE = "src/test/resources/selenide.properties";
    private static final Properties properties = new Properties();
    
    /**
     * Initializes the test configuration from properties files
     */
    public static void initializeConfiguration() {
        loadProperties();
        configureSelenide();
    }
    
    /**
     * Loads properties from the properties file
     */
    private static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(DEFAULT_PROPERTIES_FILE)) {
            properties.load(fis);
            log.info("Loaded properties from {}", DEFAULT_PROPERTIES_FILE);
        } catch (IOException e) {
            log.warn("Could not load properties from {}: {}", DEFAULT_PROPERTIES_FILE, e.getMessage());
        }
        
        // Load system properties (overrides file properties)
        properties.putAll(System.getProperties());
    }
    
    /**
     * Configures Selenide based on loaded properties
     */
    private static void configureSelenide() {
        // Browser configuration
        Configuration.browser = getProperty("selenide.browser", "chrome");
        Configuration.browserSize = getProperty("selenide.browserSize", "1920x1080");
        Configuration.timeout = Long.parseLong(getProperty("selenide.timeout", "5000"));
        Configuration.pageLoadStrategy = getProperty("selenide.pageLoadStrategy", "eager");
        Configuration.baseUrl = getProperty("selenide.baseUrl", "http://localhost:8080");
        
        // Remote configuration (if specified)
        String remoteUrl = getProperty("selenide.remote", "");
        if (!remoteUrl.isEmpty()) {
            Configuration.remote = remoteUrl;
        }
        
        // Other configurations
        Configuration.headless = Boolean.parseBoolean(getProperty("selenide.headless", "false"));
        Configuration.fastSetValue = Boolean.parseBoolean(getProperty("selenide.fastSetValue", "true"));
        Configuration.screenshots = Boolean.parseBoolean(getProperty("selenide.screenshots", "true"));
        Configuration.savePageSource = Boolean.parseBoolean(getProperty("selenide.savePageSource", "true"));
        Configuration.reportsFolder = getProperty("selenide.reportsFolder", "build/reports/tests");
        
        // Project specific URL based on project name if specified
        String projectName = getProperty("project.name", "");
        if (!projectName.isEmpty()) {
            String projectSpecificUrl = getProperty("selenide.url." + projectName, "");
            if (!projectSpecificUrl.isEmpty()) {
                Configuration.baseUrl = projectSpecificUrl;
            }
        }
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
