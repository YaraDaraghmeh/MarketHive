package com.markethive.MarketHive.services.impl;

import com.markethive.MarketHive.dto.request.OrderRequest;
import com.markethive.MarketHive.dto.response.OrderItemResponse;
import com.markethive.MarketHive.dto.response.OrderResponse;
import com.markethive.MarketHive.dto.response.PaymentResponse;
import com.markethive.MarketHive.entity.*;
import com.markethive.MarketHive.enums.OrderStatus;
import com.markethive.MarketHive.exception.BadRequestException;
import com.markethive.MarketHive.exception.ForbiddenException;
import com.markethive.MarketHive.exception.ResourceNotFoundException;
import com.markethive.MarketHive.repository.*;
import com.markethive.MarketHive.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        // Validate stock for all items first
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!product.isActive()) {
                throw new BadRequestException("Product is no longer available: " + product.getName());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for: " + product.getName()
                        + ". Available: " + product.getStock());
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Build order
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.pending)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .build();
        order = orderRepository.save(order);

        // Build order items and deduct stock
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal unitPrice = product.getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .build();
            orderItemRepository.save(orderItem);

            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            total = total.add(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // Update total and clear cart
        order.setTotalAmount(total);
        orderRepository.save(order);
        cartItemRepository.deleteByUserId(userId);

        return mapToResponse(order);
    }

    @Override
    public OrderResponse getById(String id, String userId) {
        Order order = findById(id);
        // Users can only see their own orders; admins bypass this via controller
        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to this order");
        }
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getMyOrders(String userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateStatus(String id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void cancelOrder(String id, String userId) {
        Order order = findById(id);
        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only cancel your own orders");
        }
        if (order.getStatus() != OrderStatus.pending && order.getStatus() != OrderStatus.confirmed) {
            throw new BadRequestException("Cannot cancel an order that is already " + order.getStatus().name());
        }

        // Restore stock
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.cancelled);
        orderRepository.save(order);
    }

    private Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        List<OrderItemResponse> itemResponses = items.stream().map(item ->
                OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build()
        ).collect(Collectors.toList());

        PaymentResponse paymentResponse = null;
        if (order.getPayment() != null) {
            Payment p = order.getPayment();
            paymentResponse = PaymentResponse.builder()
                    .id(p.getId())
                    .orderId(order.getId())
                    .amount(p.getAmount())
                    .status(p.getStatus().name())
                    .method(p.getMethod())
                    .transactionId(p.getTransactionId())
                    .paidAt(p.getPaidAt())
                    .build();
        }

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .items(itemResponses)
                .payment(paymentResponse)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
