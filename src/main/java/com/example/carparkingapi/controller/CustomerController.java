package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.config.security.authentication.AuthenticationRequest;
import com.example.carparkingapi.config.security.authentication.AuthenticationResponse;
import com.example.carparkingapi.config.security.authentication.AuthenticationService;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.dto.CarDTO;
import com.example.carparkingapi.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final AuthenticationService service;

    private final CarService carService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid CustomerCommand request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/{id}/cars")
    public ResponseEntity<List<CarDTO>> getCarsByCustomerId(@PathVariable Long id) {
        return new ResponseEntity<>(carService.findAllCarsByCustomerId(id), HttpStatus.OK);
    }
}
