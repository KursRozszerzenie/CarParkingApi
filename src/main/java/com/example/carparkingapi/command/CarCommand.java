package com.example.carparkingapi.command;

import com.example.carparkingapi.model.Fuel;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
@Valid
public class CarCommand {

    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @NotBlank(message = "Model cannot be blank")
    private String model;

    @PositiveOrZero(message = "Price must be positive or zero")
    private double price;

    @Positive(message = "Length must be positive")
    private Integer length;

    @Positive(message = "Width must be positive")
    private Integer width;

    @NotNull(message = "Fuel cannot be null")
    private Fuel fuel;

    @NotNull(message = "Date of production cannot be null")
    private LocalDate dateOfProduction;
}
