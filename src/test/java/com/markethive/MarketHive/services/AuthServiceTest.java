package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.LoginRequest;
import com.markethive.MarketHive.dto.request.RegisterRequest;
import com.markethive.MarketHive.dto.response.AuthResponse;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.enums.Role;
import com.markethive.MarketHive.exception.BadRequestException;
import com.markethive.MarketHive.repository.UserRepository;
import com.markethive.MarketHive.security.JwtUtils;
import com.markethive.MarketHive.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("+1234567890");
        registerRequest.setRole("user");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");
    }

    // ================= REGISTER =================

    @Test
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        User savedUser = User.builder()
                .id("1")   // FIXED: String not Long
                .name("John Doe")
                .email("john@example.com")
                .role(Role.user)
                .phone("+1234567890")
                .isActive(true)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtils.generateToken(any())).thenReturn("fake-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
        assertEquals("user", response.getRole());
        assertNotNull(response.getToken());
    }

    @Test
    void register_duplicateEmail_shouldFail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void register_admin_shouldFail() {
        registerRequest.setRole("admin");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void register_invalidRole_defaultsToUser() {
        registerRequest.setRole("invalid");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        User savedUser = User.builder()
                .id("1") // FIXED
                .name("John Doe")
                .email("john@example.com")
                .role(Role.user)
                .build();

        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtUtils.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(registerRequest);

        assertEquals("user", response.getRole());
    }

    // ================= LOGIN =================

    @Test
    void login_success() {
        User user = User.builder()
                .id("1") // FIXED
                .name("John Doe")
                .email("john@example.com")
                .role(Role.user)
                .build();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateToken(any())).thenReturn("fake-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
        assertEquals("user", response.getRole());
    }

    @Test
    void login_fail() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
    }
}