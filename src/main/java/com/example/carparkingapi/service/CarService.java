package com.example.carparkingapi.service;

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

import java.util.*;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final CustomerRepository customerRepository;

    private final ParkingService parkingService;

    private final ModelMapper modelMapper;

    private final CustomUserDetailsService customUserDetailsService;

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

    public void delete(Long id) {
        carRepository.delete(carRepository.findById(id).orElseThrow(CarNotFoundException::new));
    }

    public CarDTO parkCar(Long carId, Long parkingId) {
        Car car = findById(carId);

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
        Car car = findById(carId);

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

    public List<CarDTO> findAllCarsByCustomer() {
        return Optional.ofNullable(carRepository
                        .findAllByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
    }

    public Car findMostExpensiveCarForCustomer() {
        return Optional.ofNullable(carRepository.findAllByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(() -> new CarNotFoundException("No cars found"));
    }

    public List<Car> findAllCarsByBrand(String brand) {
        return Optional.ofNullable(carRepository.findAllByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getBrand().equals(brand))
                .toList();
    }

    public Car findMostExpensiveCarByBrand(String brand) {
        return Optional.ofNullable(carRepository.findAllByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getBrand().equals(brand))
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(CarNotFoundException::new);
    }

    public List<Car> findAllCarsByCustomerAndFuel(Fuel fuel) {
        return Optional.ofNullable(carRepository.findAllByCustomerUsername(customUserDetailsService.getCurrentUsername()))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .filter(car -> car.getFuel().equals(fuel))
                .toList();
    }

    public Car findMostExpensiveCar() {
        return Optional.of(carRepository.findAll())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(CarNotFoundException::new);
    }

//    public String carNotFoundMessage(Long id, String value) {
////        z contextu robisz getUsername
//        String str = "No cars found for customer " + customerRepository.findById(id).orElseThrow().getUsername();
//        if (value == null) {
//            return str;
////            nazwy do stalych + zmiana kolejnosci
//        } else if (value.equals("DIESEL") || value.equals("PETROL") || value.equals("ELECTRIC")
//                || value.equals("HYBRID") || value.equals("LPG")) {
//            return str + " and fuel " + value;
//        } else {
//            return str + " and brand " + value;
//        }
//    }
}