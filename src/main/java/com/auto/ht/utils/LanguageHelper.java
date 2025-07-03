package com.auto.ht.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodHandles.lookup;

public class LanguageHelper {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());


    public static String getLanguage() {
        return LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, "selenide.language", "en");
    }

    public static String getTestSuite() {
        return System.getProperty("test.suite", "vietjet").toLowerCase();
    }

    public static String getBaseURL() {
        String testSuite = getTestSuite();
        String baseUrl = switch (testSuite) {
            case "vietjet" -> LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, "selenide.baseUrl.VjAir", "https://vietjetair.com");
            case "leapfrog" -> LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, "selenide.baseUrl.lf", "");
            default -> {
                log.warn("Unknown test suite: {}. Defaulting to Vietjet base URL.", testSuite);
                yield LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, "selenide.baseUrl.VjAir", "https://vietjetair.com");
            }
        };

        log.info("Using base URL: {}", baseUrl);
        return baseUrl;
    }

    public static String getLocalizedURL() {
        String testSuite = getTestSuite();
        String baseUrl = getBaseURL();

        // Only append language for Vietjet
        if ("vietjet".equals(testSuite)) {
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            return baseUrl + getLanguage();
        }

        return baseUrl;
    }

}
