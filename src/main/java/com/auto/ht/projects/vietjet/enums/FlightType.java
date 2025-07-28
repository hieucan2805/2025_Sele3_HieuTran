package com.auto.ht.projects.vietjet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlightType {

    RETURN("return"),
    ONE_WAY( "oneway");
    private final String name;

    public static FlightType fromName(String name) {
        for (FlightType type : FlightType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant found for name: " + name);
    }

}

