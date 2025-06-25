package com.auto.ht.utils;

import com.codeborne.selenide.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodHandles.lookup;

public class LanguageHelper {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());
    static PropertiesReader properties = new PropertiesReader("selenide.properties");

    public static String getLanguage() {
        return properties.getProperty("language", "en");
    }

//    public static String getLocalizedURL() {
//        String baseUrl = properties.getProperty("baseUrl","");
//        // Check and concat URL
//        if (!baseUrl.endsWith("/")) {
//            baseUrl += "/";
//        }
//
//        return baseUrl + getLanguage();
//    }


    public static String getTestSuite() {
        return System.getProperty("test.suite", "vietjet").toLowerCase();
    }


    public static String getBaseURL() {
        String testSuite = getTestSuite();
        String baseUrl = switch (testSuite) {
            case "vietjet" -> properties.getProperty("baseUrl.vj", "https://vietjetair.com");
            case "leapfrog" -> properties.getProperty("baseUrl.lf", "");
            default -> {
                log.warn("Unknown test suite: {}. Defaulting to Vietjet base URL.", testSuite);
                yield properties.getProperty("baseUrl.vj", "https://vietjetair.com");
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
