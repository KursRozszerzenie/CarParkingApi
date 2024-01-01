package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.AdminCommand;
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
import com.example.carparkingapi.service.CustomUserDetailsService;
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

    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AdminCommand request) {
        return ResponseEntity.ok(authService.authenticateAdmin(request));
    }

    @PostMapping("{adminId}/cars/add")
    public ResponseEntity<CarDTO> addCar(@PathVariable Long adminId,
                                         @RequestBody @Valid CarCommand carCommand) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(modelMapper
                .map(carService.save(modelMapper.map(carCommand, Car.class)), CarDTO.class), HttpStatus.CREATED);
    }

    @DeleteMapping("{adminId}/cars/{carId}/delete")
    public ResponseEntity<Void> deleteCar(@PathVariable Long adminId, @PathVariable Long carId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        carService.delete(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("{adminId}/cars/{carId}/park/{parkingId}")
    public ResponseEntity<CarDTO> parkCar(@PathVariable Long adminId,
                                          @PathVariable Long carId,
                                          @PathVariable Long parkingId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(carService.parkCar(carId, parkingId), HttpStatus.OK);
    }

    @PostMapping("{adminId}/cars/{carId}/leave")
    public ResponseEntity<Void> leaveParking(@PathVariable Long adminId, @PathVariable Long carId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        carService.leaveParking(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{adminId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar(@PathVariable Long adminId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(modelMapper.map(carService.findMostExpensiveCar(), CarDTO.class), HttpStatus.OK);
    }

    @PostMapping("{adminId}/parking/save")
    public ResponseEntity<ParkingDTO> saveParking(@PathVariable Long adminId,
                                                  @RequestBody @Valid ParkingCommand parkingCommand) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(modelMapper
                .map(parkingService
                .save(modelMapper.map(parkingCommand, Parking.class)), ParkingDTO.class), HttpStatus.CREATED);
    }

    @GetMapping("{adminId}/parking/all")
    public ResponseEntity<List<ParkingDTO>> getAllParkings(@PathVariable Long adminId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(parkingService.findAll().stream()
                .map(parking -> modelMapper.map(parking, ParkingDTO.class)).toList(), HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/get")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long adminId,
                                                     @PathVariable Long parkingId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(modelMapper.map(parkingService
                .findById(parkingId), ParkingDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("{adminId}/parking/delete/{parkingId}")
    public ResponseEntity<Void> deleteParking(@PathVariable Long adminId,
                                              @PathVariable Long parkingId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        parkingService.delete(parkingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/cars")
    public ResponseEntity<List<CarDTO>> getAllCarsFromParking(@PathVariable Long adminId,
                                                              @PathVariable Long parkingId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(parkingService.findAllCarsFromParking(parkingId).stream()
                .map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long adminId,
                                                           @PathVariable Long parkingId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(parkingService.countAllCarsFromParking(parkingId), HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long adminId,
                                                                 @PathVariable Long parkingId) {
        customUserDetailsService.verifyAdminAccess(adminId);
        return new ResponseEntity<>(modelMapper.map(parkingService
                .findMostExpensiveCarFromParking(parkingId), CarDTO.class), HttpStatus.OK);
    }
}