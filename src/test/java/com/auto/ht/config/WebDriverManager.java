package com.auto.ht.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * Manages WebDriver configuration and browser setup for tests
 */
public class WebDriverManager {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());
    private String tempUserDataDir; // To store user data dir path for cleanup
    
    /**
     * Configures the WebDriver based on properties
     * 
     * @return The path to any temporary directory created, for cleanup
     */
    public String configureWebDriver() {
        // Use Selenide's Configuration.remote directly
        String remoteUrl = Configuration.remote;

        // Configure Selenide to use Selenium Grid if remoteUrl is provided
        if (remoteUrl != null && !remoteUrl.isEmpty() && !remoteUrl.equalsIgnoreCase("false")) {
            log.info("Setting up Grid execution with browser: {}, remoteUrl: {}", Configuration.browser, remoteUrl);
//            configureSeleniumGrid(Configuration.browser, remoteUrl);
        } else {
            log.info("Setting up local execution with browser: {}", Configuration.browser);
            // Important: Set remote to null for local execution
            Configuration.remote = null;

            // Configure browser options using the general method
            configureBrowserForLocalExecution(Configuration.browser.toLowerCase());
        }

        return tempUserDataDir;
    }

    /**
     * General method to configure any browser for local execution
     *
     * @param browserName The name of the browser (chrome, edge, firefox, etc.)
     */
    private void configureBrowserForLocalExecution(String browserName) {
        // Create a unique user data directory for this test run
        createTempUserDataDir(browserName);

        // Get browser-specific options
        MutableCapabilities options = createBrowserOptions(browserName);

        // Set the browser capabilities with our options
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // Add options to capabilities based on browser type
        switch (browserName) {
            case "chrome":
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                break;
            case "edge":
                capabilities.setCapability("ms:edgeOptions", options);
                break;
            case "firefox":
                capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
                break;
            default:
                log.warn("No specific capability mapping for browser: {}", browserName);
                // Try a generic approach for unknown browsers
                capabilities.merge(options);
        }
        
        Configuration.browserCapabilities = capabilities;
    }

    /**
     * Creates browser-specific options with appropriate settings
     *
     * @param browserName The name of the browser
     * @return The configured browser options
     */
    private MutableCapabilities createBrowserOptions(String browserName) {
        boolean isCI = System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null;

        return switch (browserName) {
            case "chrome" -> createChromeOptions(isCI);
            case "edge" -> createEdgeOptions(isCI);
            case "firefox" -> createFirefoxOptions(isCI);
            default -> {
                log.warn("Using default options for unsupported browser: {}", browserName);
                yield new DesiredCapabilities();
            }
        };
    }

    /**
     * Creates Chrome-specific options
     */
    private ChromeOptions createChromeOptions(boolean isCI) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=" + tempUserDataDir);
        
        // Common options for all environments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // CI-specific options
        if (isCI) {
            options.addArguments("--incognito");
            options.addArguments("--disable-application-cache");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-plugins");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-infobars");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--no-first-run");
            options.addArguments("--headless=new");
        }

        return options;
    }

    /**
     * Creates Edge-specific options
     */
    private EdgeOptions createEdgeOptions(boolean isCI) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--user-data-dir=" + tempUserDataDir);

        // Common options for all environments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // CI-specific options
        if (isCI) {
            options.addArguments("--inprivate");
            options.addArguments("--disable-application-cache");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-infobars");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--no-first-run");
            options.addArguments("--headless=new");
        }
        
        return options;
    }

    /**
     * Creates Firefox-specific options
     */
    private FirefoxOptions createFirefoxOptions(boolean isCI) {
        FirefoxOptions options = new FirefoxOptions();
        FirefoxProfile profile = new FirefoxProfile();

        // Configure Firefox profile preferences
        profile.setPreference("browser.cache.disk.enable", false);
        profile.setPreference("browser.cache.memory.enable", false);
        profile.setPreference("browser.cache.offline.enable", false);
        profile.setPreference("network.http.use-cache", false);
        profile.setPreference("browser.privatebrowsing.autostart", true);

        options.setProfile(profile);

        // Firefox doesn't use user-data-dir argument like Chrome/Edge
        // Instead we set a custom profile which is already created in tempUserDataDir

        // CI-specific options
        if (isCI) {
            options.addArguments("-headless");
        }

        return options;
    }

    /**
     * Creates a unique temporary directory for browser user data
     *
     * @param browserName The name of the browser (chrome, edge, firefox)
     */
    private void createTempUserDataDir(String browserName) {
        String uniqueId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis());

        // In CI environments, ensure we use a completely unique path
        boolean isCI = System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null;

        // Always create a unique directory path, even for CI
        if (isCI) {
            // For CI, use a path in the workspace that's guaranteed to be unique and writable
            tempUserDataDir = Paths.get(System.getProperty("user.dir"), browserName + "_profile_" + uniqueId + "_" + timestamp).toString();
            log.info("CI environment detected, using workspace {} profile path: {}", browserName, tempUserDataDir);
        } else {
            // For local execution, use the temp directory
            tempUserDataDir = Paths.get(System.getProperty("java.io.tmpdir"), browserName + "_profile_" + uniqueId + "_" + timestamp).toString();
        }

        log.info("Setting up {} with unique profile directory: {}", browserName, tempUserDataDir);

        // Create the directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(tempUserDataDir));
            log.info("Successfully created {} user data directory: {}", browserName, tempUserDataDir);
        } catch (IOException e) {
            log.error("Failed to create user data directory for {}: {}", browserName, e.getMessage());
        }
    }

//    /**
//     * Configures Selenide to run tests on Selenium Grid with browser-specific capabilities
//     *
//     * @param browser the browser to use (chrome, edge)
//     * @param remoteUrl the Selenium Grid URL to connect to
//     */
//    private void configureSeleniumGrid(String browser, String remoteUrl) {
//        // Implementation for configuring Selenium Grid
//    }
}
