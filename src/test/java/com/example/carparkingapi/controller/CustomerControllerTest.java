package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.model.Role;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("application-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerControllerTest {

    //    Poczytaj o @WIthUserDetails nad testem
//    .save
//     mockMvc.perform(get("/api/products").with(user(customUserDetails))

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        prepareCustomersAndCars();
    }

    private void prepareCustomersAndCars() {
        Customer customer = new Customer();
        customer.setFirstName("Jan");
        customer.setLastName("Kowalski");
        customer.setUsername("jan.kowalski@email.com");
        customer.setPassword("password");
        customer.setRole(Role.USER);
        customer.setAccountEnabled(true);
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);

        customerRepository.save(customer);

        Car car1 = new Car();
        car1.setBrand("Mercedes-Benz");
        car1.setModel("c-class");
        car1.setPrice(150000);
        car1.setLength(50);
        car1.setWidth(1);
        car1.setFuel(Fuel.PETROL);
        car1.setCustomer(customerRepository.findCustomerByUsername("jan.kowalski@email.com").orElseThrow());
        car1.setDateOfProduction(LocalDate.of(2023, 10, 10));

        Car car2 = new Car();
        car2.setBrand("BMW");
        car2.setModel("M3");
        car2.setPrice(350000);
        car2.setLength(1);
        car2.setWidth(1);
        car2.setFuel(Fuel.PETROL);
        car2.setCustomer(customerRepository.findCustomerByUsername("jan.kowalski@email.com").orElseThrow());
        car2.setDateOfProduction(LocalDate.of(2023, 10, 10));

        Car car3 = new Car();
        car3.setBrand("Tesla");
        car3.setModel("Model S");
        car3.setPrice(400000);
        car3.setLength(1);
        car3.setWidth(1);
        car3.setFuel(Fuel.ELECTRIC);
        car3.setCustomer(customerRepository.findCustomerByUsername("jan.kowalski@email.com").orElseThrow());
        car3.setDateOfProduction(LocalDate.of(2023, 10, 10));

        Car car4 = new Car();
        car4.setBrand("BMW");
        car4.setModel("M5");
        car4.setPrice(550000);
        car4.setLength(1);
        car4.setWidth(1);
        car4.setFuel(Fuel.PETROL);
        car4.setCustomer(customerRepository.findCustomerByUsername("jan.kowalski@email.com").orElseThrow());
        car4.setDateOfProduction(LocalDate.of(2023, 10, 10));

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);
        carRepository.save(car4);

        Parking parking = new Parking();
        parking.setName("Parking 1");
        parking.setAdress("Address 1");
        parking.setCapacity(10);
        parking.setParkingType(ParkingType.UNDERGROUND);
        parking.setPlacesForElectricCars(2);
        parking.setParkingSpotWidth(20576);
        parking.setParkingSpotLength(30756);

        parkingRepository.save(parking);
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldReturnAllCarsForCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.sort.unsorted").value(false))
                .andExpect(jsonPath("$.content[0].brand").value("Mercedes-Benz"))
                .andExpect(jsonPath("$.content[0].model").value("c-class"))
                .andExpect(jsonPath("$.content[1].brand").value("BMW"))
                .andExpect(jsonPath("$.content[1].model").value("M3"))
                .andExpect(jsonPath("$.content[2].brand").value("Tesla"))
                .andExpect(jsonPath("$.content[2].model").value("Model S"))
                .andExpect(jsonPath("$.content[3].brand").value("BMW"))
                .andExpect(jsonPath("$.content[3].model").value("M5"));
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldReturnAllCarsForCustomerAndBrand() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/all/brand/BMW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.sort.unsorted").value(false))
                .andExpect(jsonPath("$.content[0].brand").value("BMW"))
                .andExpect(jsonPath("$.content[0].model").value("M3"))
                .andExpect(jsonPath("$.content[1].brand").value("BMW"))
                .andExpect(jsonPath("$.content[1].model").value("M5"));
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldReturnAllCarsForCustomerAndFuel() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/all/fuel/PETROL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.sort.unsorted").value(false))
                .andExpect(jsonPath("$.content[0].model").value("c-class"))
                .andExpect(jsonPath("$.content[1].brand").value("BMW"))
                .andExpect(jsonPath("$.content[1].model").value("M3"))
                .andExpect(jsonPath("$.content[2].brand").value("BMW"))
                .andExpect(jsonPath("$.content[2].model").value("M5"));

    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldReturnMostExpensiveCarByCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/most-expensive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.model").value("M5"));
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldReturnMostExpensiveCarByCustomerAndBrand() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/most-expensive/BMW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.model").value("M5"));
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldSaveNewCar() throws Exception {
        CarCommand carCommand = new CarCommand("Audi", "A4", 200000,
                1, 1, Fuel.PETROL, LocalDate.of(2023, 10, 10));

        this.mockMvc.perform(post("/api/v1/customer/cars/save")
                        .content(objectMapper.writeValueAsString(carCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        Optional<Car> savedCar = carRepository
                .findCarByCustomerUsernameAndBrandAndModel("jan.kowalski@email.com", "Audi", "A4");
        assertTrue(savedCar.isPresent());
        assertEquals("Audi", savedCar.get().getBrand());
        assertEquals("A4", savedCar.get().getModel());
        assertEquals(200000, savedCar.get().getPrice());
        assertEquals(1, savedCar.get().getLength());
        assertEquals(1, savedCar.get().getWidth());
        assertEquals(Fuel.PETROL, savedCar.get().getFuel());
        assertEquals(LocalDate.of(2023, 10, 10), savedCar.get().getDateOfProduction());
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldSaveAllCars() throws Exception {
        List<CarCommand> carCommands = Arrays.asList(
                new CarCommand("Audi", "A4", 200000, 4, 2, Fuel.PETROL, LocalDate.of(2023, 10, 10)),
                new CarCommand("BMW", "M4", 300000, 5, 2, Fuel.DIESEL, LocalDate.of(2023, 11, 11))
        );

        this.mockMvc.perform(post("/api/v1/customer/cars/save/batch")
                        .content(objectMapper.writeValueAsString(carCommands))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        Optional<Car> savedCar1 = carRepository
                .findCarByCustomerUsernameAndBrandAndModel("jan.kowalski@email.com", "Audi", "A4");
        assertTrue(savedCar1.isPresent());
        assertEquals("Audi", savedCar1.get().getBrand());
        assertEquals("A4", savedCar1.get().getModel());
        assertEquals(200000, savedCar1.get().getPrice());
        assertEquals(4, savedCar1.get().getLength());
        assertEquals(2, savedCar1.get().getWidth());
        assertEquals(Fuel.PETROL, savedCar1.get().getFuel());
        assertEquals(LocalDate.of(2023, 10, 10), savedCar1.get().getDateOfProduction());

        Optional<Car> savedCar2 = carRepository
                .findCarByCustomerUsernameAndBrandAndModel("jan.kowalski@email.com", "BMW", "M4");
        assertTrue(savedCar2.isPresent());
        assertEquals("BMW", savedCar2.get().getBrand());
        assertEquals("M4", savedCar2.get().getModel());
        assertEquals(300000, savedCar2.get().getPrice());
        assertEquals(5, savedCar2.get().getLength());
        assertEquals(2, savedCar2.get().getWidth());
        assertEquals(Fuel.DIESEL, savedCar2.get().getFuel());
        assertEquals(LocalDate.of(2023, 11, 11), savedCar2.get().getDateOfProduction());
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldDeleteCar() throws Exception {
        mockMvc.perform(delete("/api/v1/customer/cars/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        Optional<Car> deletedCar = carRepository.findById(1L);
        assertTrue(deletedCar.isEmpty());
    }

    @Test
    @Transactional
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldParkCar() throws Exception {
        mockMvc.perform(post("/api/v1/customer/cars/1/park/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<Car> parkedCar = carRepository.findById(1L);
        assertTrue(parkedCar.isPresent());
        assertEquals(1L, parkedCar.get().getParking().getId());
        assertEquals(1, parkedCar.get().getParking().getTakenPlaces());
        assertEquals(0, parkedCar.get().getParking().getTakenElectricPlaces());
    }

    @Test
    @Transactional
    @WithMockUser(username = "jan.kowalski@email.com", roles = "USER")
    void shouldLeaveParking() throws Exception {
        mockMvc.perform(post("/api/v1/customer/cars/1/park/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<Car> parkedCar = carRepository.findById(1L);
        assertTrue(parkedCar.isPresent());
        assertEquals(1L, parkedCar.get().getParking().getId());
        assertEquals(1, parkedCar.get().getParking().getTakenPlaces());
        assertEquals(0, parkedCar.get().getParking().getTakenElectricPlaces());

        mockMvc.perform(post("/api/v1/customer/cars/1/leave")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<Car> leftParkingCar = carRepository.findById(1L);
        assertTrue(leftParkingCar.isPresent());
        assertNull(leftParkingCar.get().getParking());

        Optional<Parking> parking = parkingRepository.findById(1L);
        assertTrue(parking.isPresent());
        assertEquals(0, parking.get().getTakenPlaces());
        assertEquals(0, parking.get().getTakenElectricPlaces());
    }
}
