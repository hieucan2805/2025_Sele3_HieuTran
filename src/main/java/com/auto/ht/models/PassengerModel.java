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

    @Override
    public String toString() {
        return "Passenger{adults=" + adults + ", child=" + child + ", baby=" + baby + "}";    }
}
