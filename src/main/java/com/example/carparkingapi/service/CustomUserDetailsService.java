package com.example.carparkingapi.service;

import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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

    public void verifyCustomerAccess(Long customerId) {
        if (!customerRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Customer not authenticated"))
                .getId()
                .equals(customerId)) {
                    throw new AccessDeniedException("Access denied. You can only access your own data.");
        }
    }

    public void verifyAdminAccess(Long adminId) {
        if (!adminRepository.findByUsername(getCurrentUsername())
            .orElseThrow(() -> new AccessDeniedException("admin not authenticated"))
            .getId().
            equals(adminId)) {
                throw new AccessDeniedException("Access denied");
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        throw new AccessDeniedException("User not authenticated");
    }

    public Customer updateCustomer(Long customerId, CustomerCommand customerCommand) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        customer.setFirstName(customerCommand.getFirstName());
        customer.setLastName(customerCommand.getLastName());
        customer.setUsername(customerCommand.getUsername());
        customer.setPassword(customerCommand.getPassword());

        return customerRepository.save(customer);
    }

}
