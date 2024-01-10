package com.example.carparkingapi.service;

import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.config.map.struct.CarMapper;
import com.example.carparkingapi.config.map.struct.ParkingMapper;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.dto.ParkingDTO;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.ParkingNotFoundException;
import com.example.carparkingapi.exception.parking.FullParkingException;
import com.example.carparkingapi.exception.parking.LPGNotAllowedException;
import com.example.carparkingapi.exception.parking.NoMoreElectricPlacesException;
import com.example.carparkingapi.exception.parking.ParkingSpaceToSmallException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static com.example.carparkingapi.util.Constants.*;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;

    private final CarRepository carRepository;

    private final ParkingMapper parkingMapper;

    private final CarMapper carMapper;

    public void save(ParkingCommand parkingCommand) {
        Parking parking = parkingMapper.parkingCommandToParking(parkingCommand);
        parking.setTakenPlaces(0);
        parking.setTakenElectricPlaces(0);
        parkingRepository.save(parking);
    }

    public void delete(Long id) {
        Parking parking = findById(id);
        parkingRepository.delete(parking);
    }

    public Page<ParkingDTO> getAllParkings(Pageable pageable) {
        return parkingRepository.findAll(pageable).map(parkingMapper::parkingToParkingDTO);
    }

    public Parking findById(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ParkingNotFoundException(PARKING_NOT_FOUND_ERROR_MESSAGE));
    }

    public Page<CarDTO> findAllCarsFromParking(Long id, Pageable pageable) {
        return carRepository.findAllByParkingId(id, pageable)
                .map(carMapper::carToCarDTO);
    }


    public CarDTO findMostExpensiveCarFromParking(Long id) {
        return findById(id).getCars().stream()
                .max(Comparator.comparing(Car::getPrice))
                .map(carMapper::carToCarDTO)
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND_ERROR_MESSAGE));
    }

    protected void validateParkingSpace(Parking parking, Car car) {
        if (parking.getTakenPlaces() >= parking.getCapacity()) {
            throw new FullParkingException(PARKING_FULL_ERROR_MESSAGE);
        }
        if (car.getLength() > parking.getParkingSpotLength() || car.getWidth() > parking.getParkingSpotWidth()) {
            throw new ParkingSpaceToSmallException(PARKING_SPACE_TO_SMALL_ERROR_MESSAGE);
        }
        if ((Fuel.ELECTRIC.equals(car.getFuel()) && parking.getTakenElectricPlaces() >= parking.getPlacesForElectricCars())) {
            throw new NoMoreElectricPlacesException(NO_MORE_ELECTRIC_PLACES_ERROR_MESSAGE);
        }
        if (Fuel.LPG.equals(car.getFuel()) && ParkingType.UNDERGROUND.equals(parking.getParkingType())) {
            throw new LPGNotAllowedException(PARKING_NOT_ALLOW_LPG_CAR_ERROR_MESSAGE);
        }
    }
}
