package com.auto.ht.config;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

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
        
        return tempUserDataDir;
    }

    /**
     * Configures Chrome browser for local execution with unique user data directory
     */
    private void configureChromeForLocalExecution() {
        // Create a unique user data directory for this test run
        String uniqueId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis());

        // In CI environments, ensure we use a completely unique path
        boolean isCI = System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null;

        // Always create a unique directory path, even for CI
        if (isCI) {
            // For CI, use a path in the workspace that's guaranteed to be unique and writable
            tempUserDataDir = Paths.get(System.getProperty("user.dir"), "chrome_profile_" + uniqueId + "_" + timestamp).toString();
            log.info("CI environment detected, using workspace chrome profile path: {}", tempUserDataDir);
        } else {
            // For local execution, use the temp directory
            tempUserDataDir = Paths.get(System.getProperty("java.io.tmpdir"), "chrome_profile_" + uniqueId + "_" + timestamp).toString();
        }

        log.info("Setting up Chrome with unique user data directory: {}", tempUserDataDir);

        // Create the directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(tempUserDataDir));
            log.info("Successfully created Chrome user data directory: {}", tempUserDataDir);
        } catch (IOException e) {
            log.error("Failed to create user data directory: {}", e.getMessage());
        }

        // Create Chrome options with unique user data directory
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=" + tempUserDataDir);
        
        // CI detection
        isCI = System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null;
        if (isCI) {
            // CI flags
            options.addArguments("--incognito");
            options.addArguments("--disable-application-cache");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-plugins");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-infobars");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--no-first-run");
        }
        // Add other useful Chrome options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        // Enforce headless in CI
        if (isCI) {
            options.addArguments("--headless=new");
        }

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
        
        // Add any grid-specific configuration if needed
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }
        
        Configuration.browserCapabilities = capabilities;
    }
    
    /**
     * Cleans up any temporary Chrome user data directory that was created
     * 
     * @param tempUserDataDir The path to the user data directory to clean
     */
    public void cleanupTempUserDataDir(String tempUserDataDir) {
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
}
