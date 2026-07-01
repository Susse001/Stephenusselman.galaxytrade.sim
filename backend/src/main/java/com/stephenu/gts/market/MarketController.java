package com.stephenu.gts.market;


import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stephenu.gts.market.dto.MarketResponse;

import lombok.RequiredArgsConstructor;

/**
 * Exposes REST endpoints for retrieving market data.
 */
@RestController
@RequestMapping("/api/markets")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    /**
     * Returns all markets.
     *
     * @return A list of all markets.
     */
    @GetMapping
    public List<MarketResponse> getAllMarkets() {
        return marketService.getAllMarkets();
    }

    /**
     * Returns all markets for the specified star system.
     *
     * @param id The identifier of the requested star system.
     * @return A list of markets within the specified star system.
     */
    @GetMapping("/system/{id}")
    public List<MarketResponse> getMarketsForSystem(
            @PathVariable Long id
    ) {
        return marketService.getMarketsForSystem(id);
    }
}
