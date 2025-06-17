package com.auto.ht.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.invoke.MethodHandles.lookup;

public class PropertyLoader {
    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public static void loadProperties(String fileName) {
        try (InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
                log.info("Loaded properties from {}", fileName);
            } else {
                log.warn("{} not found in resources", fileName);
            }
        } catch (IOException ex) {
            log.error("Error loading {}", fileName, ex);
        }
    }
}

