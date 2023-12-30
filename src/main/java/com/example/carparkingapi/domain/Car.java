package com.example.carparkingapi.domain;

import com.example.carparkingapi.model.Fuel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Data
@Valid
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand cannot be null or blank")
    private String brand;

    @NotBlank(message = "Model cannot be null or blank")
    private String model;

    @Positive(message = "Price must be positive, cannot be null or blank")
    private double price;

    @Positive(message = "Length must be positive, cannot be null or blank")
    private int length;

    @Positive(message = "Width must be positive, cannot be null or blank")
    private int width;

    @NotNull(message = "Date of production cannot be null")
    private LocalDate dateOfProduction;

    @NotNull(message = "Fuel cannot be null")
    @Enumerated(EnumType.STRING)
    private Fuel fuel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    private Parking parking;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
