package com.example.carparkingapi.service;

import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.*;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ActionService actionService;

    private final EditService editService;

    private final AdminRepository adminRepository;

    private final CustomerRepository customerRepository;

    private final CarRepository carRepository;

    private final ParkingRepository parkingRepository;

    public void verifyAndLog(Long adminId, ActionType actionType) {
        verifyAdminAccess(adminId);
        actionService.logAction(actionType);
    }

    public void verifyAndLog(Long adminId, ActionType actionType, Long entityId, String entityType,
                             String fieldName, String newValue) {
        verifyAdminAccess(adminId);
        editService.verifyFieldName(fieldName, entityType);
        actionService.logAction(actionType, entityId, entityType, fieldName,
                editService.getOldValue(entityId, entityType, fieldName), newValue);
    }

    public void verifyAdminAccess(Long adminId) {
        if (!adminRepository.findByUsername(getCurrentUsername())
                .orElseThrow(InvalidCredentialsException::new)
                .getId().
                equals(adminId)) {
            throw new InvalidCredentialsException();
        }
    }

    public Customer updateCustomer(Long customerId, EditCommand customerEdit) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        switch (customerEdit.getFieldName()) {
            case "username" -> customer.setUsername(customerEdit.getNewValue());
            case "password" -> customer.setPassword(customerEdit.getNewValue());
            case "firstName" -> customer.setFirstName(customerEdit.getNewValue());
            case "lastName" -> customer.setLastName(customerEdit.getNewValue());
            default -> throw new InvalidFieldNameException("Invalid field name choose from " +
                    "username, password, firstName, lastName");
        }

        return customerRepository.save(customer);
    }

    public Car updateCar(Long carId, EditCommand editCommand) {
        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        switch (editCommand.getFieldName()) {
            case "brand" -> car.setBrand(editCommand.getNewValue());
            case "model" -> car.setModel(editCommand.getNewValue());
            case "price" -> car.setPrice(Double.parseDouble(editCommand.getNewValue()));
            case "length" -> car.setLength(Integer.parseInt(editCommand.getNewValue()));
            case "width" -> car.setWidth(Integer.parseInt(editCommand.getNewValue()));
            case "dateOfProduction" -> car.setDateOfProduction(LocalDate.parse(editCommand.getNewValue()));
            case "fuel" -> car.setFuel(Fuel.valueOf(editCommand.getNewValue()));
            default -> throw new InvalidFieldNameException("Invalid field name choose from " +
                    "brand, model, color, fuel, year, price, parking");
        }

        return carRepository.save(car);
    }

    public Parking updateParking(Long parkingId, EditCommand editCommand) {
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new EntityNotFoundException("Parking not found"));

        switch (editCommand.getFieldName()) {
            case "name" -> parking.setName(editCommand.getNewValue());
            case "address" -> parking.setAdress(editCommand.getNewValue());
            case "parkingType" -> parking.setParkingType(ParkingType.valueOf(editCommand.getNewValue()));
            case "capacity" -> parking.setCapacity(Integer.parseInt(editCommand.getNewValue()));
            case "parkingSpotWidth" -> parking.setParkingSpotWidth(Integer.parseInt(editCommand.getNewValue()));
            case "parkingSpotLength" -> parking.setParkingSpotLength(Integer.parseInt(editCommand.getNewValue()));
            case "placesForElectricCars" -> parking.setPlacesForElectricCars(Integer.parseInt(editCommand.getNewValue()));
            default -> throw new InvalidFieldNameException("Invalid field name choose from " +
                    "name, address, parkingType, numberOfPlaces, numberOfElectricPlaces");
        }

        return parkingRepository.save(parking);
    }

    public Customer lockCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountNonLocked(false);

        return customerRepository.save(customer);
    }

    public Customer unlockCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountNonLocked(true);

        return customerRepository.save(customer);
    }

    public Customer enableCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountEnabled(true);

        return customerRepository.save(customer);
    }

    public Customer disableCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountEnabled(false);

        return customerRepository.save(customer);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        throw new UserNotAuthenticatedException();
    }
}
