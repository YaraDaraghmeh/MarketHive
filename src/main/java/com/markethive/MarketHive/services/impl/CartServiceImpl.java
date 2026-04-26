package com.markethive.MarketHive.services.impl;

import com.markethive.MarketHive.dto.request.CartRequest;
import com.markethive.MarketHive.dto.response.CartResponse;
import com.markethive.MarketHive.entity.CartItem;
import com.markethive.MarketHive.entity.Product;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.exception.BadRequestException;
import com.markethive.MarketHive.exception.ResourceNotFoundException;
import com.markethive.MarketHive.repository.CartItemRepository;
import com.markethive.MarketHive.repository.ProductRepository;
import com.markethive.MarketHive.repository.UserRepository;
import com.markethive.MarketHive.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<CartResponse> getCart(String userId) {
        return cartItemRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }


    @Override
    public CartResponse addToCart(CartRequest request, String userId) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        if (!product.isActive()) {
            throw new BadRequestException("Product is not available");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock");
        }

        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());

        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));
            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
        }

        return mapToResponse(cartItemRepository.save(cartItem));
    }

    @Override
    public CartResponse updateQuantity(String productId, int quantity, String userId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Product product = cartItem.getProduct();
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }

        cartItem.setQuantity(quantity);
        return mapToResponse(cartItemRepository.save(cartItem));
    }

    @Override
    @Transactional
    public void removeItem(String productId, String userId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartResponse mapToResponse(CartItem item) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImage(item.getProduct().getImageUrl())
                .unitPrice(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .addedAt(item.getAddedAt())
                .build();
    }
}
