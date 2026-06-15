package com.stephenu.gts.simulation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.trader.Trader;

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
                    .min((a, b) -> Integer.compare(
                            a.getPrice(),
                            b.getPrice()))
                    .orElse(null);

            Market highestMarket = markets.stream()
                    .filter(m -> m.getCommodity().getType() == commodityType)
                    .max((a, b) -> Integer.compare(
                            a.getPrice(),
                            b.getPrice()))
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
                    || profit > bestOpportunity.expectedProfit()) {

                bestOpportunity = new TradeOpportunity(
                        commodityType,
                        lowestMarket.getStarSystem(),
                        highestMarket.getStarSystem(),
                        profit
                );
            }
        }

        return bestOpportunity;
    }
}
