package com.example.carparkingapi.service;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.config.map.struct.CarMapper;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
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

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final CustomerRepository customerRepository;

    private final ParkingService parkingService;

    private final CustomUserDetailsService customUserDetailsService;

    private final CarMapper carMapper;

    private final Utils utils;

    private static final Logger logger = LogManager.getLogger(CarService.class);

    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream()
                .map(carMapper::carToCarDTO)
                .toList();
    }

    public void save(CarCommand carCommand) {
        Car car = carMapper.carCommandToCar(carCommand);
        car.setCustomer(customerRepository.findCustomerByUsername(customUserDetailsService.getCurrentUsername())
                .orElseThrow(CustomerNotFoundException::new));
        carRepository.save(car);
    }

    public void saveWithoutCustomer(CarCommand carCommand) {
        carRepository.save(carMapper.carCommandToCar(carCommand));
    }

    public void saveBatch(List<CarCommand> carCommands, Customer customer) {
        carRepository.saveAll(carCommands.stream().map(command -> {
            Car car = carMapper.carCommandToCar(command);
            car.setCustomer(customer);
            return car;
        }).toList());
    }

    public void saveBatch(List<CarCommand> carCommands) {
        carRepository.saveAll(carCommands.stream()
                .map(carMapper::carCommandToCar)
                .toList());
    }

    public void delete(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(CarNotFoundException::new);
        try {
            leaveParking(car.getId());
        } catch (CarParkingStatusException e) {
            logger.warn("Attempt to delete a parked car, car left parking before deletion");
        }

        car.getCustomer().getCars().remove(car);
        carRepository.delete(car);
    }

    public void parkCar(Long carId, Long parkingId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

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

    public CarDTO findMostExpensiveCar() {
        return Optional.of(carRepository.findAll())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Car::getPrice))
                .map(carMapper::carToCarDTO)
                .orElseThrow(CarNotFoundException::new);
    }

    public List<CarDTO> findAllCarsByCustomer() {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(
                        customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new NoCarsFoundException(utils.noCarsFoundMessage(null))).stream()
                .map(carMapper::carToCarDTO)
                .toList();
    }

    public CarDTO findMostExpensiveCarForCustomer() {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(
                        customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new CarNotFoundException(utils.noCarsFoundMessage(null)))
                .stream()
                .filter(Objects::nonNull)
                .map(carMapper::carToCarDTO)
                .max(Comparator.comparing(CarDTO::getPrice))
                .orElseThrow(() -> new CarNotFoundException("No cars found"));
    }

    public List<CarDTO> findAllCarsByBrand(String brand) {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new NoCarsFoundException(utils.noCarsFoundMessage(brand)))
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getBrand().equals(brand))
                .map(carMapper::carToCarDTO)
                .toList();
    }

    public CarDTO findMostExpensiveCarByBrand(String brand) {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new CarNotFoundException(utils.noCarsFoundMessage(null)))
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getBrand().equals(brand))
                .map(carMapper::carToCarDTO)
                .max(Comparator.comparing(CarDTO::getPrice))
                .orElseThrow(CarNotFoundException::new);
    }

    public List<CarDTO> findAllCarsByCustomerAndFuel(Fuel fuel) {
        return Optional.ofNullable(carRepository.findAllCarsByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseThrow(() -> new NoCarsFoundException(utils.noCarsFoundMessage(String.valueOf(fuel))))
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getFuel().equals(fuel))
                .map(carMapper::carToCarDTO)
                .toList();
    }
}