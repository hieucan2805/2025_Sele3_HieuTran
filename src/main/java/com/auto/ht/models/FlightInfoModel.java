package com.auto.ht.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FlightInfoModel {

    private String type;
    private String from;
    private String to;
    private String departureDate;
    private String duration;
    private PassengerModel passenger;

}
