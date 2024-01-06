package com.example.carparkingapi.util;

import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.exception.security.InvalidCredentialsException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Component
public class Utils {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomerRepository customerRepository;

    public String noCarsFoundMessage(String value) {
        String str = "No cars found for customer " + customerRepository
                .findCustomerByUsername(customUserDetailsService.getCurrentUsername()).orElseThrow().getUsername();
        if (value == null) {
            return str;
        } else if (Fuel.DIESEL.toString().equals(value) || Fuel.PETROL.toString().equals(value)
                || Fuel.LPG.toString().equals(value) || Fuel.ELECTRIC.toString().equals(value)
            || Fuel.HYBRID.toString().equals(value)) {
            return str + " and fuel " + value;
        } else {
            return str + " and brand " + value;
        }
    }
}
