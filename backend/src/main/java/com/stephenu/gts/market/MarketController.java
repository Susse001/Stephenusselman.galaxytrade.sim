package com.stephenu.gts.market;


import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stephenu.gts.market.dto.MarketResponse;

@RestController
@RequestMapping("/api/markets")
@CrossOrigin(origins = "http://localhost:5173")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping
    public List<MarketResponse> getAllMarkets() {
        return marketService.getAllMarkets();
    }

    @GetMapping("/system/{id}")
    public List<MarketResponse> getMarketsForSystem(
            @PathVariable Long id
    ) {
        return marketService.getMarketsForSystem(id);
    }
}
