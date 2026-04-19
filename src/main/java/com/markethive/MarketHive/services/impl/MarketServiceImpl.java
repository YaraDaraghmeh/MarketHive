package com.markethive.MarketHive.services.impl;

import com.markethive.MarketHive.dto.request.MarketRequest;
import com.markethive.MarketHive.dto.response.MarketResponse;
import com.markethive.MarketHive.entity.Market;
import com.markethive.MarketHive.entity.User;
import com.markethive.MarketHive.exception.ForbiddenException;
import com.markethive.MarketHive.exception.ResourceNotFoundException;
import com.markethive.MarketHive.repository.MarketRepository;
import com.markethive.MarketHive.repository.UserRepository;
import com.markethive.MarketHive.services.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {

    private final MarketRepository marketRepository;
    private final UserRepository userRepository;

    @Override
    public MarketResponse create(MarketRequest request, String ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", ownerId));

        Market market = Market.builder()
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .owner(owner)
                .isApproved(false)
                .build();

        return mapToResponse(marketRepository.save(market));
    }

    @Override
    public MarketResponse getById(String id) {
        return mapToResponse(findById(id));
    }

    @Override
    public List<MarketResponse> getAll() {
        return marketRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<MarketResponse> getApproved() {
        return marketRepository.findByIsApproved(true).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<MarketResponse> getByOwner(String ownerId) {
        return marketRepository.findByOwnerId(ownerId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public MarketResponse update(String id, MarketRequest request, String ownerId) {
        Market market = findById(id);
        if (!market.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("You don't own this market");
        }
        market.setName(request.getName());
        market.setDescription(request.getDescription());
        market.setLogoUrl(request.getLogoUrl());
        return mapToResponse(marketRepository.save(market));
    }

    @Override
    public void delete(String id, String ownerId) {
        Market market = findById(id);
        if (!market.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("You don't own this market");
        }
        marketRepository.delete(market);
    }

    @Override
    public MarketResponse approve(String id) {
        Market market = findById(id);
        market.setApproved(true);
        return mapToResponse(marketRepository.save(market));
    }

    @Override
    public MarketResponse reject(String id) {
        Market market = findById(id);
        market.setApproved(false);
        return mapToResponse(marketRepository.save(market));
    }

    private Market findById(String id) {
        return marketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market", id));
    }

    private MarketResponse mapToResponse(Market m) {
        return MarketResponse.builder()
                .id(m.getId())
                .ownerId(m.getOwner().getId())
                .ownerName(m.getOwner().getName())
                .name(m.getName())
                .description(m.getDescription())
                .logoUrl(m.getLogoUrl())
                .isApproved(m.isApproved())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
