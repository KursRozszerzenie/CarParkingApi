package com.example.carparkingapi.util;

import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Utils {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomerRepository customerRepository;

    public String noCarsFoundMessage(String value) {
        String str = "No cars found for customer " + customerRepository
                .findCustomerByUsername(customUserDetailsService.getCurrentUsername())
                .orElseThrow()//dodaj error
                .getUsername();
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
