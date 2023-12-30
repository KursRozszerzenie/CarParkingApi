package com.example.carparkingapi.repository;


import com.example.carparkingapi.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<List<Car>> findAllByCustomerId(Long id);
}
