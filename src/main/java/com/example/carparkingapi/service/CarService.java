package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.exception.not.found.NoCarsFoundException;
import com.example.carparkingapi.exception.parking.action.CarParkingStatusException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.util.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final CustomerRepository customerRepository;

    private final ParkingService parkingService;

    private final CustomUserDetailsService customUserDetailsService;

    private final Utils utils;

    private static final Logger logger = LogManager.getLogger(CarService.class);

    public void save(Car car) {
        car.setCustomer(customerRepository.findCustomerByUsername(customUserDetailsService.getCurrentUsername())
                .orElseThrow(CustomerNotFoundException::new));
        carRepository.save(car);
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(CarNotFoundException::new);
        try {
            leaveParking(car.getId());
        } catch (CarParkingStatusException e) {
            logger.warn("Attempt to delete a parked car, car left parking before deletion");
        }

        car.getCustomer().getCars().remove(car);
        carRepository.delete(car);
    }

    public void parkCar(Long carId, Long parkingId) {
        Car car = carRepository.findById(carId).orElseThrow(CarNotFoundException::new);

        if (Objects.nonNull(car.getParking())) {
            throw new CarParkingStatusException("Car is already parked");
        }

        Parking parking = parkingService.findById(parkingId);

        parkingService.validateParkingSpace(parking, car);

        parking.setTakenPlaces(parking.getTakenPlaces() + 1);
        car.setParking(parking);
        if (Objects.equals(Fuel.ELECTRIC, car.getFuel())) {
            parking.setTakenElectricPlaces(parking.getTakenElectricPlaces() + 1);
        }

        carRepository.save(car);
    }

    public void leaveParking(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(CarNotFoundException::new);

        Parking parking = Optional.ofNullable(car.getParking())
                .orElseThrow(() -> new CarParkingStatusException("Car is not parked"));

        parking.setTakenPlaces(parking.getTakenPlaces() - 1);

        if (Objects.equals(Fuel.ELECTRIC, car.getFuel())) {
            parking.setTakenElectricPlaces(parking.getTakenElectricPlaces() - 1);
        }

        car.setParking(null);
        carRepository.save(car);
    }

    public Car findMostExpensiveCar() {
        return Optional.of(carRepository.findAll())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(CarNotFoundException::new);
    }

    public Page<Car> findAllCarsByCustomer(Pageable pageable) {
        return Optional.ofNullable(
                        carRepository.findAllCarsByCustomerUsername(
                                customUserDetailsService.getCurrentUsername(), pageable))
                .orElseThrow(() -> new NoCarsFoundException(utils.noCarsFoundMessage(null)));
    }

    public Car findMostExpensiveCarForCustomer() {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new CarNotFoundException(utils.noCarsFoundMessage(null)))
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(() -> new CarNotFoundException("No cars found"));
    }

    public List<Car> findAllCarsByBrand(String brand) {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new NoCarsFoundException(utils.noCarsFoundMessage(brand)))
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getBrand().equals(brand))
                .toList();
    }

    public Car findMostExpensiveCarByBrand(String brand) {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new CarNotFoundException(utils.noCarsFoundMessage(null)))
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getBrand().equals(brand))
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(CarNotFoundException::new);
    }

    public List<Car> findAllCarsByCustomerAndFuel(Fuel fuel) {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new NoCarsFoundException(utils.noCarsFoundMessage(String.valueOf(fuel))))
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getFuel().equals(fuel))
                .toList();
    }
}