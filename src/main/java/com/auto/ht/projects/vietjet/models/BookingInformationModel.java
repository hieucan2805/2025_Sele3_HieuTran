package com.auto.ht.projects.vietjet.models;

import java.time.LocalDate;

import com.auto.ht.projects.vietjet.enums.FlightType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookingInformationModel {

    private FlightType type;
    private String from;
    private String to;
    private LocalDate departureDate;
    private String duration;
    private String range;
    private PassengerModel passenger;

    public BookingInformationModel() {
        this.type = FlightType.RETURN; // Default type
        this.from = "";
        this.to = "";
        this.departureDate = null;
        this.duration = "";
        this.range = "";
        this.passenger = new PassengerModel();
    }
}
