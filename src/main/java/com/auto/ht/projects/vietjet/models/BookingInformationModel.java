package com.auto.ht.projects.vietjet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookingInformationModel {

    private String type;
    private String from;
    private String to;
    private String departureDate;
    private String duration;
    private String range;
    private PassengerModel passenger;

    public BookingInformationModel() {
        this.type = "RETURN"; // Default type
        this.from = "";
        this.to = "";
        this.departureDate = "";
        this.duration = "";
        this.range = "";
        this.passenger = new PassengerModel();
    }
}
