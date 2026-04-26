package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.OrderRequest;
import com.markethive.MarketHive.dto.response.OrderResponse;
import com.markethive.MarketHive.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request, String userId);
    OrderResponse getById(String id, String userId);
    List<OrderResponse> getMyOrders(String userId);
    List<OrderResponse> getAllOrders();
    OrderResponse updateStatus(String id, OrderStatus status);
    void cancelOrder(String id, String userId);
}
