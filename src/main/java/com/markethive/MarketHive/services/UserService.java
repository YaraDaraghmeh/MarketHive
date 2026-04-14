package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.response.UserResponse;
import com.markethive.MarketHive.entity.User;

import java.util.List;

public interface UserService {
    UserResponse getProfile(String userId);
    List<UserResponse> getAllUsers();
    void toggleUserActive(String userId);
    UserResponse mapToResponse(User user);
}