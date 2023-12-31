package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.config.security.authentication.AuthenticationRequest;
import com.example.carparkingapi.config.security.authentication.AuthenticationResponse;
import com.example.carparkingapi.config.security.authentication.AuthenticationService;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.dto.ParkingDTO;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CarService carService;

    private final ModelMapper modelMapper;

    private final ParkingService parkingService;

    private final AuthenticationService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }


    @PostMapping("/cars/add")
    public ResponseEntity<CarDTO> addCar(@RequestBody CarCommand carCommand) {
        CarDTO carDTO = modelMapper.map(carService.save(modelMapper.map(carCommand, Car.class)), CarDTO.class);
        return new ResponseEntity<>(carDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/{carId}/delete")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        carService.delete(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/park/{parkingId}")
    public ResponseEntity<CarDTO> parkCar(@PathVariable Long carId, @PathVariable Long parkingId) {
        CarDTO carDTO = carService.parkCar(carId, parkingId);
        return new ResponseEntity<>(carDTO, HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/leave")
    public ResponseEntity<Void> leaveParking(@PathVariable Long carId) {
        carService.leaveParking(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        CarDTO carDTO = modelMapper.map(carService.findMostExpensiveCar(), CarDTO.class);
        return new ResponseEntity<>(carDTO, HttpStatus.OK);
    }

    @PostMapping("/parking/save")
    public ResponseEntity<ParkingDTO> saveParking(@RequestBody @Valid ParkingCommand parkingCommand) {
        Parking parking = modelMapper.map(parkingCommand, Parking.class);
        ParkingDTO parkingDTO = modelMapper.map(parkingService.save(parking), ParkingDTO.class);
        return new ResponseEntity<>(parkingDTO, HttpStatus.CREATED);
    }

    @GetMapping("/parking/all")
    public ResponseEntity<List<ParkingDTO>> getAllParkings() {
        List<ParkingDTO> parkingDTOs = parkingService.findAll().stream()
                .map(parking -> modelMapper.map(parking, ParkingDTO.class))
                .toList();
        return new ResponseEntity<>(parkingDTOs, HttpStatus.OK);
    }

    @GetMapping("/parking/{id}/get")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long id) {
        ParkingDTO parkingDTO = modelMapper.map(parkingService.findById(id), ParkingDTO.class);
        return new ResponseEntity<>(parkingDTO, HttpStatus.OK);
    }

    @DeleteMapping("/parking/delete/{id}")
    public ResponseEntity<Void> deleteParking(@PathVariable Long id) {
        parkingService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/parking/{id}/cars")
    public ResponseEntity<List<CarDTO>> getAllCarsFromParking(@PathVariable Long id) {
        List<CarDTO> carDTOs = parkingService.findAllCarsFromParking(id).stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
        return new ResponseEntity<>(carDTOs, HttpStatus.OK);
    }

    @GetMapping("/parking/{id}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long id) {
        int count = parkingService.countAllCarsFromParking(id);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/parking/{id}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long id) {
        CarDTO carDTO = modelMapper.map(parkingService.findMostExpensiveCarFromParking(id), CarDTO.class);
        return new ResponseEntity<>(carDTO, HttpStatus.OK);
    }
}