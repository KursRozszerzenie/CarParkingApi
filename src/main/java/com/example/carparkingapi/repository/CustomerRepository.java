package com.example.carparkingapi.repository;

import com.example.carparkingapi.domain.Customer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByUsername(String username);

    @NotNull Page<Customer> findAll(@NotNull Pageable pageable);
}

