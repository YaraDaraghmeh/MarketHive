package com.markethive.MarketHive.controller;

import com.markethive.MarketHive.dto.request.MarketRequest;
import com.markethive.MarketHive.dto.response.ApiResponse;
import com.markethive.MarketHive.dto.response.MarketResponse;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.services.MarketService;
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
public class MarketController {

    private final MarketService marketService;

    /** Public: list all approved markets */
    @GetMapping("/markets")
    public ResponseEntity<ApiResponse<List<MarketResponse>>> getApprovedMarkets() {
        return ResponseEntity.ok(ApiResponse.success(marketService.getApproved()));
    }

    /** Public: get market by id */
    @GetMapping("/markets/{id}")
    public ResponseEntity<ApiResponse<MarketResponse>> getMarket(@PathVariable("id") String id) {
        return ResponseEntity.ok(ApiResponse.success(marketService.getById(id)));
    }

    /** Market: create a new market (vendor registers a store) */
    @PostMapping("/market/markets")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<MarketResponse>> createMarket(
            @Valid @RequestBody MarketRequest request,
            @AuthenticationPrincipal User currentUser) {

        MarketResponse response = marketService.create(request, currentUser.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Market created, pending approval", response));
    }

    /** Market: update own market */
    @PutMapping("/market/markets/{id}")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<MarketResponse>> updateMarket(
            @PathVariable("id") String id,
            @Valid @RequestBody MarketRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Market updated",
                        marketService.update(id, request, currentUser.getId())
                )
        );
    }

    /** Market: delete own market */
    @DeleteMapping("/market/markets/{id}")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<Void>> deleteMarket(
            @PathVariable("id") String id,
            @AuthenticationPrincipal User currentUser) {

        marketService.delete(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Market deleted", null));
    }

    /** Market: list own markets */
    @GetMapping("/market/markets")
    @PreAuthorize("hasRole('market')")
    public ResponseEntity<ApiResponse<List<MarketResponse>>> getMyMarkets(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success(marketService.getByOwner(currentUser.getId()))
        );
    }

    /** Admin: list all markets */
    @GetMapping("/admin/markets")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<List<MarketResponse>>> getAllMarkets() {
        return ResponseEntity.ok(ApiResponse.success(marketService.getAll()));
    }

    /** Admin: approve market */
    @PatchMapping("/admin/markets/{id}/approve")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<MarketResponse>> approveMarket(
            @PathVariable("id") String id) {

        return ResponseEntity.ok(
                ApiResponse.success("Market approved", marketService.approve(id))
        );
    }

    /** Admin: reject market */
    @PatchMapping("/admin/markets/{id}/reject")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<MarketResponse>> rejectMarket(
            @PathVariable("id") String id) {

        return ResponseEntity.ok(
                ApiResponse.success("Market rejected", marketService.reject(id))
        );
    }
}