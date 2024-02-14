package com.example.carparkingapi.controller;

import com.example.carparkingapi.config.security.jwt.JwtService;
import com.example.carparkingapi.domain.Admin;
import com.example.carparkingapi.domain.Customer;
import com.example.carparkingapi.model.AuthenticationRequest;
import com.example.carparkingapi.model.AuthenticationResponse;
import com.example.carparkingapi.data.loader.TestDataLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private TestDataLoader testDataLoader;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldRegisterCustomer() throws Exception {
        String json = objectMapper.writeValueAsString(testDataLoader.createCustomerCommand());

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
    void shouldRegisterAdmin() throws Exception {
        String json = objectMapper.writeValueAsString(testDataLoader.createAdminCommand());

        mockMvc.perform(post("/api/v1/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void shouldAuthenticateCustomerAndVerifyToken() throws Exception {
        Customer customer = testDataLoader.createCustomer();
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

    @Test
    void shouldAuthenticateAdminAndVerifyToken() throws Exception {
        Admin admin = testDataLoader.createAdmin();
        AuthenticationRequest request = new AuthenticationRequest("admin", "adminPassword");

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

