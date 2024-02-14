package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Get all cars by customer")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all cars by customer",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/cars")
    public ResponseEntity<Page<CarDTO>> getAllCarsByCustomer(
            @PageableDefault(size = 15, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomer(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get all cars by customer and brand")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all cars by customer and brand",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/cars/all/brand/{brand}")
    public ResponseEntity<Page<CarDTO>> getAllCarsByCustomerAndBrand(@PathVariable String brand,
                                                                     @PageableDefault(size = 15, sort = "price",
                                                                             direction = Sort.Direction.ASC) Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomerAndBrand(brand, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get all cars by customer and fuel type")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all cars by customer and fuel type",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/cars/all/fuel/{fuel}")
    public ResponseEntity<Page<CarDTO>> getAllCarsByCustomerAndFuel(@PathVariable Fuel fuel,
                                                                    @PageableDefault(size = 15, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findAllCarsByCustomerAndFuel(fuel, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get the most expensive car for a customer")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of the most expensive car for a customer",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar(Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findMostExpensiveCarForCustomer(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get the most expensive car by brand for a customer")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of the most expensive car by brand for a customer",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/cars/most-expensive/{brand}")
    public ResponseEntity<CarDTO> getMostExpensiveCarByBrand(@PathVariable String brand, Pageable pageable) {
        customUserDetailsService.verifyCustomerAccess();
        return new ResponseEntity<>(carService.findMostExpensiveCarByCustomerAndBrand(brand, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Save a new car")
    @ApiResponse(responseCode = "201", description = "Successful saving of a new car")
    @PostMapping("cars/save")
    public ResponseEntity<Void> saveNewCar(@RequestBody @Valid CarCommand carCommand) {
        customUserDetailsService.verifyCustomerAccess();
        carService.save(carCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Save a batch of cars")
    @ApiResponse(responseCode = "201", description = "Successful saving of a batch of cars")
    @PostMapping("cars/save/batch")
    public ResponseEntity<Void> saveAll(@RequestBody @Valid List<CarCommand> carCommands) {
        customUserDetailsService.verifyCustomerAccess();
        carService.saveBatch(carCommands, customUserDetailsService.getCurrentCustomer());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a car")
    @ApiResponse(responseCode = "200", description = "Successful deletion of a car")
    @DeleteMapping("/cars/delete/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess();
        carService.delete(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Park a car")
    @ApiResponse(responseCode = "200", description = "Successful parking of a car")
    @PostMapping("/cars/{carId}/park/{parkingId}")
    public ResponseEntity<Void> parkCar(@PathVariable Long carId, @PathVariable Long parkingId) {
        customUserDetailsService.verifyCustomerAccess();
        carService.parkCar(carId, parkingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Leave parking")
    @ApiResponse(responseCode = "200", description = "Successful leaving of parking by a car")
    @PostMapping("/cars/{carId}/leave")
    public ResponseEntity<Void> leaveParking(@PathVariable Long carId) {
        customUserDetailsService.verifyCustomerAccess();
        carService.leaveParking(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
