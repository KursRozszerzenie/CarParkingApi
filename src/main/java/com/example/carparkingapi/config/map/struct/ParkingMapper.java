package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.dto.ParkingDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParkingMapper {
    ParkingDTO parkingToParkingDTO(Parking parking);
    Parking parkingCommandToParking(ParkingCommand parkingCommand);
}
