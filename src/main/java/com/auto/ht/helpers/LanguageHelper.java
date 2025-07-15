package com.auto.ht.helpers;

import com.auto.ht.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static java.lang.invoke.MethodHandles.lookup;

public class LanguageHelper {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());


    public static String getLanguage() {
        String languageCode = LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, "selenide.language", "en");
        try {
            Locale locale = Locale.forLanguageTag(languageCode.toLowerCase());
            String displayLanguage = locale.getDisplayLanguage(locale);

            if (!displayLanguage.isEmpty()) {
                return displayLanguage;
            }
        } catch (Exception e) {
            log.warn("Error getting display language for code: {}", languageCode, e);
        }

        log.warn("Unknown language code: {}. Defaulting to English.", languageCode);
        return "English";
    }

    public static String getLanguageCode() {
        String languageCode = LazyPropertiesHelper.getProperty(Constants.PROPERTIES_FILE, "selenide.language", "en");
        try {
            Locale locale = Locale.forLanguageTag(languageCode.toLowerCase());
            return locale.getLanguage();
        } catch (Exception e) {
            log.warn("Error getting language code for: {}", languageCode, e);
            return "en"; // Default to English if there's an error
        }
    }

    public static String getTestSuite() {
        return System.getProperty("test.suite", "vietjet").toLowerCase();
    }
}
