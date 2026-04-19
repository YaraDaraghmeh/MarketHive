package com.markethive.MarketHive.controller;


import com.markethive.MarketHive.dto.request.ProductRequest;
import com.markethive.MarketHive.dto.response.ApiResponse;
import com.markethive.MarketHive.dto.response.ProductResponse;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** Public: all active products */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAll()));
    }

    /** Public: single product */
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    /** Public: search products */
    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(productService.search(keyword)));
    }

    /** Public: products by market */
    @GetMapping("/markets/{marketId}/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByMarket(@PathVariable String marketId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByMarket(marketId)));
    }

    /** Public: products by category */
    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByCategory(categoryId)));
    }

    /** Market: add product to own market */
    @PostMapping("/market/markets/{marketId}/products")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @PathVariable String marketId,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        ProductResponse response = productService.create(request, marketId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product created", response));
    }

    /** Market: update own product */
    @PutMapping("/market/products/{id}")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.update(id, request, currentUser.getId())));
    }

    /** Market: delete own product */
    @DeleteMapping("/market/products/{id}")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id,
            @AuthenticationPrincipal User currentUser) {
        productService.delete(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    /** Market: toggle product visibility */
    @PatchMapping("/market/products/{id}/toggle-active")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<Void>> toggleActive(
            @PathVariable String id,
            @AuthenticationPrincipal User currentUser) {
        productService.toggleActive(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Product visibility toggled", null));
    }
}

