package com.auto.ht.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVDataProvider {
    private static final Logger log = LoggerFactory.getLogger(CSVDataProvider.class);

    /**
     * Read all data from a CSV file
     * @param filePath Path to the CSV file
     * @return List of maps, each map represents a row with column name as key
     */
    public static List<Map<String, String>> readCSVData(String filePath) {
        List<Map<String, String>> csvData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read header row
            String headerLine = br.readLine();
            if (headerLine == null) {
                log.error("CSV file is empty: {}", filePath);
                return csvData;
            }

            // Split header into column names
            String[] headers = headerLine.split(",");
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim();
            }

            // Read data rows
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0) {
                    Map<String, String> rowData = new HashMap<>();
                    boolean hasData = false;

                    for (int i = 0; i < headers.length; i++) {
                        String value = (i < values.length) ? values[i].trim() : "";
                        rowData.put(headers[i], value);
                        if (!value.isEmpty()) {
                            hasData = true;
                        }
                    }

                    if (hasData) {
                        csvData.add(rowData);
                    }
                }
            }

        } catch (IOException e) {
            log.error("Error reading CSV file: {}", filePath, e);
        }

        return csvData;
    }

    /**
     * Process passenger string format like "2 adults, 1 child, 0 baby"
     * Removes all alphabetic characters and spaces
     * @param input The passenger string
     * @return An array of numbers [adults, child, baby]
     */
    public static String[] parsePassengerString(String input) {
        if (input == null || input.isEmpty()) {
            return new String[]{"0", "0", "0"};
        }

        // Split by comma
        String[] parts = input.split(",");
        String[] result = new String[3];
        
        // Default values
        result[0] = "0"; // adults
        result[1] = "0"; // child
        result[2] = "0"; // baby

        // Extract numbers from each part
        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            result[i] = parts[i].replaceAll("[^0-9]", "").trim();
            if (result[i].isEmpty()) {
                result[i] = "0";
            }
        }

        return result;
    }
}
