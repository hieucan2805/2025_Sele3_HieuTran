package com.auto.ht.vietjet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Airport {

    SGN("SGN"),
    HAN("HAN"),
    DAD("DAD");

    private final String code;


    /**
     * Get Airport enum by IATA code.
     * @param code IATA code (e.g., SGN, HAN, DAD)
     * @return Airport enum if found; otherwise throws IllegalArgumentException
     */
    public static Airport fromCode(String code) {
        for (Airport airport : Airport.values()) {
            if (airport.getCode().equalsIgnoreCase(code)) {
                return airport;
            }
        }
        throw new IllegalArgumentException("No airport found with code: " + code);
    }

    /**
     * Find airport code by Vietnamese name, English name, or airport name.
     * @param name The name to search for
     * @return The corresponding airport code if found
     */
    public static String findByName(String name) {
        String normalizedInput = name.trim().toLowerCase();
        for (Airport airport : Airport.values()) {
            if (airport.getVietnameseName().equalsIgnoreCase(normalizedInput)
                    || airport.getEnglishName().equalsIgnoreCase(normalizedInput)
                    || airport.getAirportName().equalsIgnoreCase(normalizedInput)) {
                return airport.getCode();
            }
        }
        throw new IllegalArgumentException("No airport code found for name: " + name);
    }

    /**
     * Get the airport name based on the IATA code.
     * @param code IATA code (e.g., SGN, HAN, DAD)
     * @return The airport name if found; otherwise throws IllegalArgumentException
     */
    public static String getAirportNameByCode(String code) {
        for (Airport airport : Airport.values()) {
            if (airport.getCode().equalsIgnoreCase(code)) {
                return airport.getAirportName();
            }
        }
        throw new IllegalArgumentException("No airport found with code: " + code);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) [%s]", code, englishName, vietnameseName, airportName);
    }
}
