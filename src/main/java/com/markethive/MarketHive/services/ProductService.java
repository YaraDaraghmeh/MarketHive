package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.ProductRequest;
import com.markethive.MarketHive.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request, String marketId, String ownerId);
    ProductResponse getById(String id);
    List<ProductResponse> getAll();
    List<ProductResponse> getByMarket(String marketId);
    List<ProductResponse> getByCategory(Integer categoryId);
    List<ProductResponse> search(String keyword);
    ProductResponse update(String id, ProductRequest request, String ownerId);
    void delete(String id, String ownerId);
    void toggleActive(String id, String ownerId);
}
