package com.example.carparkingapi.domain;

import com.example.carparkingapi.model.Fuel;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    private Parking parking;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
