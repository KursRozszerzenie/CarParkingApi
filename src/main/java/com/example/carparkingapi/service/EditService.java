package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.exception.other.InvalidFieldNameException;
import com.example.carparkingapi.exception.not.found.ParkingNotFoundException;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.carparkingapi.util.Constants.*;

@Service
@RequiredArgsConstructor
public class EditService {

    private final CustomerRepository customerRepository;

    private final CarRepository carRepository;

    private final ParkingRepository parkingRepository;

    public void verifyFieldName(String fieldName, String entityType) {
        switch (entityType) {
            case CUSTOMER -> verifyCustomerFieldName(fieldName);
            case CAR -> verifyCarFieldName(fieldName);
            case PARKING -> verifyParkingFieldName(fieldName);
            default -> throw new InvalidFieldNameException(INVALID_ENTITY_TYPE_ERROR_MESSAGE);
        }
    }

    public String getOldValue(Long entityId, String entityType, String fieldName) {
        return switch (entityType) {
            case CUSTOMER -> getCustomerOldValue(entityId, fieldName);
            case CAR -> getCarOldValue(entityId, fieldName);
            case PARKING -> getParkingOldValue(entityId, fieldName);
            default -> throw new InvalidFieldNameException(INVALID_ENTITY_TYPE_ERROR_MESSAGE);
        };
    }

    private void verifyParkingFieldName(String fieldName) {
        if (!PARKING_NAME.equals(fieldName) && !ADRESS.equals(fieldName) && !CAPACITY.equals(fieldName)
                && !PARKING_TYPE.equals(fieldName) && !PARKING_SPOT_WIDTH.equals(fieldName)
                && !PARKING_SPOT_LENGTH.equals(fieldName) && !PLACES_FOR_ELECTRIC_CARS.equals(fieldName)) {
            throw new InvalidFieldNameException(PARKING_FIELD_ERROR_MESSAGE);
        }
    }

    private void verifyCarFieldName(String fieldName) {
        if (!BRAND.equals(fieldName) && !MODEL.equals(fieldName) && !PRICE.equals(fieldName)
                && !LENGTH.equals(fieldName) && !WIDTH.equals(fieldName) &&
                !DATE_OF_PRODUCTION.equals(fieldName) && !FUEL.equals(fieldName)) {
            throw new InvalidFieldNameException(CAR_FIELD_ERROR_MESSAGE);
        }
    }

    private void verifyCustomerFieldName(String fieldName) {
        if (!USERNAME.equals(fieldName) && !LAST_NAME.equals(fieldName)
                && !PASSWORD.equals(fieldName) && !FIRST_NAME.equals(fieldName))  {
            throw new InvalidFieldNameException(CUSTOMER_FIELD_ERROR_MESSAGE);
        }
    }

    private String getCustomerOldValue(Long entityId, String fieldName) {
        Customer customer = customerRepository.findById(entityId)
                .orElseThrow(CustomerNotFoundException::new);

        return switch (fieldName) {
            case USERNAME -> customer.getUsername();
            case PASSWORD -> customer.getPassword();
            case FIRST_NAME -> customer.getFirstName();
            case LAST_NAME -> customer.getLastName();
            default -> throw new InvalidFieldNameException(CUSTOMER_FIELD_ERROR_MESSAGE);
        };
    }

    private String getCarOldValue(Long entityId, String fieldName) {
        Car car = carRepository.findById(entityId)
                .orElseThrow(CarNotFoundException::new);

        return switch (fieldName) {
            case BRAND -> car.getBrand();
            case MODEL -> car.getModel();
            case PRICE -> String.valueOf(car.getPrice());
            case LENGTH -> String.valueOf(car.getLength());
            case WIDTH -> String.valueOf(car.getWidth());
            case DATE_OF_PRODUCTION -> String.valueOf(car.getDateOfProduction());
            case FUEL -> String.valueOf(car.getFuel());
            default -> throw new InvalidFieldNameException(CUSTOMER_FIELD_ERROR_MESSAGE);
        };
    }

    private String getParkingOldValue(Long entityId, String fieldName) {
        Parking parking = parkingRepository.findById(entityId)
                .orElseThrow(ParkingNotFoundException::new);

        return switch (fieldName) {
            case PARKING_NAME -> parking.getName();
            case ADRESS -> parking.getAdress();
            case CAPACITY -> String.valueOf(parking.getCapacity());
            case PARKING_TYPE -> String.valueOf(parking.getParkingType());
            case PARKING_SPOT_WIDTH -> String.valueOf(parking.getParkingSpotWidth());
            case PARKING_SPOT_LENGTH -> String.valueOf(parking.getParkingSpotLength());
            case PLACES_FOR_ELECTRIC_CARS -> String.valueOf(parking.getPlacesForElectricCars());
            default -> throw new InvalidFieldNameException(PARKING_FIELD_ERROR_MESSAGE);
        };
    }
}