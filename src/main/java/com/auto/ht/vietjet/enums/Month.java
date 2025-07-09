package com.auto.ht.vietjet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Month {
    JAN("Tháng 01", "January"),
    FEB("Tháng 02", "February"),
    MAR("Tháng 03", "March"),
    APR("Tháng 04", "April"),
    MAY("Tháng 05", "May"),
    JUN("Tháng 06", "June"),
    JUL("Tháng 07", "July"),
    AUG("Tháng 08", "August"),
    SEP("Tháng 09", "September"),
    OCT("Tháng 10", "October"),
    NOV("Tháng 11", "November"),
    DEC("Tháng 12", "December");

    private final String viName;
    private final String enName;


    public String getVietnameseName() {
        return viName;
    }

    public String getEnglishName() {
        return enName;
    }

    public static Month fromShortName(String shortName) {
        for (Month month : values()) {
            if (month.name().equalsIgnoreCase(shortName)) {
                return month;
            }
        }
        throw new IllegalArgumentException("No enum constant for short name: " + shortName);
    }
}
