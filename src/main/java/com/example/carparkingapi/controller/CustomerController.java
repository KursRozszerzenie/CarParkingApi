package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CarService carService;

    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/cars")
    public ResponseEntity<Page<CarDTO>> getCarsByCustomer(
        @PageableDefault(size = 15, sort = "brand", direction = Sort.Direction.DESC) Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomer(pageable), HttpStatus.OK);
    }

    @GetMapping("/cars/all/brand/{brand}")
    public ResponseEntity<Page<CarDTO>> getAllCarsByBrand(@PathVariable String brand,
        @PageableDefault(size = 15, sort = "brand", direction = Sort.Direction.DESC) Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomerAndBrand(brand, pageable), HttpStatus.OK);
    }

    @GetMapping("/cars/all/fuel/{fuel}")
    public ResponseEntity<Page<CarDTO>> getAllCarsByFuel(@PathVariable Fuel fuel,
        @PageableDefault(size = 15, sort = "brand", direction = Sort.Direction.DESC) Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomerAndFuel(fuel, pageable), HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findMostExpensiveCarForCustomer(), HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive/{brand}")
    public ResponseEntity<CarDTO> getMostExpensiveCarByBrand(@PathVariable String brand) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>((carService.findMostExpensiveCarByCustomerAndBrand(brand)), HttpStatus.OK);
    }

    @PostMapping("cars/save")
    public ResponseEntity<Void> saveNewCar(@RequestBody @Valid CarCommand carCommand) {
        customUserDetailsService.verifyCustomerAccess();
        carService.save(carCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("cars/save/batch")
    public ResponseEntity<List<Void>> saveAll(@RequestBody @Valid List<CarCommand> carCommands) {
        customUserDetailsService.verifyCustomerAccess();
        carService.saveBatch(carCommands, customUserDetailsService.getCurrentCustomer());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/delete/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess();
        carService.delete(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/park/{parkingId}")
    public ResponseEntity<Void> parkCar(@PathVariable Long carId, @PathVariable Long parkingId) {
        customUserDetailsService.verifyCustomerAccess();
        carService.parkCar(carId, parkingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/leave")
    public ResponseEntity<Void> leaveParking(@PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess();
        carService.leaveParking(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}