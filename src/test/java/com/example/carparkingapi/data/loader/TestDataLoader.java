package com.example.carparkingapi.data.loader;

import com.example.carparkingapi.command.AdminCommand;
import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.command.CustomerCommand;
import com.example.carparkingapi.command.ParkingCommand;
import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.model.Role;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataLoader {

    private static final String E_MAIL = "jan.kowalski@email.com";

    private final CustomerRepository customerRepository;

    private final CarRepository carRepository;

    private final ParkingRepository parkingRepository;

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    public Customer createCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Jan");
        customer.setLastName("Kowalski");
        customer.setUsername(E_MAIL);
        customer.setPassword(passwordEncoder.encode("password"));
        customer.setRole(Role.USER);
        customer.setAccountEnabled(true);
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);

        return customerRepository.save(customer);
    }

    public Admin createAdmin() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("adminPassword"));
        admin.setRole(Role.ADMIN);

        return adminRepository.save(admin);
    }

    public AdminCommand createAdminCommand() {
        return new AdminCommand("admin", "password");
    }

    public CustomerCommand createCustomerCommand() {
        return new CustomerCommand("Jan", "Kowalski", E_MAIL, "password");
    }

    public ParkingCommand createParkingCommand(){
        return new ParkingCommand("Parking ABC", "Wapienna",
                10, ParkingType.UNDERGROUND, 10, 10, 10);
    }

    public CarCommand createCarCommand() {
        return new CarCommand("Audi", "A4", 200000,
                1, 1, Fuel.PETROL, LocalDate.of(2023, 10, 10));
    }

    public List<CarCommand> createCarCommands() {
        return Arrays.asList(
                new CarCommand("Audi", "A4", 200000, 1, 1, Fuel.DIESEL, LocalDate.of(2023, 10, 10)),
                new CarCommand("BMW", "M4", 300000, 1, 1, Fuel.PETROL, LocalDate.of(2023, 11, 11))
        );
    }

    public void createCustomersCarsAndParkings() {
        Customer customer = new Customer();
        customer.setFirstName("Jan");
        customer.setLastName("Kowalski");
        customer.setUsername(E_MAIL);
        customer.setPassword("customerPassword");
        customer.setRole(Role.USER);
        customer.setAccountEnabled(true);
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);

        Customer customer2 = new Customer();
        customer2.setFirstName("Michal");
        customer2.setLastName("Nowak");
        customer2.setUsername("Michal.Nowak@email.com");
        customer2.setPassword("password");
        customer2.setRole(Role.USER);
        customer2.setAccountEnabled(true);
        customer2.setAccountNonExpired(true);
        customer2.setAccountNonLocked(true);
        customer2.setCredentialsNonExpired(true);

        customerRepository.save(customer);
        customerRepository.save(customer2);

        Car car1 = new Car();
        car1.setBrand("Mercedes-Benz");
        car1.setModel("c-class");
        car1.setPrice(150000);
        car1.setLength(50);
        car1.setWidth(1);
        car1.setFuel(Fuel.PETROL);
        car1.setCustomer(customerRepository.findCustomerByUsername(E_MAIL).orElseThrow());
        car1.setDateOfProduction(LocalDate.of(2023, 10, 10));

        Car car2 = new Car();
        car2.setBrand("BMW");
        car2.setModel("M3");
        car2.setPrice(350000);
        car2.setLength(1);
        car2.setWidth(1);
        car2.setFuel(Fuel.PETROL);
        car2.setCustomer(customerRepository.findCustomerByUsername(E_MAIL).orElseThrow());
        car2.setDateOfProduction(LocalDate.of(2023, 10, 10));

        Car car3 = new Car();
        car3.setBrand("Tesla");
        car3.setModel("Model S");
        car3.setPrice(400000);
        car3.setLength(1);
        car3.setWidth(1);
        car3.setFuel(Fuel.ELECTRIC);
        car3.setCustomer(customerRepository.findCustomerByUsername(E_MAIL).orElseThrow());
        car3.setDateOfProduction(LocalDate.of(2023, 10, 10));

        Car car4 = new Car();
        car4.setBrand("BMW");
        car4.setModel("M5");
        car4.setPrice(550000);
        car4.setLength(1);
        car4.setWidth(1);
        car4.setFuel(Fuel.PETROL);
        car4.setCustomer(customerRepository.findCustomerByUsername(E_MAIL).orElseThrow());
        car4.setDateOfProduction(LocalDate.of(2023, 10, 10));

        Parking parking1 = new Parking();
        parking1.setName("Parking 1");
        parking1.setAdress("Address 1");
        parking1.setCapacity(10);
        parking1.setParkingType(ParkingType.UNDERGROUND);
        parking1.setPlacesForElectricCars(2);
        parking1.setParkingSpotWidth(20576);
        parking1.setParkingSpotLength(30756);

        Parking parking2 = new Parking();
        parking2.setName("Parking 2");
        parking2.setAdress("Address 2");
        parking2.setCapacity(10);
        parking2.setParkingType(ParkingType.UNDERGROUND);
        parking2.setPlacesForElectricCars(2);
        parking2.setParkingSpotWidth(20576);
        parking2.setParkingSpotLength(30756);


        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);
        carRepository.save(car4);
        parkingRepository.save(parking1);
        parkingRepository.save(parking2);
    }
}
