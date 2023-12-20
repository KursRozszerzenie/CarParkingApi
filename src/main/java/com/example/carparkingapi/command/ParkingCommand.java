package com.example.carparkingapi.command;

import com.example.carparkingapi.model.ParkingType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public class ParkingCommand {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Adress cannot be blank")
    private String adress;

    @Positive(message = "capacity must be positive, cannot be null or blank")
    private int capacity;

    @NotNull(message = "Parking type cannot be null")
    private ParkingType parkingType;

    @Positive(message = "parking spot width must be positive, cannot be null or blank")
    private int parkingSpotWidth;

    @Positive(message = "parking spot length must be positive, cannot be null or blank")
    private int parkingSpotLength;

    @PositiveOrZero(message = "places for electric cars must be positive or zero")
    private int placesForElectricCars;
}
