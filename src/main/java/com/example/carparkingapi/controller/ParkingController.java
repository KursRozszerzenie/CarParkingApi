package com.example.carparkingapi.controller;


import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.dto.ParkingDTO;
import com.example.carparkingapi.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/parking")
@RestController
@RequiredArgsConstructor
public class ParkingController {

    private final ModelMapper modelMapper;

    private final ParkingService parkingService;

    @PostMapping
    public ResponseEntity<ParkingDTO> save(@RequestBody ParkingCommand parkingCommand) {
        return new ResponseEntity<>(modelMapper
                .map(parkingService.save(modelMapper
                        .map(parkingCommand, Parking.class)), ParkingDTO.class),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParkingDTO>> getAllParkings() {
        return new ResponseEntity<>(parkingService.findAll().stream()
                .map(parking -> modelMapper.map(parking, ParkingDTO.class))
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long id) {
        return new ResponseEntity<>(modelMapper
                .map(parkingService.findById(id), ParkingDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParking(@PathVariable Long id) {
        parkingService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/cars")
    public ResponseEntity<List<CarDTO>> getAllCarsFromParking(@PathVariable Long id) {
        return new ResponseEntity<>(parkingService.findAllCarsFromParking(id).stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/{id}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long id) {
        return new ResponseEntity<>(parkingService.countAllCarsFromParking(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/cars/mostExpensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long id) {
        return new ResponseEntity<>(modelMapper
                .map(parkingService.findMostExpensiveCarFromParking(id), CarDTO.class),
                HttpStatus.OK);
    }
}
