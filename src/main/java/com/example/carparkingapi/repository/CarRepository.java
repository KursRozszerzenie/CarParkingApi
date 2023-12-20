package com.example.carparkingapi.repository;


import com.example.carparkingapi.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

}
