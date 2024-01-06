package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.CarNotFoundException;
import com.example.carparkingapi.exception.CustomerNotFoundException;
import com.example.carparkingapi.exception.InvalidFieldNameException;
import com.example.carparkingapi.exception.ParkingNotFoundException;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.carparkingapi.util.Constants.USERNAME;

@Service
@RequiredArgsConstructor
public class EditService {

    private final CustomerRepository customerRepository;

    private final CarRepository carRepository;

    private final ParkingRepository parkingRepository;

//    porozdzielac
//    stringi z nazwami pol do stalych i pozamieniac kolejnosc warunkow
    public void verifyFieldName(String fieldName, String entityType) {
        switch (entityType) {
            case "customer" -> {
                if (!USERNAME.equals(fieldName) && !fieldName.equals("password") && !fieldName.equals("firstName")
                        && !fieldName.equals("lastName")) {
                    throw new InvalidFieldNameException("Invalid field name choose from " +
                            "username, password, firstName, lastName");
                }
            }
            case "car" -> {
                if (!fieldName.equals("brand") && !fieldName.equals("model") && !fieldName.equals("price")
                        && !fieldName.equals("length") && !fieldName.equals("width") &&
                        !fieldName.equals("dateOfProduction") && !fieldName.equals("fuel")) {
                    throw new InvalidFieldNameException("Invalid field name choose from " +
                            "brand, model, price, length, width, dateOfProduction, fuel");
                }
            }
            case "parking" -> {
                if (!fieldName.equals("name") && !fieldName.equals("adress") && !fieldName.equals("capacity")
                        && !fieldName.equals("parkingType") && !fieldName.equals("parkingSpotWidth")
                        && !fieldName.equals("parkingSpotLength") && !fieldName.equals("placesForElectricCars")) {
                    throw new InvalidFieldNameException("Invalid field name choose from " +
                            "name, address, capacity, parkingType, parkingSpotWidth, parkingSpotLength, placesForElectricCars");
                }
            }
            default -> throw new InvalidFieldNameException("Invalid entity type choose from " +
                    "customer, car, parking");
        }
    }

    public String getOldValue(Long entityId, String entityType, String fieldName) {
        return switch (entityType) {
            case "customer" -> getCustomerOldValue(entityId, fieldName);
            case "car" -> getCarOldValue(entityId, fieldName);
            case "parking" -> getParkingOldValue(entityId, fieldName);
            default -> throw new InvalidFieldNameException("Invalid entity type choose from customer, car, parking");
        };
    }

    private String getCustomerOldValue(Long entityId, String fieldName) {
        Customer customer = customerRepository.findById(entityId)
                .orElseThrow(CustomerNotFoundException::new);

        return switch (fieldName) {
            case "username" -> customer.getUsername();
            case "password" -> customer.getPassword();
            case "firstName" -> customer.getFirstName();
            case "lastName" -> customer.getLastName();
            default -> throw new InvalidFieldNameException("Invalid field name for customer. " +
                    "Choose from username, password, firstName, lastName");
        };
    }

    private String getCarOldValue(Long entityId, String fieldName) {
        Car car = carRepository.findById(entityId)
                .orElseThrow(CarNotFoundException::new);

        return switch (fieldName) {
            case "brand" -> car.getBrand();
            case "model" -> car.getModel();
            case "price" -> String.valueOf(car.getPrice());
            case "length" -> String.valueOf(car.getLength());
            case "width" -> String.valueOf(car.getWidth());
            case "dateOfProduction" -> String.valueOf(car.getDateOfProduction());
            case "fuel" -> String.valueOf(car.getFuel());
            default -> throw new InvalidFieldNameException("Invalid field name for car. " +
                    "Choose from brand, model, price, length, width, dateOfProduction, fuel");
        };
    }

    private String getParkingOldValue(Long entityId, String fieldName) {
        Parking parking = parkingRepository.findById(entityId)
                .orElseThrow(ParkingNotFoundException::new);

        return switch (fieldName) {
            case "name" -> parking.getName();
            case "adress" -> parking.getAdress();
            case "capacity" -> String.valueOf(parking.getCapacity());
            case "parkingType" -> String.valueOf(parking.getParkingType());
            case "parkingSpotWidth" -> String.valueOf(parking.getParkingSpotWidth());
            case "parkingSpotLength" -> String.valueOf(parking.getParkingSpotLength());
            case "placesForElectricCars" -> String.valueOf(parking.getPlacesForElectricCars());
            default -> throw new InvalidFieldNameException("Invalid field name for parking. " +
                    "Choose from name, address, capacity, parkingType");
        };
    }
}