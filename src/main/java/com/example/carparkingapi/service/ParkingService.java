package com.example.carparkingapi.service;

import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.exception.CarNotFoundException;
import com.example.carparkingapi.exception.ParkingNotFoundException;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;

    private final ModelMapper modelMapper;

    public Parking save(Parking parking) {
        parking.setTakenPlaces(0);
        parking.setTakenElectricPlaces(0);
        return parkingRepository.save(parking);
    }

    public Parking updateParking(Long parkingId, ParkingCommand parkingCommand) {
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new EntityNotFoundException("Parking not found"));
        parking.setName(parkingCommand.getName());
        parking.setAdress(parkingCommand.getAdress());
        parking.setCapacity(parkingCommand.getCapacity());
        parking.setParkingType(parkingCommand.getParkingType());
        parking.setParkingSpotWidth(parkingCommand.getParkingSpotWidth());
        parking.setParkingSpotLength(parkingCommand.getParkingSpotLength());
        parking.setPlacesForElectricCars(parkingCommand.getPlacesForElectricCars());

        return parkingRepository.save(parking);
    }

    public List<Parking> findAll() {
        return parkingRepository.findAll();
    }

    public Parking findById(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ParkingNotFoundException("Parking not found"));
    }

    public void delete(Long id) {
        Parking parking = findById(id);
        parkingRepository.delete(parking);
    }

    public List<CarDTO> findAllCarsFromParking(Long id) {
        return findById(id).getCars().stream()
                .map(car -> modelMapper.map(car, CarDTO.class))
                .toList();
    }

    public int countAllCarsFromParking(Long id) {
        return findById(id).getCars().size();
    }

    public CarDTO findMostExpensiveCarFromParking(Long id) {
        return modelMapper.map(findById(id).getCars().stream()
                .max(Comparator.comparing(Car::getPrice))
                .orElseThrow(() -> new CarNotFoundException("No cars found")), CarDTO.class);
    }
}
