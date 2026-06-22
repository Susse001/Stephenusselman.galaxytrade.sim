package com.stephenu.gts.simulation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TraderDecisionService {

    private final MarketRepository marketRepository;

    public TradeOpportunity findBestTrade() {

        List<Market> markets = marketRepository.findAll();

        TradeOpportunity bestOpportunity = null;

        for (CommodityType commodityType : CommodityType.values()) {

            Market lowestMarket = markets.stream()
                    .filter(m -> m.getCommodity().getType() == commodityType)
                    .min(Comparator.comparingInt(Market::getPrice))
                    .orElse(null);

            Market highestMarket = markets.stream()
                    .filter(m -> m.getCommodity().getType() == commodityType)
                    .max(Comparator.comparingInt(Market::getPrice))
                    .orElse(null);

            if (lowestMarket == null || highestMarket == null) {
                continue;
            }

            int profit =
                    highestMarket.getPrice()
                            - lowestMarket.getPrice();

            if (profit <= 0) {
                continue;
            }

            if (bestOpportunity == null
                    || profit > bestOpportunity.getExpectedProfit()) {

                TradeOpportunity opportunity =
                        new TradeOpportunity();

                opportunity.setCommodity(commodityType);
                opportunity.setBuySystem(
                        lowestMarket.getStarSystem()
                );
                opportunity.setSellSystem(
                        highestMarket.getStarSystem()
                );
                opportunity.setExpectedProfit(profit);

                bestOpportunity = opportunity;
            }
        }

        return bestOpportunity;
    }
}
