package com.example.carparkingapi.service;

import com.example.carparkingapi.command.AdminCommand;
import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.config.map.struct.AdminMapper;
import com.example.carparkingapi.config.map.struct.CustomerMapper;
import com.example.carparkingapi.config.security.jwt.JwtService;
import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.dto.AdminDTO;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.exception.not.found.AdminNotFoundException;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.exception.security.InvalidCredentialsException;
import com.example.carparkingapi.model.AuthenticationRequest;
import com.example.carparkingapi.model.AuthenticationResponse;
import com.example.carparkingapi.model.Role;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomerRepository customerRepository;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final AdminMapper adminMapper;

    private final CustomerMapper customerMapper;

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    public CustomerDTO registerCustomer(CustomerCommand request) {
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setUsername(request.getUsername());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setRole(Role.USER);
        customer.setAccountEnabled(true);
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);


        return customerMapper.customerToCustomerDTO(customerRepository.save(customer));
    }

    public AuthenticationResponse authenticateCustomer(AuthenticationRequest request) {
        Customer customer = customerRepository.findCustomerByUsername(request.getUsername())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            logger.error(e.getMessage(), e);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return new AuthenticationResponse(jwtService.generateToken(customer));
    }

    public AdminDTO registerAdmin(AdminCommand request) {
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));

        return adminMapper.adminToAdminDTO(adminRepository.save(admin));
    }


    public AuthenticationResponse authenticateAdmin(AuthenticationRequest request) {
        Admin admin = adminRepository.findAdminByUsername(request.getUsername())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            logger.error(e.getMessage(), e);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return new AuthenticationResponse(jwtService.generateToken(admin));
    }
}