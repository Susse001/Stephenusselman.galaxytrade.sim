package com.stephenu.gts.market;

import org.springframework.stereotype.Service;

import com.stephenu.gts.market.dto.MarketResponse;

import java.util.List;

@Service
public class MarketService {

    private final MarketRepository marketRepository;

    public MarketService(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public List<MarketResponse> getAllMarkets() {
        return marketRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MarketResponse> getMarketsForSystem(Long systemId) {
        return marketRepository.findByStarSystemId(systemId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MarketResponse toResponse(Market market) {
        return new MarketResponse(
                market.getId(),
                market.getStarSystem().getId(),
                market.getStarSystem().getName(),
                market.getStarSystem().getRegion().toString(),
                market.getCommodity().getType(),
                market.getPrice(),
                market.getSupply(),
                market.getDemand()
        );
    }
}
