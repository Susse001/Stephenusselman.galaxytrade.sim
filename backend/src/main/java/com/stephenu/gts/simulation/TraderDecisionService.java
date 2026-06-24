package com.stephenu.gts.simulation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.trader.Trader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TraderDecisionService {

    private final MarketRepository marketRepository;

    public TradeOpportunity findBestTrade(Trader trader) {

        List<Market> markets =
            marketRepository.findAll();

        TradeOpportunity bestOpportunity = null;

        double bestScore =
                Double.NEGATIVE_INFINITY;

        for (CommodityType commodityType
                : CommodityType.values()) {

                Market lowestMarket = markets.stream()
                        .filter(m ->
                                m.getCommodity().getType()
                                        == commodityType)
                        .min(
                                Comparator.comparingInt(
                                        Market::getPrice
                                )
                        )
                        .orElse(null);

                Market highestMarket = markets.stream()
                        .filter(m ->
                                m.getCommodity().getType()
                                        == commodityType)
                        .max(
                                Comparator.comparingInt(
                                        Market::getPrice
                                )
                        )
                        .orElse(null);

                if (lowestMarket == null
                        || highestMarket == null) {
                continue;
                }

                int profit =
                        highestMarket.getPrice()
                                - lowestMarket.getPrice();

                if (profit <= 0) {
                continue;
                }

                TradeOpportunity opportunity =
                        new TradeOpportunity();

                opportunity.setCommodity(
                        commodityType
                );

                opportunity.setBuySystem(
                        lowestMarket.getStarSystem()
                );

                opportunity.setSellSystem(
                        highestMarket.getStarSystem()
                );

                opportunity.setExpectedProfit(
                        profit
                );

                double score =
                        calculateScore(
                                trader,
                                opportunity
                        );

                if (score > bestScore) {

                bestScore = score;
                bestOpportunity = opportunity;
                }
        }

        return bestOpportunity;
    }

    private double calculateScore(
        Trader trader,
        TradeOpportunity trade) {

        int travelTicks =
                calculateTravelTicks(
                        trader.getCurrentSystem(),
                        trade.getBuySystem()
                )
                +
                calculateTravelTicks(
                        trade.getBuySystem(),
                        trade.getSellSystem()
                );

        return switch (
                trader.getStrategyProfile()
        ) {

                case AGGRESSIVE ->
                        trade.getExpectedProfit();

                case CONSERVATIVE ->
                        (double) trade.getExpectedProfit()
                                / travelTicks;

                case BALANCED ->
                        trade.getExpectedProfit()
                                / Math.sqrt(
                                        travelTicks
                                );
        };
    }

    private int calculateTravelTicks(
        StarSystem from,
        StarSystem to) {

        double dx =
                from.getXCoordinate() - to.getXCoordinate();

        double dy =
                from.getYCoordinate() - to.getYCoordinate();

        double distance =
                Math.sqrt(
                        dx * dx + dy * dy
                );

        return Math.max(
                1,
                (int) Math.ceil(
                        distance / 10.0
                )
        );
    }
}
