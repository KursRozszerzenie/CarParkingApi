package com.example.carparkingapi.config.map.struct;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.dto.CarDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarDTO carToCarDTO(Car car);
    Car carCommandToCar(CarCommand carCommand);
}
