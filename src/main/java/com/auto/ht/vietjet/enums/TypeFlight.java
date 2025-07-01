package com.auto.ht.vietjet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeFlight {

    ROUND_TRIP("radio.roundTrip","Round Trip"),
    ONE_WAY("radio.oneWay", "One Way");
    private final String xPathKey;
    private final String name;

    public static TypeFlight fromName(String name) {
        for (TypeFlight type : TypeFlight.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant found for name: " + name);
    }

}
