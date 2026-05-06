package com.markethive.MarketHive.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markethive.MarketHive.dto.request.LoginRequest;
import com.markethive.MarketHive.dto.request.RegisterRequest;
import com.markethive.MarketHive.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setName("John Doe");
        validRegisterRequest.setEmail("john@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setPhone("+1234567890");
        validRegisterRequest.setRole("user");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("john@example.com");
        validLoginRequest.setPassword("password123");
    }

    @Test
    void testRegisterEndpoint_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registered successfully"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value("john@example.com"))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.role").value("user"));
    }

    @Test
    void testRegisterWithMarketRole_Success() throws Exception {
        // Arrange
        RegisterRequest marketRegister = new RegisterRequest();
        marketRegister.setName("Market Store");
        marketRegister.setEmail("market@example.com");
        marketRegister.setPassword("password123");
        marketRegister.setPhone("+1234567890");
        marketRegister.setRole("market");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(marketRegister)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("market"));
    }

    @Test
    void testRegisterAsAdmin_Fails() throws Exception {
        // Arrange
        RegisterRequest adminRegister = new RegisterRequest();
        adminRegister.setName("Admin User");
        adminRegister.setEmail("admin@example.com");
        adminRegister.setPassword("password123");
        adminRegister.setRole("admin");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRegister)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRegisterDuplicateEmail_Fails() throws Exception {
        // Arrange - Register first user
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)));

        RegisterRequest duplicateRegister = new RegisterRequest();
        duplicateRegister.setName("Another User");
        duplicateRegister.setEmail("john@example.com");
        duplicateRegister.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRegister)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRegisterWithMissingName_ValidationFails() throws Exception {
        // Arrange
        RegisterRequest invalidRegister = new RegisterRequest();
        invalidRegister.setEmail("test@example.com");
        invalidRegister.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithInvalidEmail_ValidationFails() throws Exception {
        // Arrange
        RegisterRequest invalidRegister = new RegisterRequest();
        invalidRegister.setName("Test User");
        invalidRegister.setEmail("invalid-email");
        invalidRegister.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithShortPassword_ValidationFails() throws Exception {
        // Arrange
        RegisterRequest invalidRegister = new RegisterRequest();
        invalidRegister.setName("Test User");
        invalidRegister.setEmail("test@example.com");
        invalidRegister.setPassword("123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegister)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginEndpoint_Success() throws Exception {
        // Arrange - Register first
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value("john@example.com"))
                .andExpect(jsonPath("$.data.name").value("John Doe"));
    }

    @Test
    void testLoginWithInvalidEmail_Fails() throws Exception {
        // Arrange
        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setEmail("nonexistent@example.com");
        invalidLogin.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithWrongPassword_Fails() throws Exception {
        // Arrange - Register first
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)));

        LoginRequest wrongPasswordLogin = new LoginRequest();
        wrongPasswordLogin.setEmail("john@example.com");
        wrongPasswordLogin.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithMissingEmail_ValidationFails() throws Exception {
        // Arrange
        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginWithMissingPassword_ValidationFails() throws Exception {
        // Arrange
        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setEmail("test@example.com");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCompleteAuthFlow_RegisterAndLogin() throws Exception {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("New User");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("securepass123");
        registerRequest.setPhone("+1111111111");

        // Act - Register
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        assertNotNull(registerResponse);

        // Act - Login with registered credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("newuser@example.com");
        loginRequest.setPassword("securepass123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"))
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        assertNotNull(loginResponse);
    }

    @Test
    void testRegisterEndpointReturnsCorrectStructure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.type").value("Bearer"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.role").exists());
    }
}
