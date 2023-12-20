package com.example.carparkingapi.repository;


import com.example.carparkingapi.domain.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
