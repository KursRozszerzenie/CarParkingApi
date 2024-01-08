package com.example.carparkingapi.repository;


import com.example.carparkingapi.domain.Car;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findAllCarsByCustomerUsername(String username, Pageable pageable);

    List<Car> findAllCarsByCustomerUsername(String username);

    @NotNull Page<Car> findAll(@NotNull Pageable pageable);

}
