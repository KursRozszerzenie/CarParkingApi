package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.command.AdminCommand;
import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.config.security.authentication.AuthenticationResponse;
import com.example.carparkingapi.config.security.authentication.AuthenticationService;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.dto.ParkingDTO;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.service.AdminService;
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

    private final AuthenticationService authService;

    private final AdminService adminService;

    private final ModelMapper modelMapper;

    private final ParkingService parkingService;

    @PutMapping("{adminId}/customers/update/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long adminId,
                                                      @PathVariable Long customerId,
                                                      @RequestBody EditCommand editCommand) {
        adminService.verifyAndLog(adminId, ActionType.UPDATE_CUSTOMER, customerId, "customer",
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(modelMapper.map(adminService
                .updateCustomer(customerId, editCommand), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("{adminId}/cars/update/{carId}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long adminId,
                                            @PathVariable Long carId,
                                            @RequestBody EditCommand editCommand) {
        adminService.verifyAndLog(adminId, ActionType.UPDATING_CAR, carId, "car",
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(modelMapper.map(adminService
                .updateCar(carId, editCommand), CarDTO.class), HttpStatus.OK);
    }

    @PutMapping("{adminId}/parking/update/{parkingId}")
    public ResponseEntity<ParkingDTO> updateParking(@PathVariable Long adminId, @PathVariable Long parkingId,
                                                    @RequestBody @Valid EditCommand editCommand) {
        adminService.verifyAndLog(adminId, ActionType.UPDATING_PARKING, parkingId, "parking",
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(modelMapper.map(adminService
                .updateParking(parkingId, editCommand), ParkingDTO.class), HttpStatus.OK);
    }

    @PutMapping("{adminId}/customers/enable-account/{customerId}")
    public ResponseEntity<CustomerDTO> enableCustomerAccount(@PathVariable Long adminId,
                                                             @PathVariable Long customerId) {
        adminService.verifyAndLog(adminId, ActionType.ENABLE_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .enableCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("{adminId}/customers/disable-account/{customerId}")
    public ResponseEntity<CustomerDTO> disableCustomerAccount(@PathVariable Long adminId,
                                                              @PathVariable Long customerId) {
        adminService.verifyAndLog(adminId, ActionType.DISABLE_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .disableCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("{adminId}/customers/lock-account/{customerId}")
    public ResponseEntity<CustomerDTO> lockCustomerAccount(@PathVariable Long adminId,
                                                           @PathVariable Long customerId) {
        adminService.verifyAndLog(adminId, ActionType.LOCK_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .lockCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PutMapping("{adminId}/customers/unlock-account/{customerId}")
    public ResponseEntity<CustomerDTO> unlockCustomerAccount(@PathVariable Long adminId,
                                                             @PathVariable Long customerId) {
        adminService.verifyAndLog(adminId, ActionType.UNLOCK_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(modelMapper.map(adminService
                .unlockCustomerAccount(customerId), CustomerDTO.class), HttpStatus.OK);
    }

    @PostMapping("{adminId}/cars/add")
    public ResponseEntity<CarDTO> addCar(@PathVariable Long adminId,
                                         @RequestBody @Valid CarCommand carCommand) {
        adminService.verifyAndLog(adminId, ActionType.ADDING_CAR);
        return new ResponseEntity<>(modelMapper
                .map(carService.save(modelMapper.map(carCommand, Car.class)), CarDTO.class), HttpStatus.CREATED);
    }

    @DeleteMapping("{adminId}/cars/{carId}/delete")
    public ResponseEntity<String> deleteCar(@PathVariable Long adminId,
                                            @PathVariable Long carId) {
        adminService.verifyAndLog(adminId, ActionType.DELETING_CAR);
        return new ResponseEntity<>(carService.delete(carId), HttpStatus.OK);
    }

    @PostMapping("{adminId}/cars/{carId}/park/{parkingId}")
    public ResponseEntity<CarDTO> parkCar(@PathVariable Long adminId,
                                          @PathVariable Long carId,
                                          @PathVariable Long parkingId) {
        adminService.verifyAndLog(adminId, ActionType.PARKING_CAR);
        return new ResponseEntity<>(carService.parkCar(carId, parkingId), HttpStatus.OK);
    }

    @PostMapping("{adminId}/cars/{carId}/leave")
    public ResponseEntity<String> leaveParking(@PathVariable Long adminId, @PathVariable Long carId) {
        adminService.verifyAndLog(adminId, ActionType.LEAVING_PARKING);
        return new ResponseEntity<>(carService.leaveParking(carId), HttpStatus.OK);
    }

    @GetMapping("{adminId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar(@PathVariable Long adminId) {
        adminService.verifyAndLog(adminId, ActionType.RETRIEVING_MOST_EXPENSIVE_CAR);
        return new ResponseEntity<>(modelMapper.map(carService.findMostExpensiveCar(), CarDTO.class), HttpStatus.OK);
    }

    @PostMapping("{adminId}/parking/save")
    public ResponseEntity<ParkingDTO> saveParking(@PathVariable Long adminId,
                                                  @RequestBody @Valid ParkingCommand parkingCommand) {
        adminService.verifyAndLog(adminId, ActionType.ADDING_PARKING);
        return new ResponseEntity<>(modelMapper
                .map(parkingService
                .save(modelMapper.map(parkingCommand, Parking.class)), ParkingDTO.class), HttpStatus.CREATED);
    }

    @GetMapping("{adminId}/parking/all")
    public ResponseEntity<List<ParkingDTO>> getAllParkings(@PathVariable Long adminId) {
        adminService.verifyAndLog(adminId, ActionType.RETRIEVING_ALL_PARKINGS);
        return new ResponseEntity<>(parkingService.findAll().stream()
                .map(parking -> modelMapper.map(parking, ParkingDTO.class)).toList(), HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/get")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long adminId,
                                                     @PathVariable Long parkingId) {
        adminService.verifyAndLog(adminId, ActionType.RETRIEVING_PARKING);
        return new ResponseEntity<>(modelMapper.map(parkingService
                .findById(parkingId), ParkingDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("{adminId}/parking/delete/{parkingId}")
    public ResponseEntity<String> deleteParking(@PathVariable Long adminId,
                                              @PathVariable Long parkingId) {
        adminService.verifyAndLog(adminId, ActionType.DELETING_PARKING);
        return new ResponseEntity<>(parkingService.delete(parkingId), HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/cars")
    public ResponseEntity<List<CarDTO>> getAllCarsFromParking(@PathVariable Long adminId,
                                                              @PathVariable Long parkingId) {
        adminService.verifyAndLog(adminId, ActionType.RETRIEVING_ALL_CARS_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findAllCarsFromParking(parkingId).stream()
                .map(car -> modelMapper.map(car, CarDTO.class)).toList(), HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long adminId,
                                                           @PathVariable Long parkingId) {
        adminService.verifyAndLog(adminId, ActionType.RETRIEVING_CARS_COUNT_FROM_PARKING);
        return new ResponseEntity<>(parkingService.countAllCarsFromParking(parkingId), HttpStatus.OK);
    }

    @GetMapping("{adminId}/parking/{parkingId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long adminId,
                                                                 @PathVariable Long parkingId) {
        adminService.verifyAndLog(adminId, ActionType.RETRIEVING_MOST_EXPENSIVE_CAR_FROM_PARKING);
        return new ResponseEntity<>(modelMapper.map(parkingService
                .findMostExpensiveCarFromParking(parkingId), CarDTO.class), HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AdminCommand request) {
        return ResponseEntity.ok(authService.authenticateAdmin(request));
    }

}