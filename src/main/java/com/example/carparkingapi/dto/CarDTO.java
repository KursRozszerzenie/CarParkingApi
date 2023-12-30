package com.example.carparkingapi.dto;

import com.example.carparkingapi.model.Fuel;
import lombok.*;

import java.time.LocalDate;

@Data
public class CarDTO {
    private String brand;

    private String model;

    private double price;

    private Fuel fuel;

    private Integer length;

    private Integer width;

    private LocalDate dateOfProduction;

}
