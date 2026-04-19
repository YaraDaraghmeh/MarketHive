package com.markethive.MarketHive.services.impl;

import com.markethive.MarketHive.dto.request.ProductRequest;
import com.markethive.MarketHive.dto.response.ProductResponse;
import com.markethive.MarketHive.entity.Category;
import com.markethive.MarketHive.entity.Market;
import com.markethive.MarketHive.entity.Product;
import com.markethive.MarketHive.exception.ForbiddenException;
import com.markethive.MarketHive.exception.ResourceNotFoundException;
import com.markethive.MarketHive.repository.CategoryRepository;
import com.markethive.MarketHive.repository.MarketRepository;
import com.markethive.MarketHive.repository.ProductRepository;
import com.markethive.MarketHive.repository.ReviewRepository;
import com.markethive.MarketHive.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MarketRepository marketRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ProductResponse create(ProductRequest request, String marketId, String ownerId) {
        Market market = marketRepository.findByIdAndOwnerId(marketId, ownerId)
                .orElseThrow(() -> new ForbiddenException("Market not found or you don't own it"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .market(market)
                .isActive(true)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", String.valueOf(request.getCategoryId())));
            product.setCategory(category);
        }

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getById(String id) {
        return mapToResponse(findById(id));
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findByIsActive(true)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getByMarket(String marketId) {
        return productRepository.findByMarketIdAndIsActive(marketId, true)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> search(String keyword) {
        return productRepository.searchByKeyword(keyword)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ProductResponse update(String id, ProductRequest request, String ownerId) {
        Product product = findById(id);
        ensureOwnership(product, ownerId);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", String.valueOf(request.getCategoryId())));
            product.setCategory(category);
        }

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void delete(String id, String ownerId) {
        Product product = findById(id);
        ensureOwnership(product, ownerId);
        productRepository.delete(product);
    }

    @Override
    public void toggleActive(String id, String ownerId) {
        Product product = findById(id);
        ensureOwnership(product, ownerId);
        product.setActive(!product.isActive());
        productRepository.save(product);
    }

    private Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private void ensureOwnership(Product product, String ownerId) {
        if (!product.getMarket().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("You don't own this product's market");
        }
    }

    public ProductResponse mapToResponse(Product p) {
        Double avg = reviewRepository.findAverageRatingByProductId(p.getId());
        return ProductResponse.builder()
                .id(p.getId())
                .marketId(p.getMarket().getId())
                .marketName(p.getMarket().getName())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .imageUrl(p.getImageUrl())
                .isActive(p.isActive())
                .averageRating(avg)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
