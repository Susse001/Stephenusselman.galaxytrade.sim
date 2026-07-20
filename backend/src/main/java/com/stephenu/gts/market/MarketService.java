package com.stephenu.gts.market;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.market.dto.MarketResponse;
import com.stephenu.gts.starsystem.StarSystem;

import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Provides services for retrieving market data.
 */
@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;

    /**
     * Returns all markets.
     *
     * @return A list of all markets.
     */
    public List<MarketResponse> getAllMarkets() {
        return marketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns the markets for a star system.
     *
     * @param systemId The identifier of the requested star system.
     * @return A list of markets for the specified star system.
     */
    public List<MarketResponse> getMarketsForSystem(Long systemId) {
        return marketRepository.findByStarSystemId(systemId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MarketResponse mapToResponse(Market market) {
        StarSystem system = market.getStarSystem();
        Commodity commodity = market.getCommodity();

        return new MarketResponse(
                market.getId(),
                system.getId(),
                system.getName(),
                system.getRegion(),
                commodity.getType(),
                market.getPrice(),
                market.getInventory(),
                market.getTargetInventory()
        );
    }
}
