package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CarService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/v1/car")
@RestController
@RequiredArgsConstructor
public class CarController {

    private final ModelMapper modelMapper;

    private final CarService carService;

    private final CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<CarDTO> save(@RequestBody @Valid CarCommand carCommand) {
        Customer customer = customerRepository.findById(carCommand.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Car car = modelMapper.map(carCommand, Car.class);
        car.setCustomer(customer); // Set customer in car

        Car savedCar = carService.save(car); // Save car (which now has a reference to the customer)

        customer.getCars().add(savedCar); // Add car to customer's list
        customerRepository.save(customer); // Save customer

        return new ResponseEntity<>(modelMapper.map(savedCar, CarDTO.class), HttpStatus.CREATED);
    }

    @PostMapping("/save/batch")
    public ResponseEntity<List<CarDTO>> saveAll(@RequestBody @Valid List<CarCommand> carCommands) {
        return new ResponseEntity<>(carService.saveAll(carCommands.stream()
                        .map(command -> modelMapper.map(command, Car.class))
                        .toList()).stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        return new ResponseEntity<>(modelMapper
                .map(carService.findById(id), CarDTO.class),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        return new ResponseEntity<>(carService.findAll().stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/park/{parkingId}")
    public ResponseEntity<CarDTO> parkCar(@PathVariable Long id, @PathVariable Long parkingId) {
        String currentUsername = getCurrentUsername();
        return new ResponseEntity<>(carService.parkCar(id, parkingId, currentUsername), HttpStatus.OK);
    }

    @PostMapping("/{carId}/leave")
    public ResponseEntity<Void> leaveParking(@PathVariable Long carId) {
        String currentUsername = getCurrentUsername();
        carService.leaveParking(carId, currentUsername);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        throw new AccessDeniedException("User not authenticated");
    }


    @GetMapping("/mostExpensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        return new ResponseEntity<>(modelMapper
                .map(carService.findMostExpensiveCar(), CarDTO.class),
                HttpStatus.OK);
    }

    @GetMapping("/mostExpensive/{brand}")
    public ResponseEntity<CarDTO> getMostExpensiveCarByBrand(@PathVariable String brand) {
        return new ResponseEntity<>(modelMapper
                .map(carService.findMostExpensiveCarByBrand(brand), CarDTO.class),
                HttpStatus.OK);
    }

    @GetMapping("/all/brand/{brand}")
    public ResponseEntity<List<CarDTO>> getAllCarsByBrand(@PathVariable String brand) {
        return new ResponseEntity<>(carService.findAllCarsByBrand(brand).stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/all/fuel/{fuel}")
    public ResponseEntity<List<CarDTO>> getAllCarsByFuel(@PathVariable Fuel fuel) {
        return new ResponseEntity<>(carService.findAllCarsByFuel(fuel).stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList(), HttpStatus.OK);
    }
}
