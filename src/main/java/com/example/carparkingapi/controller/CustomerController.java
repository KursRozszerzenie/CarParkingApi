package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.config.map.struct.CarMapper;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
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

    private final CarRepository carRepository;

    private final CustomerRepository customerRepository;

    private final CarMapper carMapper;

    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/cars")
    public ResponseEntity<List<CarDTO>> getCarsByCustomer() {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomer().stream()
                .map(carMapper::carToCarDTO)
                .toList(), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Void> addCarToCustomer(@RequestBody @Valid CarCommand carCommand) {
        customUserDetailsService.verifyCustomerAccess();
        carService.save(carMapper.carCommandToCar(carCommand));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/save/batch")
    public ResponseEntity<List<Void>> saveAll(@RequestBody @Valid List<CarCommand> carCommands) {

        customUserDetailsService.verifyCustomerAccess();
        Customer customer = customerRepository.findCustomerByUsername(customUserDetailsService.getCurrentUsername())
                .orElseThrow(CustomerNotFoundException::new);

        carRepository.saveAll(carCommands.stream().map(command -> {
            Car car = carMapper.carCommandToCar(command);
            car.setCustomer(customer);
            return car;
        }).toList());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/delete/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess();
        carService.deleteCar(carId);
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

    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carMapper.carToCarDTO(carService.findMostExpensiveCarForCustomer()), HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive/{brand}")
    public ResponseEntity<CarDTO> getMostExpensiveCarByBrand(@PathVariable String brand) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carMapper.carToCarDTO(carService.findMostExpensiveCarByBrand(brand)), HttpStatus.OK);
    }

    @GetMapping("/cars/all/brand/{brand}")
    public ResponseEntity<List<CarDTO>> getAllCarsByBrand(@PathVariable String brand) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByBrand(brand).stream().map(carMapper::carToCarDTO).toList(), HttpStatus.OK);
    }

    @GetMapping("/cars/all/fuel/{fuel}")
    public ResponseEntity<List<CarDTO>> getAllCarsByFuel(@PathVariable Fuel fuel) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomerAndFuel(fuel).stream().map(carMapper::carToCarDTO).toList(), HttpStatus.OK);
    }
}