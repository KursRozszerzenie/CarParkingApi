package com.example.carparkingapi.service;

import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.exception.not.found.ParkingNotFoundException;
import com.example.carparkingapi.exception.other.InvalidFieldNameException;
import com.example.carparkingapi.exception.security.InvalidCredentialsException;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.example.carparkingapi.util.Constants.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ActionService actionService;

    private final EditService editService;

    private final AdminRepository adminRepository;

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomerRepository customerRepository;

    private final CarRepository carRepository;

    private final ParkingRepository parkingRepository;

    public void verifyAdminAccessAndSaveAction(ActionType actionType) {
        adminRepository.findAdminByUsername(customUserDetailsService.getCurrentUsername())
                .orElseThrow(() -> new InvalidCredentialsException(ADMIN_NOT_AUTHORIZED_ERROR_MESSAGE));

        actionService.saveAction(actionType);
    }

    public void verifyAdminAccessAndSaveAction(ActionType actionType, Long entityId, String entityType,
                                               String fieldName, String newValue) {
        adminRepository.findAdminByUsername(customUserDetailsService.getCurrentUsername())
                .orElseThrow(() -> new InvalidCredentialsException(ADMIN_NOT_AUTHORIZED_ERROR_MESSAGE));

        editService.verifyFieldName(fieldName, entityType);
        actionService.saveAction(actionType, entityId, entityType, fieldName,
                editService.getOldValue(entityId, entityType, fieldName), newValue);
    }

    public Customer updateCustomer(Long customerId, EditCommand customerEdit) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        switch (customerEdit.getFieldName()) {
            case USERNAME -> customer.setUsername(customerEdit.getNewValue());
            case PASSWORD -> customer.setPassword(customerEdit.getNewValue());
            case FIRST_NAME -> customer.setFirstName(customerEdit.getNewValue());
            case LAST_NAME -> customer.setLastName(customerEdit.getNewValue());
            default -> throw new InvalidFieldNameException(CUSTOMER_FIELD_ERROR_MESSAGE);
        }

        return customerRepository.save(customer);
    }

    public Car updateCar(Long carId, EditCommand editCommand) {
        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        switch (editCommand.getFieldName()) {
            case BRAND -> car.setBrand(editCommand.getNewValue());
            case MODEL -> car.setModel(editCommand.getNewValue());
            case PRICE -> car.setPrice(Double.parseDouble(editCommand.getNewValue()));
            case LENGTH -> car.setLength(Integer.parseInt(editCommand.getNewValue()));
            case WIDTH -> car.setWidth(Integer.parseInt(editCommand.getNewValue()));
            case DATE_OF_PRODUCTION -> car.setDateOfProduction(LocalDate.parse(editCommand.getNewValue()));
            case FUEL -> car.setFuel(Fuel.valueOf(editCommand.getNewValue()));
            default -> throw new InvalidFieldNameException(CAR_FIELD_ERROR_MESSAGE);
        }

        return carRepository.save(car);
    }

    public Parking updateParking(Long parkingId, EditCommand editCommand) {
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(ParkingNotFoundException::new);

        switch (editCommand.getFieldName()) {
            case PARKING_NAME -> parking.setName(editCommand.getNewValue());
            case ADRESS -> parking.setAdress(editCommand.getNewValue());
            case PARKING_TYPE -> parking.setParkingType(ParkingType.valueOf(editCommand.getNewValue()));
            case CAPACITY -> parking.setCapacity(Integer.parseInt(editCommand.getNewValue()));
            case PARKING_SPOT_WIDTH -> parking.setParkingSpotWidth(Integer.parseInt(editCommand.getNewValue()));
            case PARKING_SPOT_LENGTH -> parking.setParkingSpotLength(Integer.parseInt(editCommand.getNewValue()));
            case PLACES_FOR_ELECTRIC_CARS ->
                    parking.setPlacesForElectricCars(Integer.parseInt(editCommand.getNewValue()));
            default -> throw new InvalidFieldNameException(PARKING_FIELD_ERROR_MESSAGE);
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
}
