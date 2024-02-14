package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.AdminCommand;
import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.dto.AdminDTO;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.model.AuthenticationRequest;
import com.example.carparkingapi.model.AuthenticationResponse;
import com.example.carparkingapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication controller", description = "Benocno")
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Operation(summary = "Register a customer", description = "The endpoint through which we can register a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got the token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDTO.class))})
    })
    @PostMapping("/customer/register")
    public ResponseEntity<CustomerDTO> registerCustomer(@RequestBody @Valid CustomerCommand request) {
        return new ResponseEntity<>(authService.registerCustomer(request), HttpStatus.CREATED);
    }


    @Operation(summary = "Get authentication token", description = "The endpoint through which we can get a new auth token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got the token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))})
    })
    @PostMapping("/customer/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateCustomer(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticateCustomer(request));
    }


    @Operation(summary = "register admin", description = "The endpoint through which we can register a new admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got the token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminDTO.class))})
    })
    @PostMapping("/admin/register")
    public ResponseEntity<AdminDTO> registerAdmin(@RequestBody @Valid AdminCommand request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAdmin(request));
    }


    @Operation(summary = "Get authentication token", description = "The endpoint through which we can get a new auth token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got the token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))})
    })
    @PostMapping("/admin/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateAdmin(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticateAdmin(request));
    }
}
