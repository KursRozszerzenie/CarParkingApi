package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.dto.ActionDTO;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.dto.ParkingDTO;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.service.ActionService;
import com.example.carparkingapi.service.AdminService;
import com.example.carparkingapi.service.CarService;
import com.example.carparkingapi.service.ParkingService;
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
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CarService carService;

    private final AdminService adminService;

    private final ParkingService parkingService;

    private final ActionService actionService;

    @PutMapping("/customers/update/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId,
                                                      @RequestBody EditCommand editCommand) {
//        Dla potomnych, akcja zawsze bedzie edit w tym miejscu
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, customerId, CUSTOMER,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(adminService.updateCustomer(customerId, editCommand), HttpStatus.OK);
    }

    @PutMapping("/cars/update/{carId}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long carId,
                                            @RequestBody EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, carId, CAR,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(adminService.updateCar(carId, editCommand),HttpStatus.OK);
    }

    @PutMapping("/parking/update/{parkingId}")
    public ResponseEntity<ParkingDTO> updateParking(@PathVariable Long parkingId,
                                                    @RequestBody @Valid EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, parkingId, PARKING,
                editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(adminService.updateParking(parkingId, editCommand), HttpStatus.OK);
    }

    @PutMapping("/customers/enable-account/{customerId}")
    public ResponseEntity<Void> enableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ENABLE_CUSTOMER_ACCOUNT);
        adminService.enableCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/customers/disable-account/{customerId}")
    public ResponseEntity<Void> disableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DISABLE_CUSTOMER_ACCOUNT);
        adminService.disableCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/customers/lock-account/{customerId}")
    public ResponseEntity<Void> lockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.LOCK_CUSTOMER_ACCOUNT);
        adminService.lockCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/customers/unlock-account/{customerId}")
    public ResponseEntity<Void> unlockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.UNLOCK_CUSTOMER_ACCOUNT);
        adminService.unlockCustomerAccount(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/action/all")
    public ResponseEntity<Page<ActionDTO>> getAllActions(
        @PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_ACTIONS);
        return new ResponseEntity<>(actionService.getActionsForAdmin(pageable), HttpStatus.OK);
    }

    @GetMapping("customers/all")
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CUSTOMERS);
        return new ResponseEntity<>(adminService.getAllCustomers(pageable), HttpStatus.OK);
    }

    @GetMapping("/cars/all")
    public ResponseEntity<Page<CarDTO>> getAllCars(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CARS);
        return new ResponseEntity<>(carService.getAllCars(pageable), HttpStatus.OK);
    }

    @GetMapping("/parking/all")
    public ResponseEntity<Page<ParkingDTO>> getAllParkings(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_PARKINGS);
        return new ResponseEntity<>(parkingService.getAllParkings(pageable), HttpStatus.OK);
    }

    @PostMapping("/cars/save")
    public ResponseEntity<Void> addCar(@RequestBody @Valid CarCommand carCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_CAR);
        carService.saveWithoutCustomer(carCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("cars/save/batch")
    public ResponseEntity<Void> saveAll(@RequestBody @Valid List<CarCommand> carCommands) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_CAR);
        carService.saveBatchWithoutCustomer(carCommands);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/parking/save")
    public ResponseEntity<Void> saveParking(@RequestBody @Valid ParkingCommand parkingCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_PARKING);
        parkingService.save(parkingCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/{carId}/delete")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_CAR);
        carService.delete(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/parking/{parkingId}/delete")
    public ResponseEntity<Void> deleteParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_PARKING);
        parkingService.delete(parkingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/park/{parkingId}")
    public ResponseEntity<Void> parkCar(@PathVariable Long carId, @PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.PARKING_CAR);
        carService.parkCar(carId, parkingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cars/{carId}/leave")
    public ResponseEntity<Void> leaveParking(@PathVariable Long carId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.LEAVING_PARKING);
        carService.leaveParking(carId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars")
    public ResponseEntity<Page<CarDTO>> getAllCarsFromParking(@PathVariable Long parkingId,
      @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CARS_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findAllCarsFromParking(parkingId, pageable), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_CARS_COUNT_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findById(parkingId).getCars().size(), HttpStatus.OK);
    }

    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR);
        return new ResponseEntity<>(carService.findMostExpensiveCar(), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findMostExpensiveCarFromParking(parkingId), HttpStatus.OK);
    }
}