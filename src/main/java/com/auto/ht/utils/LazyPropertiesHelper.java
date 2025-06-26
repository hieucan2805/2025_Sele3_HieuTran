package com.auto.ht.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, lazy-loading properties helper for reusability across the project.
 */
public class LazyPropertiesHelper {
    private static final ConcurrentHashMap<String, Properties> cache = new ConcurrentHashMap<>();

    /**
     * Loads and caches properties from the given file name (classpath resource).
     * @param fileName the properties file name
     * @return the loaded Properties object
     */
    public static Properties getProperties(String fileName) {
        return cache.computeIfAbsent(fileName, LazyPropertiesHelper::loadProperties);
    }

    private static Properties loadProperties(String fileName) {
        Properties props = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                props.load(is);
            } else {
                throw new RuntimeException("Properties file not found: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file: " + fileName, e);
        }
        return props;
    }

    /**
     * Gets a property value from the specified file, or returns the default if not found.
     * @param fileName the properties file name
     * @param key the property key
     * @param defaultValue the default value
     * @return the property value or defaultValue
     */
    public static String getProperty(String fileName, String key, String defaultValue) {
        return getProperties(fileName).getProperty(key, defaultValue);
    }
}

