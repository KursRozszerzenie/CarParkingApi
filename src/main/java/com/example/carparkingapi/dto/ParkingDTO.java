package com.example.carparkingapi.dto;

import com.example.carparkingapi.model.ParkingType;
import lombok.Data;

@Data
public class ParkingDTO {

    private String name;

    private String adress;

    private int capacity;

    private ParkingType parkingType;

    private int parkingSpotWidth;

    private int parkingSpotLength;

    private int placesForElectricCars;

    private int takenElectricPlaces;

    private int takenPlaces;
}
