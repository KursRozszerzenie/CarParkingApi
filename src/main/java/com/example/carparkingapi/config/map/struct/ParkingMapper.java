package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.ParkingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParkingMapper {

    ParkingDTO parkingToParkingDTO(Parking parking);

    @Mapping(target = "takenPlaces", ignore = true)
    @Mapping(target = "takenElectricPlaces", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cars", ignore = true)
    Parking parkingCommandToParking(ParkingCommand parkingCommand);
}
