package com.example.carparkingapi.service;

import com.example.carparkingapi.domain.Customer;
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

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("USER_NOT_FOUND", username)));
    }

    public void verifyCustomerAccess(Long customerId) {
        String currentUsername = getCurrentUsername();
        Customer customer = customerRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedException("Customer not authenticated"));

        if (!customer.getId().equals(customerId)) {
            throw new AccessDeniedException("Access denied. You can only access your own data.");
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}
