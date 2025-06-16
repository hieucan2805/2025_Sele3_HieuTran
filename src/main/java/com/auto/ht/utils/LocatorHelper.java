package com.auto.ht.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocatorHelper {
    private final ResourceBundle localBundle;

    private  final String language = URLHelper.getLanguage();

    // Initialize by loading the localization files


    public LocatorHelper(String page) {
        localBundle = ResourceBundle.getBundle((STR."localization.\{page.toLowerCase()}"), Locale.of(language));
    }

    /**
     * Retrieve a value from the localization file based on the language and key path
     *
     * @param key Key (e.g., "homePage.button.findFlight")
     * @return Localization value
     */
    public  String getLocalizedText(String key) {
        return localBundle.getString(key);
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

