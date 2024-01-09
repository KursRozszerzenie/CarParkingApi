package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.ParkingNotFoundException;
import com.example.carparkingapi.exception.parking.action.FullParkingException;
import com.example.carparkingapi.exception.parking.action.LPGNotAllowedException;
import com.example.carparkingapi.exception.parking.action.NoMoreElectricPlacesException;
import com.example.carparkingapi.exception.parking.action.ParkingSpaceToSmallException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public Parking save(Parking parking) {
        parking.setTakenPlaces(0);
        parking.setTakenElectricPlaces(0);
        return parkingRepository.save(parking);
    }

    public String delete(Long id) {
        Parking parking = findById(id);
        parkingRepository.delete(parking);
        return "Parking with id " + id + " deleted";
    }

    public Parking findById(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ParkingNotFoundException("Parking not found"));
    }

    public List<Car> findAllCarsFromParking(Long id) {
        return findById(id).getCars().stream()
                .toList();
    }

    public Car findMostExpensiveCarFromParking(Long id) {
        return findById(id).getCars().stream()
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(() -> new CarNotFoundException("No cars found"));
    }

//    to do zmiany .equals
    protected void validateParkingSpace(Parking parking, Car car) {
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
}
