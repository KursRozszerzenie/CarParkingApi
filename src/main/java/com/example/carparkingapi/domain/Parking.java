package com.example.carparkingapi.domain;

import com.example.carparkingapi.model.ParkingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Data
@Valid
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Adress cannot be blank")
    private String adress;

    @Positive(message = "capacity must be positive, cannot be null or blank")
    private int capacity;

    @NotNull(message = "Parking type cannot be null")
    @Enumerated(EnumType.STRING)
    private ParkingType parkingType;

    @PositiveOrZero(message = "taken places must be positive or zero")
    private int takenPlaces;

    @Positive(message = "parking spot width must be positive, cannot be null or blank")
    private int parkingSpotWidth;

    @Positive(message = "parking spot length must be positive, cannot be null or blank")
    private int parkingSpotLength;

    @PositiveOrZero(message = "places for electric cars must be positive or zero")
    private int placesForElectricCars;

    @PositiveOrZero(message = "taken electric places must be positive or zero")
    private int takenElectricPlaces;

    @OneToMany(mappedBy = "parking")
    private List<Car> cars = new ArrayList<>();
}
