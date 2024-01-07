package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.dto.ParkingDTO;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.service.AdminService;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.example.carparkingapi.util.Constants.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CarService carService;

    private final AdminService adminService;

    private final CarRepository carRepository;

    private final ModelMapper modelMapper;

    private final ParkingService parkingService;

    @PutMapping("/customers/update/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId,
                                                      @RequestBody EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.UPDATE_CUSTOMER, customerId, CUSTOMER,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(modelMapper.map(adminService
                .updateCustomer(customerId, editCommand), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("/cars/update/{carId}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long carId,
                                            @RequestBody EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.UPDATING_CAR, carId, CAR,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(modelMapper.map(adminService
                .updateCar(carId, editCommand), CarDTO.class), HttpStatus.OK);
    }

    @PutMapping("/parking/update/{parkingId}")
    public ResponseEntity<ParkingDTO> updateParking(@PathVariable Long parkingId,
                                                    @RequestBody @Valid EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.UPDATING_PARKING, parkingId, PARKING,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(modelMapper.map(adminService
                .updateParking(parkingId, editCommand), ParkingDTO.class), HttpStatus.OK);
    }

    @PutMapping("/customers/enable-account/{customerId}")
    public ResponseEntity<CustomerDTO> enableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ENABLE_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .enableCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("/customers/disable-account/{customerId}")
    public ResponseEntity<CustomerDTO> disableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DISABLE_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .disableCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("/customers/lock-account/{customerId}")
    public ResponseEntity<CustomerDTO> lockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.LOCK_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .lockCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("/customers/unlock-account/{customerId}")
    public ResponseEntity<CustomerDTO> unlockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.UNLOCK_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .unlockCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PostMapping("/cars/add")
    public ResponseEntity<CarDTO> addCar(@RequestBody @Valid CarCommand carCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_CAR);
        return new ResponseEntity<>(modelMapper
                .map(carRepository.save(modelMapper.map(carCommand, Car.class)), CarDTO.class), HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/{carId}/delete")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_CAR);
        carRepository.delete(carRepository.findById(carId).orElseThrow(CarNotFoundException::new));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/park/{parkingId}")
    public ResponseEntity<CarDTO> parkCar(@PathVariable Long carId,
                                          @PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.PARKING_CAR);
        return new ResponseEntity<>(carService.parkCar(carId, parkingId), HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/leave")
    public ResponseEntity<String> leaveParking(@PathVariable Long carId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.LEAVING_PARKING);
        return new ResponseEntity<>(carService.leaveParking(carId), HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR);
        return new ResponseEntity<>(modelMapper.map(carService.findMostExpensiveCar(), CarDTO.class), HttpStatus.OK);
    }

    @PostMapping("/parking/save")
    public ResponseEntity<ParkingDTO> saveParking(@RequestBody @Valid ParkingCommand parkingCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_PARKING);
        return new ResponseEntity<>(modelMapper
                .map(parkingService
                        .save(modelMapper.map(parkingCommand, Parking.class)), ParkingDTO.class), HttpStatus.CREATED);
    }

    @GetMapping("/parking/all")
    public ResponseEntity<List<ParkingDTO>> getAllParkings() {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_PARKINGS);
        return new ResponseEntity<>(parkingService.findAll().stream()
                .map(parking -> modelMapper.map(parking, ParkingDTO.class)).toList(), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/get")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_PARKING);
        return new ResponseEntity<>(modelMapper.map(parkingService
                .findById(parkingId), ParkingDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/parking/delete/{parkingId}")
    public ResponseEntity<String> deleteParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_PARKING);
        return new ResponseEntity<>(parkingService.delete(parkingId), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars")
    public ResponseEntity<List<CarDTO>> getAllCarsFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CARS_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findAllCarsFromParking(parkingId).stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_CARS_COUNT_FROM_PARKING);
        return new ResponseEntity<>(parkingService.countAllCarsFromParking(parkingId), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR_FROM_PARKING);
        return new ResponseEntity<>(modelMapper.map(parkingService
                .findMostExpensiveCarFromParking(parkingId), CarDTO.class), HttpStatus.OK);
    }
}