package com.markethive.MarketHive.controller;

import com.markethive.MarketHive.dto.request.LoginRequest;
import com.markethive.MarketHive.dto.request.RegisterRequest;
import com.markethive.MarketHive.dto.response.ApiResponse;
import com.markethive.MarketHive.dto.response.AuthResponse;
import com.markethive.MarketHive.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Creates a regular customer — active immediately
    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Account created", authService.registerUser(request))
        );
    }

    // Creates a vendor account — inactive until admin approves
    @PostMapping("/register/market")
    public ResponseEntity<ApiResponse<AuthResponse>> registerMarket(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Submitted, awaiting admin approval", authService.registerMarket(request))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", authService.login(request))
        );
    }
}
