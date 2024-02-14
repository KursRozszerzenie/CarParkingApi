package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.CarCommand;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.ParkingNotFoundException;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.repository.CarRepository;
import com.example.carparkingapi.repository.ParkingRepository;
import com.example.carparkingapi.data.loader.TestDataLoader;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private TestDataLoader testDataLoader;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        testDataLoader.createCustomersCarsAndParkings();
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldReturnAllCarsForCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
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
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldReturnAllCarsForCustomerAndBrand() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/all/brand/BMW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.content[0].brand").value("BMW"))
                .andExpect(jsonPath("$.content[0].model").value("M3"))
                .andExpect(jsonPath("$.content[1].brand").value("BMW"))
                .andExpect(jsonPath("$.content[1].model").value("M5"));
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldReturnAllCarsForCustomerAndFuel() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/all/fuel/PETROL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.content[0].model").value("c-class"))
                .andExpect(jsonPath("$.content[1].brand").value("BMW"))
                .andExpect(jsonPath("$.content[1].model").value("M3"))
                .andExpect(jsonPath("$.content[2].brand").value("BMW"))
                .andExpect(jsonPath("$.content[2].model").value("M5"));

    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldReturnMostExpensiveCarByCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/most-expensive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.model").value("M5"));
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldReturnMostExpensiveCarByCustomerAndBrand() throws Exception {
        mockMvc.perform(get("/api/v1/customer/cars/most-expensive/BMW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.model").value("M5"));
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldSaveNewCar() throws Exception {
        CarCommand carCommand = new CarCommand("Audi", "A4", 200000,
                1, 1, Fuel.PETROL, LocalDate.of(2023, 10, 10));

        this.mockMvc.perform(post("/api/v1/customer/cars/save")
                        .content(objectMapper.writeValueAsString(carCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());


        Car savedCar = carRepository.findCarByCustomerUsernameAndBrandAndModel("jan.kowalski@email.com", "Audi",
                "A4").orElseThrow(CarNotFoundException::new);
        assertEquals(5, carRepository.findAll().size());
        assertEquals("Audi", savedCar.getBrand());
        assertEquals("A4", savedCar.getModel());
        assertEquals(200000, savedCar.getPrice());
        assertEquals(1, savedCar.getLength());
        assertEquals(1, savedCar.getWidth());
        assertEquals(Fuel.PETROL, savedCar.getFuel());
        assertEquals(LocalDate.of(2023, 10, 10), savedCar.getDateOfProduction());
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldSaveAllCars() throws Exception {
        List<CarCommand> carCommands = Arrays.asList(
                new CarCommand("Audi", "A4", 200000, 1, 1, Fuel.DIESEL, LocalDate.of(2023, 10, 10)),
                new CarCommand("BMW", "M4", 300000, 1, 1, Fuel.PETROL, LocalDate.of(2023, 11, 11))
        );

        this.mockMvc.perform(post("/api/v1/customer/cars/save/batch")
                        .content(objectMapper.writeValueAsString(carCommands))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        Car savedCar1 = carRepository.findCarByCustomerUsernameAndBrandAndModel("jan.kowalski@email.com", "Audi",
                "A4").orElseThrow(CarNotFoundException::new);
        assertEquals("Audi", savedCar1.getBrand());
        assertEquals("A4", savedCar1.getModel());
        assertEquals(200000, savedCar1.getPrice());
        assertEquals(1, savedCar1.getLength());
        assertEquals(1, savedCar1.getWidth());
        assertEquals(Fuel.DIESEL, savedCar1.getFuel());
        assertEquals(LocalDate.of(2023, 10, 10), savedCar1.getDateOfProduction());


        Car savedCar2 = carRepository.findCarByCustomerUsernameAndBrandAndModel("jan.kowalski@email.com", "BMW",
                "M4").orElseThrow(CarNotFoundException::new);
        assertEquals("BMW", savedCar2.getBrand());
        assertEquals("M4", savedCar2.getModel());
        assertEquals(300000, savedCar2.getPrice());
        assertEquals(1, savedCar2.getLength());
        assertEquals(1, savedCar2.getWidth());
        assertEquals(Fuel.PETROL, savedCar2.getFuel());
        assertEquals(LocalDate.of(2023, 11, 11), savedCar2.getDateOfProduction());
    }

    @Test
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
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
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldParkCar() throws Exception {
        mockMvc.perform(post("/api/v1/customer/cars/1/park/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Car parkedCar = carRepository.findById(1L)
                .orElseThrow(ParkingNotFoundException::new);
        assertEquals(1L, parkedCar.getParking().getId());
        assertEquals(1, parkedCar.getParking().getTakenPlaces());
        assertEquals(0, parkedCar.getParking().getTakenElectricPlaces());
    }

    @Test
    @Transactional
    @WithMockUser(username = "jan.kowalski@email.com", password = "customerPassword", roles = "USER")
    void shouldLeaveParking() throws Exception {
        mockMvc.perform(post("/api/v1/customer/cars/1/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/customer/cars/1/leave")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Car leftParkingCar = carRepository.findById(1L)
                .orElseThrow(CarNotFoundException::new);
        assertNull(leftParkingCar.getParking());

        Parking parking = parkingRepository.findById(1L)
                .orElseThrow(ParkingNotFoundException::new);
        assertEquals(0, parking.getTakenPlaces());
        assertEquals(0, parking.getTakenElectricPlaces());
    }
}
