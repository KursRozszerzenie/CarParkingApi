package com.example.carparkingapi.controller;

import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CarService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/car")
@RestController
@RequiredArgsConstructor
public class CarController {

    private final ModelMapper modelMapper;

    private final CarService carService;

    private final CustomerRepository customerRepository;


}
