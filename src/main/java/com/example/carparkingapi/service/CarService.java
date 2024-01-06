package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.parking.action.CarParkingStatusException;
import com.example.carparkingapi.exception.not.found.NoCarsFoundException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.util.Utils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final ParkingService parkingService;

    private final ModelMapper modelMapper;

    private final CustomUserDetailsService customUserDetailsService;

    private final Utils utils;

    public CarDTO parkCar(Long carId, Long parkingId) {
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

        return modelMapper.map(carRepository.save(car), CarDTO.class);
    }

    public String leaveParking(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(CarNotFoundException::new);

        Parking parking = Optional.ofNullable(car.getParking())
                .orElseThrow(() -> new CarParkingStatusException("Car is not parked"));

        parking.setTakenPlaces(parking.getTakenPlaces() - 1);

        if (Objects.equals(Fuel.ELECTRIC, car.getFuel())) {
            parking.setTakenElectricPlaces(parking.getTakenElectricPlaces() - 1);
        }

        car.setParking(null);
        carRepository.save(car);

        return "Car with id " + carId + " left parking with id " + parking.getId();
    }

    public Car findMostExpensiveCar() {
        return Optional.of(carRepository.findAll())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(CarNotFoundException::new);
    }

    public List<CarDTO> findAllCarsByCustomer() {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new NoCarsFoundException(utils.noCarsFoundMessage(null)))
                .stream()
                .filter(Objects::nonNull)
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
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