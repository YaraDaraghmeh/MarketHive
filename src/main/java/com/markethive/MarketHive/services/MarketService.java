package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.MarketRequest;
import com.markethive.MarketHive.dto.response.MarketResponse;

import java.util.List;

public interface MarketService {
    MarketResponse create(MarketRequest request, String ownerId);
    MarketResponse getById(String id);
    List<MarketResponse> getAll();
    List<MarketResponse> getApproved();
    List<MarketResponse> getByOwner(String ownerId);
    MarketResponse update(String id, MarketRequest request, String ownerId);
    void delete(String id, String ownerId);
    MarketResponse approve(String id);
    MarketResponse reject(String id);
}

