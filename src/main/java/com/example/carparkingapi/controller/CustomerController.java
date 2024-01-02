package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.config.security.authentication.AuthenticationRequest;
import com.example.carparkingapi.config.security.authentication.AuthenticationResponse;
import com.example.carparkingapi.config.security.authentication.AuthenticationService;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final AuthenticationService authService;

    private final CarService carService;

    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody @Valid CustomerCommand request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticateCustomer(request));
    }

    @GetMapping("/{customerId}/cars")
    public ResponseEntity<List<CarDTO>> getCarsByCustomerId(@PathVariable Long customerId) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(carService.findAllCarsByCustomerId(customerId), HttpStatus.OK);
    }

    @PostMapping("/{customerId}/save")
    public ResponseEntity<CarDTO> addCarToCustomer(@PathVariable Long customerId,
                                                   @RequestBody @Valid CarCommand carCommand) {

        customUserDetailsService.verifyCustomerAccess(customerId);
        Car car = modelMapper.map(carCommand, Car.class);
        car.setCustomer(customerRepository.findByUsername(customUserDetailsService.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Customer not authenticated")));
        return new ResponseEntity<>(modelMapper.map(carService.save(car), CarDTO.class), HttpStatus.CREATED);
    }

    @PostMapping("/{customerId}/save/batch")
    public ResponseEntity<List<CarDTO>> saveAll(@PathVariable Long customerId,
                                                @RequestBody @Valid List<CarCommand> carCommands) {

        customUserDetailsService.verifyCustomerAccess(customerId);
        List<Car> cars = carCommands.stream().map(command -> {
            Car car = modelMapper.map(command, Car.class);
            car.setCustomer(customerRepository.findById(customerId)
                    .orElseThrow(() -> new AccessDeniedException("Customer not authenticated")));
            return car;
        }).toList();

        return new ResponseEntity<>(carService.saveAll(cars).stream()
                .map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{customerId}/cars/{carId}/delete")
    public ResponseEntity<String> deleteCar(@PathVariable Long customerId, @PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(carService.delete(carId), HttpStatus.OK);
    }

    @PostMapping("/{customerId}/cars/{carId}/park/{parkingId}")
    public ResponseEntity<CarDTO> parkCar(@PathVariable Long customerId, @PathVariable Long carId,
                                          @PathVariable Long parkingId) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(carService.parkCar(carId, parkingId), HttpStatus.OK);
    }

    @PostMapping("/{customerId}/cars/{carId}/leave")
    public ResponseEntity<String> leaveParking(@PathVariable Long customerId, @PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(carService.leaveParking(carId), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar(@PathVariable Long customerId) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(modelMapper.map(carService
                .findMostExpensiveCarForCustomer(customerId), CarDTO.class), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/cars/most-expensive/{brand}")
    public ResponseEntity<CarDTO> getMostExpensiveCarByBrand(@PathVariable Long customerId,
                                                             @PathVariable String brand) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(modelMapper.map(carService
                .findMostExpensiveCarByBrand(customerId, brand), CarDTO.class), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/cars/all/{brand}")
    public ResponseEntity<List<CarDTO>> getAllCarsByBrand(@PathVariable Long customerId, @PathVariable String brand) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(carService.findAllCarsByBrand(customerId, brand).stream()
                .map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/cars/all/{fuel}")
    public ResponseEntity<List<CarDTO>> getAllCarsByFuel(@PathVariable Long customerId, @PathVariable Fuel fuel) {
        customUserDetailsService.verifyCustomerAccess(customerId);
        return new ResponseEntity<>(carService.findAllCarsByFuel(customerId, fuel).stream()
                .map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.OK);
    }
}
