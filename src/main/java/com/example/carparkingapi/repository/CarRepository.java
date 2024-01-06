package com.example.carparkingapi.repository;


import com.example.carparkingapi.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByCustomerUsername(String username);
}
