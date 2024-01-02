package com.example.carparkingapi.service;

import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.exception.CustomerNotFoundException;
import com.example.carparkingapi.exception.InvalidCredentialsException;
import com.example.carparkingapi.exception.UserNotAuthenticatedException;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ActionService actionService;

    private final AdminRepository adminRepository;

    private final CustomerRepository customerRepository;

    public void verifyAndLog(Long adminId, ActionType actionType) {
        verifyAdminAccess(adminId);
        actionService.logAction(actionType);
    }

    public void verifyAdminAccess(Long adminId) {
        if (!adminRepository.findByUsername(getCurrentUsername())
                .orElseThrow(InvalidCredentialsException::new)
                .getId().
                equals(adminId)) {
            throw new InvalidCredentialsException();
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        throw new UserNotAuthenticatedException();
    }

    public Customer lockCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountNonLocked(false);

        return customerRepository.save(customer);
    }

    public Customer unlockCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountNonLocked(true);

        return customerRepository.save(customer);
    }

    public Customer enableCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountEnabled(true);

        return customerRepository.save(customer);
    }

    public Customer disableCustomerAccount(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setAccountEnabled(false);

        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long customerId, CustomerCommand customerCommand) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setFirstName(customerCommand.getFirstName());
        customer.setLastName(customerCommand.getLastName());
        customer.setUsername(customerCommand.getUsername());
        customer.setPassword(customerCommand.getPassword());

        return customerRepository.save(customer);
    }
}
