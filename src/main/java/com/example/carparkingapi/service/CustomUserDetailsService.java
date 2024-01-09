package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.exception.not.found.UserNotFoundException;
import com.example.carparkingapi.exception.security.InvalidCredentialsException;
import com.example.carparkingapi.exception.security.UserNotAuthenticatedException;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.carparkingapi.util.Constants.CUSTOMER_NOT_AUTHORIZED_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    private final AdminRepository adminRepository;

    public void verifyCustomerAccess() {
        verifyCustomerAccount(customerRepository.findCustomerByUsername(getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException(CUSTOMER_NOT_AUTHORIZED_ERROR_MESSAGE)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Customer> customerOptional = customerRepository.findCustomerByUsername(username);
        if (customerOptional.isPresent()) {
            return customerOptional.get();
        }

        Optional<Admin> adminOptional = adminRepository.findAdminByUsername(username);
        if (adminOptional.isPresent()) {
            return adminOptional.get();
        }

//        dodac messgae
        throw new UserNotFoundException();
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