package com.auto.ht.projects.vietjet.enums;

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

    @Override
    public String toString() {
        return String.format("%s", code);
    }
}
