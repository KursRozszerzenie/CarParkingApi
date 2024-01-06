package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;

    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/cars")
    public ResponseEntity<List<CarDTO>> getCarsByCustomer() {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomer(), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<CarDTO> addCarToCustomer(@RequestBody @Valid CarCommand carCommand) {
        customUserDetailsService.verifyCustomerAccess();
        Car car = modelMapper.map(carCommand, Car.class);
        car.setCustomer(customerRepository.findCustomerByUsername(customUserDetailsService.getCurrentUsername()).orElseThrow(CustomerNotFoundException::new));
        return new ResponseEntity<>(modelMapper.map(carRepository.save(car), CarDTO.class), HttpStatus.CREATED);
    }

    @PostMapping("/save/batch")
    public ResponseEntity<List<CarDTO>> saveAll(@RequestBody @Valid List<CarCommand> carCommands) {

        customUserDetailsService.verifyCustomerAccess();
        List<Car> cars = carCommands.stream().map(command -> {
            Car car = modelMapper.map(command, Car.class);
            car.setCustomer(customerRepository.findCustomerByUsername(customUserDetailsService.getCurrentUsername()).orElseThrow(CustomerNotFoundException::new));
            return car;
        }).toList();

        return new ResponseEntity<>(carRepository.saveAll(cars).stream().map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/{carId}/delete")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess();
        carRepository.delete(carRepository.findById(carId).orElseThrow(CarNotFoundException::new));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/park/{parkingId}")
    public ResponseEntity<CarDTO> parkCar(@PathVariable Long carId, @PathVariable Long parkingId) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.parkCar(carId, parkingId), HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/leave")
    public ResponseEntity<String> leaveParking(@PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.leaveParking(carId), HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(modelMapper.map(carService.findMostExpensiveCarForCustomer(), CarDTO.class), HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive/{brand}")
    public ResponseEntity<CarDTO> getMostExpensiveCarByBrand(@PathVariable String brand) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(modelMapper.map(carService.findMostExpensiveCarByBrand(brand), CarDTO.class), HttpStatus.OK);
    }

    @GetMapping("/cars/all/brand/{brand}")
    public ResponseEntity<List<CarDTO>> getAllCarsByBrand(@PathVariable String brand) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByBrand(brand).stream().map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.OK);
    }

    @GetMapping("/cars/all/fuel/{fuel}")
    public ResponseEntity<List<CarDTO>> getAllCarsByFuel(@PathVariable Fuel fuel) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomerAndFuel(fuel).stream().map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.OK);
    }
}
