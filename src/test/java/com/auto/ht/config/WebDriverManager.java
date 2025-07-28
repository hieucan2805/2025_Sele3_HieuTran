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

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Manages WebDriver configuration and browser setup for tests
 */
public class WebDriverManager {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    /**
     * Configures the WebDriver based on properties
     */
    public void configureWebDriver() {
        // Use Selenide's Configuration.remote directly
        String remoteUrl = Configuration.remote;

        // Check if the specified browser is available
        if (!isBrowserAvailable(Configuration.browser.toLowerCase())) {
            log.warn("Browser '{}' is not available on this system. Falling back to Chrome.", Configuration.browser);
            Configuration.browser = "chrome";
        }

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
    }

    /**
     * Checks if the specified browser is available on the system
     * @param browserName Browser name to check
     * @return true if the browser is available, false otherwise
     */
    private boolean isBrowserAvailable(String browserName) {
        // Get the operating system
        String os = System.getProperty("os.name").toLowerCase();
        String binaryPath = null;

        if (os.contains("linux")) {
            // Linux paths
            binaryPath = switch (browserName) {
                case "chrome" -> "/usr/bin/google-chrome";
                case "firefox" -> "/usr/bin/firefox";
                case "edge" -> "/usr/bin/msedge";
                default -> null;
            };
        } else if (os.contains("windows")) {
            // Windows paths - typically in Program Files
            binaryPath = switch (browserName) {
                case "chrome" -> "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
                case "firefox" -> "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
                case "edge" -> "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe";
                default -> null;
            };
        } else if (os.contains("mac") || os.contains("darwin")) {
            // macOS paths
            binaryPath = switch (browserName) {
                case "chrome" -> "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
                case "firefox" -> "/Applications/Firefox.app/Contents/MacOS/firefox";
                case "edge" -> "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge";
                default -> null;
            };
        }

        // If no path found or browser not supported
        if (binaryPath == null) {
            log.warn("No known path for browser '{}' on {} OS", browserName, os);
            return false;
        }

        // Check if the binary exists
        boolean available = new java.io.File(binaryPath).exists();
        if (!available) {
            log.warn("Browser binary '{}' not found at expected path: {}", browserName, binaryPath);
        }

        return available;
    }

    /**
     * General method to configure any browser for local execution
     *
     * @param browserName The name of the browser (chrome, edge, firefox, etc.)
     */
    private void configureBrowserForLocalExecution(String browserName) {
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

        // Set Firefox binary path based on OS
        String os = System.getProperty("os.name").toLowerCase();
        String firefoxPath = null;

        if (os.contains("linux")) {
            firefoxPath = "/usr/bin/firefox";
        } else if (os.contains("windows")) {
            firefoxPath = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
        } else if (os.contains("mac") || os.contains("darwin")) {
            firefoxPath = "/Applications/Firefox.app/Contents/MacOS/firefox";
        }

        // Only set binary if the path exists
        if (firefoxPath != null && new java.io.File(firefoxPath).exists()) {
            options.setBinary(firefoxPath);
            log.info("Configured Firefox with binary path: {}", firefoxPath);
        } else {
            log.info("Using system default Firefox binary");
        }

        // Configure Firefox profile preferences
        profile.setPreference("browser.cache.disk.enable", false);
        profile.setPreference("browser.cache.memory.enable", false);
        profile.setPreference("browser.cache.offline.enable", false);
        profile.setPreference("network.http.use-cache", false);
        profile.setPreference("browser.privatebrowsing.autostart", true);

        options.setProfile(profile);

        // CI-specific options
        if (isCI) {
            options.addArguments("-headless");
        }

        return options;
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
