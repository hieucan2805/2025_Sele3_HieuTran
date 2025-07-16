package com.auto.ht.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PassengerModel {
    private String adults;
    private String child;
    private String baby;

    public PassengerModel(String passengerInfo) {
        String[] parts = passengerInfo.split(",");
        this.adults = parts[0].trim();
        this.child = parts.length > 1 ? parts[1].trim() : "0";
        this.baby = parts.length > 2 ? parts[2].trim() : "0";
    }

    @Override
    public String toString() {
        return "Passenger{adults=" + adults + ", child=" + child + ", baby=" + baby + "}";    }
}
