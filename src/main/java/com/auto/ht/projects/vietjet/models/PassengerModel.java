package com.auto.ht.projects.vietjet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PassengerModel {
    private int adults;
    private int child;
    private int baby;

    public PassengerModel() {
        this.adults = 0;
        this.child = 0;
        this.baby = 0;
    }

    public PassengerModel(String passengerInfo) {
        String[] parts = passengerInfo.split(",");
        this.adults = Integer.parseInt(parts[0].trim());
        this.child = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
        this.baby = parts.length > 2 ? Integer.parseInt(parts[2].trim()) : 0;
    }

    @Override
    public String toString() {
        return "Passenger{adults=" + adults + ", child=" + child + ", baby=" + baby + "}";
    }
}
