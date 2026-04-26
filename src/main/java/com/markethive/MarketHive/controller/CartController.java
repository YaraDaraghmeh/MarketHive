package com.markethive.MarketHive.controller;


import com.markethive.MarketHive.dto.request.CartRequest;
import com.markethive.MarketHive.dto.response.ApiResponse;
import com.markethive.MarketHive.dto.response.CartResponse;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartResponse>>> getCart(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(currentUser.getId())));
    }

}
