package com.auto.ht.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocatorHelper {
    private final ResourceBundle localBundle;

    protected final String language = URLHelper.getLanguage();

    // Initialize by loading the localization files
    public LocatorHelper(String page) {
        localBundle = ResourceBundle.getBundle("localization." + page.toLowerCase(), Locale.forLanguageTag(language));
    }

    /**
     * Retrieve a value from the localization file based on the language and key path
     *
     * @param key Key (e.g., "homePage.button.findFlight")
     * @return Localization value
     */
    public  String getLocalizedText(String key) {
        try {
            return localBundle.getString(key);
        } catch (java.util.MissingResourceException e) {
            // If the key is not found, return a placeholder or an error message
            return "!" + key + "!";
        }
    }

    /**
     * Update a locator with text from the properties file
     *
     * @param locator Locator template (e.g., "//*[text()='%s']")
     * @param key     Key (e.g., "button.findFlight")
     * @return Updated locator
     */
    public String updateLocatorWithDynamicText(String locator, String key) {
        String localizedText = getLocalizedText(key);
        return String.format(locator, localizedText);
    }

}
