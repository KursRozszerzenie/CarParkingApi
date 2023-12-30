package com.example.carparkingapi.config.security.authentication;

import com.example.carparkingapi.config.security.jwt.JwtService;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.model.Role;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomerRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        repository.save(customer);

        return AuthenticationResponse
                .builder()
                .token(jwtService.generateToken(customer))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Customer customer = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return AuthenticationResponse
                .builder()
                .token(jwtService.generateToken(customer))
                .build();
    }
}