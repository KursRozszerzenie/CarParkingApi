package com.example.carparkingapi.repository;


import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.model.Fuel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findAllByParkingId(Long parkingId, Pageable pageable);

    Page<Car> findAllCarsByCustomerUsername(String username, Pageable pageable);

    Page<Car> findAllByCustomerUsernameAndFuel(String username, Fuel fuel, Pageable pageable);

    Page<Car> findAllByCustomerUsernameAndBrand(String username, String brand, Pageable pageable);

    @NotNull Page<Car> findAll(@NotNull Pageable pageable);

    Optional<Car> findCarByCustomerUsernameAndBrandAndModel(String username, String brand, String model);
}
