package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.exception.InvalidCredentialsException;
import com.example.carparkingapi.exception.UserNotAuthenticatedException;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customerOptional = customerRepository.findByUsername(username);
        if (customerOptional.isPresent()) {
            return customerOptional.get();
        }

        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isPresent()) {
            return adminOptional.get();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    public void verifyCustomerAccess() {
        verifyCustomerAccount(customerRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Customer not authenticated")));
    }

    private void verifyCustomerAccount(Customer customer) {
        if (!customer.isAccountEnabled()) {
            throw new InvalidCredentialsException("Account not enabled");
        }
        if (!customer.isAccountNonExpired()) {
            throw new InvalidCredentialsException("Account expired");
        }
        if (!customer.isAccountNonLocked()) {
            throw new InvalidCredentialsException("Account locked");
        }
        if (!customer.isCredentialsNonExpired()) {
            throw new InvalidCredentialsException("Credentials expired");
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        throw new UserNotAuthenticatedException("User not authenticated");
    }

}