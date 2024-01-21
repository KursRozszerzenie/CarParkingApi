package com.example.carparkingapi.controller;

import com.example.carparkingapi.command.AdminCommand;
import com.example.carparkingapi.config.security.jwt.JwtService;
import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.dto.CustomerDTO;
import com.example.carparkingapi.model.AuthenticationRequest;
import com.example.carparkingapi.model.AuthenticationResponse;
import com.example.carparkingapi.model.Role;
import com.example.carparkingapi.repository.AdminRepository;
import com.example.carparkingapi.repository.CustomerRepository;
import com.example.carparkingapi.service.AuthenticationService;
import com.example.carparkingapi.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.carparkingapi.command.CustomerCommand;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static com.datical.liquibase.ext.command.init.InitProjectCommandStep$FileTypeEnum.json;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("application-test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    public Customer createCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Jan");
        customer.setLastName("Kowalski");
        customer.setUsername("jan.kowalski@email.com");
        customer.setPassword(passwordEncoder.encode("password"));
        customer.setRole(Role.USER);
        customer.setAccountEnabled(true);
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);

        return customerRepository.save(customer);
    }

    @Test
    void shouldRegisterCustomer() throws Exception {
        CustomerCommand customerCommand = new CustomerCommand();
        customerCommand.setFirstName("Jan");
        customerCommand.setLastName("Kowalski");
        customerCommand.setUsername("jan.kowalski@email.com");
        customerCommand.setPassword("password");
        String json = objectMapper.writeValueAsString(customerCommand);

        mockMvc.perform(post("/api/v1/auth/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.username").value("jan.kowalski@email.com"));
    }

    @Test
    @WithMockUser
    void shouldAuthenticateCustomerAndVerifyToken() throws Exception {
        Customer customer = createCustomer();
        AuthenticationRequest request = new AuthenticationRequest("jan.kowalski@email.com", "password");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/customer/authenticate")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String responseToken = objectMapper.readValue(result
                .getResponse().getContentAsString(), AuthenticationResponse.class).getToken();

        Claims responseClaims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(responseToken)
                .getBody();

        Claims generatedClaims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(jwtService.generateToken(customer))
                .getBody();

        assertEquals(responseClaims.getSubject(), generatedClaims.getSubject());
        assertEquals(responseClaims.get("role"), generatedClaims.get("role"));
    }

    public Admin createAdmin() {
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password"));

        return adminRepository.save(admin);
    }

    @Test
    void shouldRegisterAdmin() throws Exception {
        AdminCommand adminCommand = new AdminCommand();
        adminCommand.setUsername("admin");
        adminCommand.setPassword("password");
        String json = objectMapper.writeValueAsString(adminCommand);

        mockMvc.perform(post("/api/v1/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void shouldAuthenticateAdminAndVerifyToken() throws Exception {
        Admin admin = createAdmin();
        AuthenticationRequest request = new AuthenticationRequest("admin", "password");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/admin/authenticate")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String responseToken = objectMapper.readValue(result
                .getResponse().getContentAsString(), AuthenticationResponse.class).getToken();

        Claims responseClaims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(responseToken)
                .getBody();

        Claims generatedClaims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(jwtService.generateToken(admin))
                .getBody();

        assertEquals(responseClaims.getSubject(), generatedClaims.getSubject());
        assertEquals(responseClaims.get("role"), generatedClaims.get("role"));
    }
}

