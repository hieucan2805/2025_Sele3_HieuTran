package com.auto.ht.utils;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class FileUtils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static void savePropertiesToFile(Properties properties, String filePath) {
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            log.error("Cannot save to file", e);
        }
    }

    public static String getCurrentDir() {
        return System.getProperty("user.dir") + File.separator;
    }
}
