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

        List<Market> markets = marketRepository.findAll();

        TradeOpportunity bestOpportunity = null;

        double bestScore = Double.NEGATIVE_INFINITY;

        for (CommodityType commodityType : CommodityType.values()) {

                Market lowestMarket = markets.stream()
                        .filter(m ->
                                m.getCommodity().getType()
                                        == commodityType)
                        .min(Comparator.comparingInt(
                                Market::getPrice))
                        .orElse(null);

                Market highestMarket = markets.stream()
                        .filter(m ->
                                m.getCommodity().getType()
                                        == commodityType)
                        .max(Comparator.comparingInt(
                                Market::getPrice))
                        .orElse(null);

                if (lowestMarket == null
                        || highestMarket == null) {
                continue;
                }

                int profitPerUnit =
                        highestMarket.getPrice()
                                - lowestMarket.getPrice();

                if (profitPerUnit <= 0) {
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

                opportunity.setBuyPrice(
                        lowestMarket.getPrice()
                );

                opportunity.setSellPrice(
                        highestMarket.getPrice()
                );

                opportunity.setExpectedProfitPerUnit(
                        profitPerUnit
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

        long totalProfit =
                calculateTotalProfit(
                        trader,
                        trade
                );

        return switch (
                trader.getStrategyProfile()) {

                case CONSERVATIVE ->
                        totalProfit *
                        (
                                1.0 /
                                Math.max(
                                1.0,
                                travelTicks / 5.0
                                )
                        );

                case BALANCED ->
                        totalProfit *
                        (
                                1.0 /
                                Math.max(
                                1.0,
                                travelTicks / 10.0
                                )
                        );

                case AGGRESSIVE ->
                        totalProfit *
                        (
                                1.0 /
                                Math.max(
                                1.0,
                                travelTicks / 15.0
                                )
                        );
                };
    }

    private long calculateTotalProfit(
        Trader trader,
        TradeOpportunity trade) {

        long affordableUnits =
                trader.getCredits()
                        / trade.getBuyPrice();

        long units =
                Math.min(
                        affordableUnits,
                        trader.getCargoCapacity()
                );

        return units
                * trade.getExpectedProfitPerUnit();
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
                        distance / 7
                )
        );
    }
}
