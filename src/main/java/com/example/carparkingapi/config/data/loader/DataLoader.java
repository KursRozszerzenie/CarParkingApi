package com.example.carparkingapi.config.data.loader;

import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.model.Role;
import com.example.carparkingapi.repository.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        loadCustomers();
    }

    private void loadCustomers() {
        Faker faker = new Faker();

        for (int i = 0; i < 100; i++) {
            Customer customer = new Customer();
            customer.setFirstName(faker.name().firstName());
            customer.setLastName(faker.name().lastName());
            customer.setUsername(faker.internet().emailAddress());
            customer.setPassword(passwordEncoder.encode("password"));
            customer.setRole(Role.USER);
            customer.setAccountEnabled(true);
            customer.setAccountNonExpired(true);
            customer.setAccountNonLocked(true);
            customer.setCredentialsNonExpired(true);

            customerRepository.save(customer);
        }
    }
}

