package com.auto.ht.vietjet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Airport {

    SGN("SGN", "Thành phố Hồ Chí Minh", "Ho Chi Minh city", "Tân Sơn Nhất"),
    HAN("HAN", "Hà Nội", "Hanoi", "Nội Bài"),
    DAD("DAD", "Đà Nẵng", "Danang", "Danang Airport");

    private final String code;
    private final String vietnameseName;
    private final String englishName;
    private final String airportName;

    /**
     * Get Airport enum by IATA code.
     * @param code IATA code (e.g., SGN, HAN, DAD)
     * @return Airport enum if found; otherwise throws IllegalArgumentException
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

    @Override
    public String toString() {
        return String.format("%s - %s (%s) [%s]", code, englishName, vietnameseName, airportName);
    }
}
