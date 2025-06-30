package com.auto.ht.utils;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LocatorHelper {
    private final ResourceBundle localBundle;

    protected final String language = LanguageHelper.getLanguage();

    // Initialize by loading the localization files with UTF-8 support
    public LocatorHelper(String page) {
        String bundleName = "localization/" + page.toLowerCase();
        Locale locale = Locale.forLanguageTag(language);
        ResourceBundle bundle;
        try {
            String resourcePath = bundleName + "_" + locale.getLanguage() + ".properties";
            java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (stream != null) {
                try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    bundle = new PropertyResourceBundle(reader);
                }
            } else {
                // fallback to default ResourceBundle if file not found
                bundle = ResourceBundle.getBundle(bundleName, locale);
            }
        } catch (IOException e) {
            // fallback to default ResourceBundle if error
            bundle = ResourceBundle.getBundle(bundleName, locale);
        }
        this.localBundle = bundle;
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
