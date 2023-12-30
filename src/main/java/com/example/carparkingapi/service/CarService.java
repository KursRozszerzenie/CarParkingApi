package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.exception.*;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final ParkingService parkingService;

    private final ModelMapper modelMapper;

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car not found"));
    }

    public void delete(Long id) {
        Car car = findById(id);
        carRepository.delete(car);
    }

    public List<CarDTO> findAllCarsByCustomerId(Long id) {
        return carRepository.findAllByCustomerId(id)
                .orElseThrow(() -> new CarNotFoundException("No cars found"))
                .stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
    }

    public List<Car> saveAll(List<Car> cars) {
        return carRepository.saveAll(cars);
    }

    public CarDTO parkCar(Long carId, Long parkingId, String currentUsername) {
        Car car = findById(carId);

        if (!car.getCustomer().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        if (car.getParking() != null) {
            throw new CarParkingStatusException("Car is already parked");
        }

        Parking parking = parkingService.findById(parkingId);

        validateParkingSpace(parking, car);

        parking.setTakenPlaces(parking.getTakenPlaces() + 1);
        car.setParking(parking);
        if (car.getFuel() == Fuel.ELECTRIC) {
            parking.setTakenElectricPlaces(parking.getTakenElectricPlaces() + 1);
        }

        return modelMapper.map(carRepository.save(car), CarDTO.class);
    }


    public void leaveParking(Long carId, String currentUsername) {
        Car car = findById(carId);

        if (!car.getCustomer().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        Parking parking = Optional.ofNullable(car.getParking())
                .orElseThrow(() -> new CarParkingStatusException("Car is not parked"));

        parking.setTakenPlaces(parking.getTakenPlaces() - 1);

        if (car.getFuel() == Fuel.ELECTRIC) {
            parking.setTakenElectricPlaces(parking.getTakenElectricPlaces() - 1);
        }

        car.setParking(null);
        carRepository.save(car);
    }


    private void validateParkingSpace(Parking parking, Car car) {
        if (parking.getTakenPlaces() >= parking.getCapacity()) {
            throw new FullParkingException("Parking is already full");
        }
        if (car.getLength() > parking.getParkingSpotLength() || car.getWidth() > parking.getParkingSpotWidth()) {
            throw new ParkingSpaceToSmallException("Parking space is too small for this car");
        }
        if (car.getFuel() == Fuel.ELECTRIC && parking.getTakenElectricPlaces() >= parking.getPlacesForElectricCars()) {
            throw new NoMoreElectricPlacesException("This parking has no more electric places");
        }
        if (car.getFuel().equals(Fuel.LPG) && parking.getParkingType().equals(ParkingType.UNDERGROUND)) {
            throw new LPGNotAllowedException("This parking does not allow LPG cars");
        }
    }

    public CarDTO findMostExpensiveCar() {
        return carRepository.findAll().stream()
                .max(Comparator.comparing(Car::getPrice))
                .map(car -> modelMapper.map(car, CarDTO.class))
                .orElseThrow(() -> new CarNotFoundException("No cars found"));
    }

    public CarDTO findMostExpensiveCarByBrand(String brand) {
        return carRepository.findAll().stream()
                .filter(car -> car.getBrand().equals(brand))
                .max(Comparator.comparing(Car::getPrice))
                .map(car -> modelMapper.map(car, CarDTO.class))
                .orElseThrow(() -> new CarNotFoundException("No cars found"));
    }

    public List<CarDTO> findAllCarsByBrand(String brand) {
        return carRepository.findAll().stream()
                .filter(car -> car.getBrand().equals(brand))
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
    }

    public List<CarDTO> findAllCarsByFuel(Fuel fuel) {
        return carRepository.findAll().stream()
                .filter(car -> car.getFuel().equals(fuel))
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
    }
}
