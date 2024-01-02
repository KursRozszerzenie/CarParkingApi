package com.example.carparkingapi.service;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.exception.CarNotFoundException;
import com.example.carparkingapi.exception.CarParkingStatusException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final CustomerRepository customerRepository;

    private final ParkingService parkingService;

    private final ModelMapper modelMapper;

    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car not found"));
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public List<Car> saveAll(List<Car> cars) {
       return carRepository.saveAll(cars);
    }

    public Car updateCar(Long carId, CarCommand carCommand) {
       Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found"));
        car.setBrand(carCommand.getBrand());
        car.setModel(carCommand.getModel());
        car.setFuel(carCommand.getFuel());
        car.setLength(carCommand.getLength());
        car.setWidth(carCommand.getWidth());
        car.setPrice(carCommand.getPrice());
        car.setDateOfProduction(carCommand.getDateOfProduction());

        return carRepository.save(car);
    }

    public String delete(Long id) {
       carRepository.delete(findById(id));
        return "Car with id " + id + " deleted";
    }

    public CarDTO parkCar(Long carId, Long parkingId) {
        Car car = findById(carId);

        if (car.getParking() != null) {
            throw new CarParkingStatusException("Car is already parked");
        }

        Parking parking = parkingService.findById(parkingId);

        parkingService.validateParkingSpace(parking, car);

        parking.setTakenPlaces(parking.getTakenPlaces() + 1);
        car.setParking(parking);
        if (car.getFuel() == Fuel.ELECTRIC) {
            parking.setTakenElectricPlaces(parking.getTakenElectricPlaces() + 1);
        }

        return modelMapper.map(carRepository.save(car), CarDTO.class);
    }

    public String leaveParking(Long carId) {
        Car car = findById(carId);

        Parking parking = Optional.ofNullable(car.getParking())
                .orElseThrow(() -> new CarParkingStatusException("Car is not parked"));

        parking.setTakenPlaces(parking.getTakenPlaces() - 1);

        if (car.getFuel() == Fuel.ELECTRIC) {
            parking.setTakenElectricPlaces(parking.getTakenElectricPlaces() - 1);
        }

        car.setParking(null);
        carRepository.save(car);

        return "Car with id " + carId + " left parking with id " + parking.getId();
    }

    public List<CarDTO> findAllCarsByCustomerId(Long id) {
        return carRepository.findAllByCustomerId(id)
                .orElseThrow(() -> new CarNotFoundException(carNotFoundMessage(id, null)))
                .stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
    }

    public Car findMostExpensiveCarForCustomer(Long customerId) {
        return carRepository.findByCustomerId(customerId).stream()
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(() -> new CarNotFoundException(carNotFoundMessage(customerId, null)));
    }

    public Car findMostExpensiveCarByBrand(Long customerId, String brand) {
        return carRepository.findAllByCustomerIdAndBrand(customerId, brand)
                .orElseThrow(() -> new CarNotFoundException(carNotFoundMessage(customerId, brand)))
                .stream()
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(() -> new CarNotFoundException(carNotFoundMessage(customerId, brand)));
    }


    public List<Car> findAllCarsByBrand(Long customerId, String brand) {
        return carRepository.findAllByCustomerIdAndBrand(customerId, brand).orElseThrow(
                () -> new CarNotFoundException(carNotFoundMessage(customerId, brand)));
    }

    public List<Car> findAllCarsByFuel(Long customerId, Fuel fuel) {
        return carRepository.findByCustomerIdAndFuel(customerId, fuel).orElseThrow(
                () -> new CarNotFoundException(carNotFoundMessage(customerId, fuel.name())));
    }

    public Car findMostExpensiveCar() {
        return carRepository.findAll().stream()
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(() -> new CarNotFoundException("No cars found"));
    }

    public String carNotFoundMessage(Long id, String value) {
        String str = "No cars found for customer " + customerRepository.findById(id).orElseThrow().getUsername();
        if (value == null) {
            return str;
        } else if (value.equals("DIESEL") || value.equals("PETROL") || value.equals("ELECTRIC")
                || value.equals("HYBRID") || value.equals("LPG")) {
            return str + " and fuel " + value;
        } else {
            return str + " and brand " + value;
        }
    }
}