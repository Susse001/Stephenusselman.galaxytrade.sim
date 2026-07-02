package com.stephenu.gts.simulation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.trader.Trader;

import lombok.RequiredArgsConstructor;

/**
 * Evaluates available trade opportunities for traders.
 *
 * Candidate routes are scored according to each trader's strategy
 * profile, available credits, cargo capacity, and travel time.
 */
@Service
@RequiredArgsConstructor
public class TraderDecisionService {

    private final MarketRepository marketRepository;
    private final TravelService travelService;

    /**
	 * Selects the highest-scoring trade opportunity for a trader.
	 *
	 * Every commodity is evaluated by comparing the lowest available buy
	 * price with the highest available sell price before applying the
	 * trader's scoring strategy.
	 *
	 * @param trader The trader requesting a trade.
	 * @return The highest-scoring trade opportunity, or {@code null} if none
	 *         are profitable.
	 */
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
					new TradeOpportunity(
							null,
							commodityType,
							lowestMarket.getStarSystem(),
							highestMarket.getStarSystem(),
							profitPerUnit,
							lowestMarket.getPrice(),
							highestMarket.getPrice()
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

	/**
	 * Calculates the desirability of a trade opportunity.
	 *
	 * The final score combines estimated profit and travel time using the
	 * trader's strategy profile.
	 *
	 * @param trader The trader evaluating the opportunity.
	 * @param trade The trade opportunity being evaluated.
	 * @return The calculated trade score.
	 */
    private double calculateScore(
        Trader trader,
        TradeOpportunity trade) {

        int travelTicks =
                travelService.calculateTravelTicks(
                        trader.getCurrentSystem(),
                        trade.getBuySystem()
                )
                +
                travelService.calculateTravelTicks(
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

	/**
	 * Estimates the maximum profit a trader can earn from a trade.
	 *
	 * The calculation considers both available credits and cargo capacity.
	 *
	 * @param trader The trader executing the trade.
	 * @param trade The trade opportunity.
	 * @return The estimated total profit.
	 */
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

        return units * trade.getExpectedProfitPerUnit();
    }

}
