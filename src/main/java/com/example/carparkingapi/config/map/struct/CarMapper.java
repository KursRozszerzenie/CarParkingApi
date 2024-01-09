package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.dto.CarDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarDTO carToCarDTO(Car car);

    @Mapping(target = "parking", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Car carCommandToCar(CarCommand carCommand);
}
