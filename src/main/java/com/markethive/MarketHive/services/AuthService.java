package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.LoginRequest;
import com.markethive.MarketHive.dto.request.RegisterRequest;
import com.markethive.MarketHive.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
