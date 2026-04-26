package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.CartRequest;
import com.markethive.MarketHive.dto.response.CartResponse;

import java.util.List;

public interface CartService {
    List<CartResponse> getCart(String userId);
    CartResponse addToCart(CartRequest request, String userId);

}
