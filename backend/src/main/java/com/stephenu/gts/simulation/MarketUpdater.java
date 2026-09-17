package com.stephenu.gts.simulation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.starsystem.StarSystemEconomicProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarketUpdater {

    private final MarketRepository marketRepository;

    /**
     * Updates every market for the current simulation tick.
     */
    public void updateMarkets() {

        List<Market> markets =
                marketRepository.findAll();

        for (Market market : markets) {
            updateMarket(market);
        }

        marketRepository.saveAll(markets);
    }

    /**
     * Advances a market by one simulation tick.
     *
     * @param market The market to update.
     */
    private void updateMarket(Market market) {

        StarSystemEconomicProfile profile =
                market.getStarSystem().getEconomicProfile();

        CommodityType type =
                market.getCommodity().getType();

        double production =
                profile.getExtractionCapacity()
                        .getOrDefault(type, 0.0)
                + profile.getManufacturing()
                        .getOrDefault(type, 0.0);

        double consumption =
                profile.getConsumption()
                        .getOrDefault(type, 0.0);

        int inventory =
                market.getInventory()
                        + (int) Math.round(
                                production - consumption
                        );

        market.setInventory(
                Math.max(0, inventory)
        );

        int newPrice = market.calculatePrice(
            market.getCommodity().getBasePrice(), 
            market.getInventory(), 
            market.getTargetInventory());
        
            market.setPrice(newPrice);
    }
}
