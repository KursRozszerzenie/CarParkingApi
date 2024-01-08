package com.example.carparkingapi.controller;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.config.map.struct.CarMapper;
import com.example.carparkingapi.config.map.struct.CustomerMapper;
import com.example.carparkingapi.config.map.struct.ParkingMapper;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.dto.ParkingDTO;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.repository.ActionRepository;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.repository.ParkingRepository;
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

    private final CarMapper carMapper;

    private final CustomerMapper customerMapper;

    private final ParkingMapper parkingMapper;

    private final AdminService adminService;

    private final CarRepository carRepository;

    private final ParkingRepository parkingRepository;

    private final CustomerRepository customerRepository;

    private final ActionRepository actionRepository;

    private final ParkingService parkingService;

    @PutMapping("/customers/update/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId, @RequestBody EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, customerId, CUSTOMER, editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(customerMapper.customerToCustomerDTO(adminService.updateCustomer(customerId, editCommand)), HttpStatus.OK);
    }

    @PutMapping("/cars/update/{carId}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long carId, @RequestBody EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, carId, CAR, editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(carMapper.carToCarDTO(adminService.updateCar(carId, editCommand)), HttpStatus.OK);
    }

    @PutMapping("/parking/update/{parkingId}")
    public ResponseEntity<ParkingDTO> updateParking(@PathVariable Long parkingId, @RequestBody @Valid EditCommand editCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.EDIT, parkingId, PARKING, editCommand.getFieldName(), editCommand.getNewValue());
        return new ResponseEntity<>(parkingMapper.parkingToParkingDTO(adminService.updateParking(parkingId, editCommand)), HttpStatus.OK);
    }

    @PutMapping("/customers/enable-account/{customerId}")
    public ResponseEntity<CustomerDTO> enableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ENABLE_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(customerMapper.customerToCustomerDTO(adminService.enableCustomerAccount(customerId)), HttpStatus.OK);
    }

    @PutMapping("/customers/disable-account/{customerId}")
    public ResponseEntity<CustomerDTO> disableCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DISABLE_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(customerMapper.customerToCustomerDTO(adminService.disableCustomerAccount(customerId)), HttpStatus.OK);
    }

    @PutMapping("/customers/lock-account/{customerId}")
    public ResponseEntity<CustomerDTO> lockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.LOCK_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(customerMapper.customerToCustomerDTO(adminService.lockCustomerAccount(customerId)), HttpStatus.OK);
    }

    @PutMapping("/customers/unlock-account/{customerId}")
    public ResponseEntity<CustomerDTO> unlockCustomerAccount(@PathVariable Long customerId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.UNLOCK_CUSTOMER_ACCOUNT);
        return new ResponseEntity<>(customerMapper.customerToCustomerDTO(adminService.unlockCustomerAccount(customerId)), HttpStatus.OK);
    }

    @GetMapping("/action/all")
    public ResponseEntity<Page<Action>> getAllActions(Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_ACTIONS);
        return new ResponseEntity<>(actionRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("customers/all")
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CUSTOMERS);
        return new ResponseEntity<>(customerRepository.findAll(pageable).map(customerMapper::customerToCustomerDTO), HttpStatus.OK);
    }

    @GetMapping("/cars/all")
    public ResponseEntity<Page<CarDTO>> getAllCars(@PageableDefault(size = 1, sort = "brand", direction = Sort.Direction.ASC) Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CARS);
        return new ResponseEntity<>(carRepository.findAll(pageable).map(carMapper::carToCarDTO), HttpStatus.OK);
    }

    @GetMapping("parking/all")
    public ResponseEntity<Page<ParkingDTO>> getAllParkings(Pageable pageable) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_PARKINGS);
        return new ResponseEntity<>(parkingRepository.findAll(pageable).map(parkingMapper::parkingToParkingDTO), HttpStatus.OK);
    }

    @PostMapping("/cars/save")
    public ResponseEntity<Void> addCar(@RequestBody @Valid CarCommand carCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_CAR);
        carRepository.save(carMapper.carCommandToCar(carCommand));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/cars/{carId}/delete")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_CAR);
        carService.delete(carId);
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

    @GetMapping("/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCar() {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR);
        return new ResponseEntity<>(carMapper.carToCarDTO(carService.findMostExpensiveCar()), HttpStatus.OK);
    }

    @PostMapping("/parking/save")
    public ResponseEntity<ParkingDTO> saveParking(@RequestBody @Valid ParkingCommand parkingCommand) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.ADDING_PARKING);
        return new ResponseEntity<>(parkingMapper.parkingToParkingDTO(parkingService.save(parkingMapper.parkingCommandToParking(parkingCommand))), HttpStatus.CREATED);
    }

    @GetMapping("/parking/{parkingId}/get")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_PARKING);
        return new ResponseEntity<>(parkingMapper.parkingToParkingDTO(parkingService.findById(parkingId)), HttpStatus.OK);
    }

    @DeleteMapping("/parking/delete/{parkingId}")
    public ResponseEntity<String> deleteParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.DELETING_PARKING);
        return new ResponseEntity<>(parkingService.delete(parkingId), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars")
    public ResponseEntity<List<CarDTO>> getAllCarsFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_ALL_CARS_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findAllCarsFromParking(parkingId).stream().map(carMapper::carToCarDTO).toList(), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars/count")
    public ResponseEntity<Integer> countAllCarsFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_CARS_COUNT_FROM_PARKING);
        return new ResponseEntity<>(parkingService.findById(parkingId).getCars().size(), HttpStatus.OK);
    }

    @GetMapping("/parking/{parkingId}/cars/most-expensive")
    public ResponseEntity<CarDTO> getMostExpensiveCarFromParking(@PathVariable Long parkingId) {
        adminService.verifyAdminAccessAndSaveAction(ActionType.RETRIEVING_MOST_EXPENSIVE_CAR_FROM_PARKING);
        return new ResponseEntity<>(carMapper.carToCarDTO(parkingService.findMostExpensiveCarFromParking(parkingId)), HttpStatus.OK);
    }
}