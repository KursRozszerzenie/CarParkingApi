package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.AdminCommand;
import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.config.map.struct.CustomerMapper;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.model.AuthenticationRequest;
import com.example.carparkingapi.model.AuthenticationResponse;
import com.example.carparkingapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    private final CustomerMapper customerMapper;

    @PostMapping("/customer/register")
    public ResponseEntity<CustomerDTO> registerCustomer(@RequestBody @Valid CustomerCommand request) {
        return new ResponseEntity<>(customerMapper.customerToCustomerDTO(
                authService.registerCustomer(request)), HttpStatus.CREATED);
    }

    @PostMapping("/customer/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateCustomer(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticateCustomer(request));
    }

    @PostMapping("/admin/register")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody @Valid AdminCommand request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAdmin(request));
    }

    @PostMapping("/admin/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateAdmin(@RequestBody AdminCommand request) {
        return ResponseEntity.ok(authService.authenticateAdmin(request));
    }
}
