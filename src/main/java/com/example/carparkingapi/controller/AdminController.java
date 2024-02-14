package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.dto.*;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.service.ActionService;
import com.example.carparkingapi.service.AdminService;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static com.example.carparkingapi.util.Constants.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "Admin controller", description = "Manage Customers and cars")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CarService carService;

    private final AdminService adminService;

    private final ParkingService parkingService;

    private final ActionService actionService;

    @Operation(summary = "Update customer")
    @ApiResponse(responseCode = "200", description = "Successful update of customer details",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomerDTO.class)))
    @PutMapping("/customers/update/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId,
                                                      @RequestBody EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, customerId, CUSTOMER,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(adminService.updateCustomer(customerId, editCommand), HttpStatus.OK);
    }

    @Operation(summary = "Update car")
    @ApiResponse(responseCode = "200", description = "Successful update of car details",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
    @PutMapping("/cars/update/{carId}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long carId,
                                            @RequestBody EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, carId, CAR,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(adminService.updateCar(carId, editCommand),HttpStatus.OK);
    }

    @Operation(summary = "Update parking")
    @ApiResponse(responseCode = "200", description = "Successful update of parking details",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ParkingDTO.class)))
    @PutMapping("/parking/update/{parkingId}")
    public ResponseEntity<ParkingDTO> updateParking(@PathVariable Long parkingId,
                                                    @RequestBody @Valid EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, parkingId, PARKING,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(adminService.updateParking(parkingId, editCommand), HttpStatus.OK);
    }

    @Operation(summary = "enable customer account")
    @ApiResponse(responseCode = "200", description = "Successful enabling of customer account")
    @PutMapping("/customers/enable-account/{customerId}")
    public ResponseEntity<Void> enableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ENABLE_CUSTOMER_ACCOUNT);
        adminService.enableCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Disable customer account")
    @ApiResponse(responseCode = "200", description = "Successful disabling of customer account")
    @PutMapping("/customers/disable-account/{customerId}")
    public ResponseEntity<Void> disableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DISABLE_CUSTOMER_ACCOUNT);
        adminService.disableCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Lock customer account")
    @ApiResponse(responseCode = "200", description = "Successful locking of customer account")
    @PutMapping("/customers/lock-account/{customerId}")
    public ResponseEntity<Void> lockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.LOCK_CUSTOMER_ACCOUNT);
        adminService.lockCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Unlock customer account")
    @ApiResponse(responseCode = "200", description = "Successful unlocking of customer account")
    @PutMapping("/customers/unlock-account/{customerId}")
    public ResponseEntity<Void> unlockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.UNLOCK_CUSTOMER_ACCOUNT);
        adminService.unlockCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get all actions")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all actions",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/action/all")
    public ResponseEntity<Page<ActionDTO>> getAllActions(
        @PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_ACTIONS);
        return new ResponseEntity<>(actionService.getActionsForAdmin(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get all customers")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all customers",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("customers/all")
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CUSTOMERS);
        return new ResponseEntity<>(adminService.getAllCustomers(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get all cars")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all cars",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping(path = "/cars/all")
    public ResponseEntity<Page<CarDTO>> getAllCars(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CARS);
        return new ResponseEntity<>(carService.getAllCars(pageable), HttpStatus.OK);
    }

    @Operation(summary = "Get all parkings")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all parkings",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    @GetMapping("/parking/all")
    public ResponseEntity<Page<ParkingDTO>> getAllParkings(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_PARKINGS);
        return new ResponseEntity<>(parkingService.getAllParkings(pageable), HttpStatus.OK);
    }

    @Operation(summary = "saving one car")
    @PostMapping("/cars/save")
    public ResponseEntity<Void> addCar(@RequestBody @Valid CarCommand carCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_CAR);
        carService.saveWithoutCustomer(carCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Saving many cars at once")
    @PostMapping("cars/save/batch")
    public ResponseEntity<Void> saveAll(@RequestBody @Valid List<CarCommand> carCommands) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_CAR);
        carService.saveBatchWithoutCustomer(carCommands);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Saving parking")
    @PostMapping("/parking/save")
    public ResponseEntity<Void> saveParking(@RequestBody @Valid ParkingCommand parkingCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_PARKING);
        parkingService.save(parkingCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Delete car")
    @ApiResponse(responseCode = "200", description = "Successful deletion of car")
    @DeleteMapping("/cars/{carId}/delete")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_CAR);
        carService.delete(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Delete parking")
    @ApiResponse(responseCode = "200", description = "Successful deletion of parking")
    @DeleteMapping("/parking/{parkingId}/delete")
    public ResponseEntity<Void> deleteParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_PARKING);
        parkingService.delete(parkingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Park a car")
    @ApiResponse(responseCode = "200", description = "Successful parking of a car")
    @PostMapping("/cars/{carId}/park/{parkingId}")
    public ResponseEntity<Void> parkCar(@PathVariable Long carId, @PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.PARKING_CAR);
        carService.parkCar(carId, parkingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Leave parking")
    @ApiResponse(responseCode = "200", description = "Successful leaving of parking by a car")
    @PostMapping("/cars/{carId}/leave")
    public ResponseEntity<Void> leaveParking(@PathVariable Long carId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.LEAVING_PARKING);
        carService.leaveParking(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get all cars from parking")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all cars from parking",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/parking/{parkingId}/cars")
    public ResponseEntity<Page<CarDTO>> getAllCarsFromParking(@PathVariable Long parkingId,
                                                              @PageableDefault(size = 15, sort = "id",
                                                                      direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CARS_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findAllCarsFromParking(parkingId, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Count all cars from parking")
    @ApiResponse(responseCode = "200", description = "getting count of all cars in parking",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Integer.class)))
    @GetMapping("/parking/{parkingId}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_CARS_COUNT_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findById(parkingId).getCars().size(), HttpStatus.OK);
    }

    @Operation(summary = "Get the most expensive car")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of the most expensive car",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR);
        return new ResponseEntity<>(carService.findMostExpensiveCar(), HttpStatus.OK);
    }

    @Operation(summary = "Get the most expensive car from parking")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of the most expensive car from given parking",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/parking/{parkingId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findMostExpensiveCarFromParking(parkingId), HttpStatus.OK);
    }
}