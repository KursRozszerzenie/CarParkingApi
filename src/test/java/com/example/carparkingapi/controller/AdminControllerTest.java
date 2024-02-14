package com.example.carparkingapi.controller;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.action.edit.action.EditAction;
import com.example.carparkingapi.command.EditCommand;
import com.example.carparkingapi.data.loader.TestDataLoader;
import com.example.carparkingapi.domain.Car;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.domain.Parking;
import com.example.carparkingapi.exception.not.found.CarNotFoundException;
import com.example.carparkingapi.exception.not.found.CustomerNotFoundException;
import com.example.carparkingapi.exception.not.found.ParkingNotFoundException;
import com.example.carparkingapi.model.ActionType;
import com.example.carparkingapi.model.Fuel;
import com.example.carparkingapi.model.ParkingType;
import com.example.carparkingapi.repository.ActionRepository;
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

import static com.example.carparkingapi.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
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
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataLoader testDataLoader;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        testDataLoader.createCustomersCarsAndParkings();
        testDataLoader.createAdmin();
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldUpdateCustomer() throws Exception {
        EditCommand editCommand = new EditCommand("firstName", "Marcin");

        mockMvc.perform(put("/api/v1/admin/customers/update/1")
                        .content(objectMapper.writeValueAsString(editCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals("Marcin", customerRepository.findById(1L)
                .orElseThrow(CustomerNotFoundException::new).getFirstName());

        EditAction editAction = (EditAction) actionRepository.findAll().get(0);
//        assertJ assertThat(...).isEqualTo(..);
//        assertThat("sdsdasd")
//                .isNotNull()
//                .isEqualTo()
//                .isInstanceOf()
//        assertEquals(1L, editAction.getCreatedBy().getId());
        assertEquals(1L, editAction.getEntityId());
        assertEquals(CUSTOMER, editAction.getEntityType());
        assertEquals("firstName", editAction.getFieldName());
        assertEquals("Jan", editAction.getOldValue());
        assertEquals("Marcin", editAction.getNewValue());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldUpdateCar() throws Exception {
        EditCommand editCommand = new EditCommand("brand", "Audi");

        mockMvc.perform(put("/api/v1/admin/cars/update/1")
                        .content(objectMapper.writeValueAsString(editCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals("Audi", carRepository.findById(1L)
                .orElseThrow(CarNotFoundException::new).getBrand());

        EditAction editAction = (EditAction) actionRepository.findAll().get(0);
        assertEquals(1L, editAction.getCreatedBy().getId());
        assertEquals(1L, editAction.getEntityId());
        assertEquals(CAR, editAction.getEntityType());
        assertEquals("brand", editAction.getFieldName());
        assertEquals("Mercedes-Benz", editAction.getOldValue());
        assertEquals("Audi", editAction.getNewValue());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldUpdateParking() throws Exception {
        EditCommand editCommand = new EditCommand("adress", "wapienna");

        mockMvc.perform(put("/api/v1/admin/parking/update/1")
                        .content(objectMapper.writeValueAsString(editCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals("wapienna", parkingRepository.findById(1L)
                .orElseThrow(ParkingNotFoundException::new).getAdress());

        EditAction editAction = (EditAction) actionRepository.findAll().get(0);
        assertEquals(1L, editAction.getCreatedBy().getId());
        assertEquals(1L, editAction.getEntityId());
        assertEquals(PARKING, editAction.getEntityType());
        assertEquals("adress", editAction.getFieldName());
        assertEquals("Address 1", editAction.getOldValue());
        assertEquals("wapienna", editAction.getNewValue());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldNotUpdateCustomer() throws Exception {
        EditCommand editCommand = new EditCommand("Adress", "Wapienna 1");

        mockMvc.perform(put("/api/v1/admin/customers/update/1")
                        .content(objectMapper.writeValueAsString(editCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(CUSTOMER_FIELD_ERROR_MESSAGE));
    }


    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldNotUpdateCar() throws Exception {
        EditCommand editCommand = new EditCommand("Name", "newName");

        mockMvc.perform(put("/api/v1/admin/cars/update/1")
                        .content(objectMapper.writeValueAsString(editCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(CAR_FIELD_ERROR_MESSAGE));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldNotUpdateParking() throws Exception {
        EditCommand editCommand = new EditCommand("contact", "Michal");

        mockMvc.perform(put("/api/v1/admin/parking/update/1")
                        .content(objectMapper.writeValueAsString(editCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(PARKING_FIELD_ERROR_MESSAGE));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldDisableCustomerAccount() throws Exception {
        mockMvc.perform(put("/api/v1/admin/customers/disable-account/1"))
                .andExpect(status().isOk());

        assertFalse(customerRepository.findById(1L)
                .orElseThrow(CustomerNotFoundException::new).isAccountEnabled());

        Action action = actionRepository.findAll().get(0);
        assertEquals(1L, action.getCreatedBy().getId());
        assertEquals(ActionType.DISABLE_CUSTOMER_ACCOUNT, action.getActionType());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldEnableCustomerAccount() throws Exception {
        Customer customer = customerRepository.findById(1L)
                .orElseThrow(CustomerNotFoundException::new);

        mockMvc.perform(put("/api/v1/admin/customers/disable-account/1"));
        mockMvc.perform(put("/api/v1/admin/customers/enable-account/1"))
                .andExpect(status().isOk());

        assertTrue(customer.isEnabled());

        Action action = actionRepository.findAll().get(1);
        assertEquals(ActionType.ENABLE_CUSTOMER_ACCOUNT, action.getActionType());
        assertEquals(1L, action.getCreatedBy().getId());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldLockCustomerAccount() throws Exception {
        mockMvc.perform(put("/api/v1/admin/customers/lock-account/1"))
                .andExpect(status().isOk());

        assertFalse(customerRepository.findById(1L)
                .orElseThrow(CustomerNotFoundException::new).isAccountNonLocked());

        Action action = actionRepository.findAll().get(0);
        assertEquals(1L, action.getCreatedBy().getId());
        assertEquals(ActionType.LOCK_CUSTOMER_ACCOUNT, action.getActionType());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldUnlockCustomerAccount() throws Exception {
        Customer customer = customerRepository.findById(1L)
                .orElseThrow(CustomerNotFoundException::new);

        mockMvc.perform(put("/api/v1/admin/customers/lock-account/1"));
        mockMvc.perform(put("/api/v1/admin/customers/unlock-account/1"))
                .andExpect(status().isOk());

        assertTrue(customer.isAccountNonLocked());

        Action action = actionRepository.findAll().get(1);
        assertEquals(1L, action.getCreatedBy().getId());
        assertEquals(ActionType.UNLOCK_CUSTOMER_ACCOUNT, action.getActionType());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldGetAllActions() throws Exception {
        mockMvc.perform(put("/api/v1/admin/customers/lock-account/1"));
        mockMvc.perform(put("/api/v1/admin/customers/unlock-account/1"));
        mockMvc.perform(put("/api/v1/admin/customers/disable-account/1"));
        mockMvc.perform(put("/api/v1/admin/customers/enable-account/1"));

        mockMvc.perform(put("/api/v1/admin/customers/update/1")
                        .content(objectMapper.writeValueAsString(new EditCommand("firstName", "Marcin")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/admin/cars/update/1")
                        .content(objectMapper.writeValueAsString(new EditCommand("brand", "Audi")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/admin/parking/update/1")
                        .content(objectMapper.writeValueAsString(new EditCommand("adress", "wapienna")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


//        wrzucic jakies parametry do paginazji np size 3 i sprawdzic czy faktycznie jest size 3
        mockMvc.perform(get("/api/v1/admin/action/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].actionType").value("LOCK_CUSTOMER_ACCOUNT"))
                .andExpect(jsonPath("$.content[1].actionType").value("UNLOCK_CUSTOMER_ACCOUNT"))
                .andExpect(jsonPath("$.content[2].actionType").value("DISABLE_CUSTOMER_ACCOUNT"))
                .andExpect(jsonPath("$.content[3].actionType").value("ENABLE_CUSTOMER_ACCOUNT"))
                .andExpect(jsonPath("$.content[4].actionType").value("EDIT"))
                .andExpect(jsonPath("$.content[5].actionType").value("EDIT"));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldReturnAllCustomers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].firstName").value("Jan"))
                .andExpect(jsonPath("$.content[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$.content[1].firstName").value("Michal"))
                .andExpect(jsonPath("$.content[1].lastName").value("Nowak"));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldReturnAllCars() throws Exception {
        mockMvc.perform(get("/api/v1/admin/cars/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
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
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldReturnAllParkings() throws Exception {
        mockMvc.perform(get("/api/v1/admin/parking/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Parking 1"))
                .andExpect(jsonPath("$.content[0].adress").value("Address 1"))
                .andExpect(jsonPath("$.content[1].name").value("Parking 2"))
                .andExpect(jsonPath("$.content[1].adress").value("Address 2"));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldSaveNewCar() throws Exception {
        this.mockMvc.perform(post("/api/v1/admin/cars/save")
                        .content(objectMapper.writeValueAsString(testDataLoader.createCarCommand()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Car savedCar = carRepository.findById(5L)
                .orElseThrow(CarNotFoundException::new);

        assertEquals("Audi", savedCar.getBrand());
        assertEquals("A4", savedCar.getModel());
        assertEquals(200000, savedCar.getPrice());
        assertEquals(1, savedCar.getLength());
        assertEquals(1, savedCar.getWidth());
        assertEquals(Fuel.PETROL, savedCar.getFuel());
        assertEquals(LocalDate.of(2023, 10, 10), savedCar.getDateOfProduction());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldSaveAllCars() throws Exception {
        this.mockMvc.perform(post("/api/v1/admin/cars/save/batch")
                        .content(objectMapper.writeValueAsString(testDataLoader.createCarCommands()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        Car savedCar1 = carRepository.findById(5L).orElseThrow(CarNotFoundException::new);
        assertEquals("Audi", savedCar1.getBrand());
        assertEquals("A4", savedCar1.getModel());
        assertEquals(200000, savedCar1.getPrice());
        assertEquals(1, savedCar1.getLength());
        assertEquals(1, savedCar1.getWidth());
        assertEquals(Fuel.DIESEL, savedCar1.getFuel());
        assertEquals(LocalDate.of(2023, 10, 10), savedCar1.getDateOfProduction());


        Car savedCar2 = carRepository.findById(6L).orElseThrow(CarNotFoundException::new);
        assertEquals("BMW", savedCar2.getBrand());
        assertEquals("M4", savedCar2.getModel());
        assertEquals(300000, savedCar2.getPrice());
        assertEquals(1, savedCar2.getLength());
        assertEquals(1, savedCar2.getWidth());
        assertEquals(Fuel.PETROL, savedCar2.getFuel());
        assertEquals(LocalDate.of(2023, 11, 11), savedCar2.getDateOfProduction());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldSaveParking() throws Exception {
        mockMvc.perform(post("/api/v1/admin/parking/save")
                        .content(objectMapper.writeValueAsString(testDataLoader.createParkingCommand()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Parking savedParking = parkingRepository.findById(3L).orElseThrow(ParkingNotFoundException::new);
        assertEquals("Wapienna", savedParking.getAdress());
        assertEquals("Parking ABC", savedParking.getName());
        assertEquals(ParkingType.UNDERGROUND, savedParking.getParkingType());
        assertEquals(10, savedParking.getCapacity());
        assertEquals(10, savedParking.getParkingSpotWidth());
        assertEquals(10, savedParking.getParkingSpotLength());
        assertEquals(10, savedParking.getPlacesForElectricCars());
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldDeleteCar() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/cars/1/delete"))
                .andExpect(status().isOk());

        assertFalse(carRepository.existsById(1L));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldDeleteParking() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/parking/1/delete"))
                .andExpect(status().isOk());

        assertFalse(parkingRepository.existsById(1L));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldParkCar() throws Exception {
        mockMvc.perform(post("/api/v1/admin/cars/1/park/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Car parkedCar = carRepository.findById(1L)
                .orElseThrow(CarNotFoundException::new);
        assertEquals(1L, parkedCar.getParking().getId());
        assertEquals(1, parkedCar.getParking().getTakenPlaces());
        assertEquals(0, parkedCar.getParking().getTakenElectricPlaces());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldLeaveParking() throws Exception {
        mockMvc.perform(post("/api/v1/admin/cars/1/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/admin/cars/1/leave")
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

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldGetAllCarsFromParking() throws Exception {
        mockMvc.perform(post("/api/v1/admin/cars/1/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/admin/cars/2/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/admin/cars/3/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/v1/admin/parking/1/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].brand").value("Mercedes-Benz"))
                .andExpect(jsonPath("$.content[0].model").value("c-class"))
                .andExpect(jsonPath("$.content[1].brand").value("BMW"))
                .andExpect(jsonPath("$.content[1].model").value("M3"))
                .andExpect(jsonPath("$.content[2].brand").value("Tesla"))
                .andExpect(jsonPath("$.content[2].model").value("Model S"));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldCountAllCarsFromParking() throws Exception {
        mockMvc.perform(post("/api/v1/admin/cars/1/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/admin/cars/2/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/admin/cars/3/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/v1/admin/parking/1/cars/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldGetMostExpensiveCarFromParking() throws Exception {

        mockMvc.perform(post("/api/v1/admin/cars/1/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/admin/cars/2/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/admin/cars/3/park/1")
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/v1/admin/parking/1/cars/most-expensive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model S"));
    }

    @Test
    @WithMockUser(username = "admin", password = "adminPassword", roles = "ADMIN")
    void shouldGetMostExpensiveCar() throws Exception {
        mockMvc.perform(get("/api/v1/admin/cars/most-expensive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.model").value("M5"));
    }
}
