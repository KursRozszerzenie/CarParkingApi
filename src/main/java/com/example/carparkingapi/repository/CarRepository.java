package com.example.carparkingapi.repository;


import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.model.Fuel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<List<Car>> findAllByCustomerId(Long id);

    List<Car> findByCustomerId(Long customerId);

    Optional<List<Car>> findByCustomerIdAndFuel(Long customerId, Fuel fuel);

    Optional<List<Car>> findAllByCustomerIdAndBrand(Long customerId, String brand);
}
