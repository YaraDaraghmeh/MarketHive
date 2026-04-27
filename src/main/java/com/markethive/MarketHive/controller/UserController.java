package com.markethive.MarketHive.controller;

import com.markethive.MarketHive.dto.response.ApiResponse;
import com.markethive.MarketHive.dto.response.UserResponse;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.services.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(userService.mapToResponse(currentUser)));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") String id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(id)));
    }

    @PatchMapping("/admin/users/{id}/toggle-active")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable String id) {
        userService.toggleUserActive(id);
        return ResponseEntity.ok(ApiResponse.success("User status toggled", null));
    }
}
