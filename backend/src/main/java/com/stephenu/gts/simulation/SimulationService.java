package com.stephenu.gts.simulation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.trader.Trader;
import com.stephenu.gts.trader.TraderRepository;
import com.stephenu.gts.trader.TraderStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Coordinates each simulation tick.
 *
 * Every tick updates market conditions before advancing each trader
 * through its current state. Market production, consumption, trading,
 * and trader movement are processed once per simulation cycle.
 */
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final TraderRepository traderRepository;
    private final MarketRepository marketRepository;
    private final TradeOpportunityRepository tradeOpportunityRepository;
    private final TraderDecisionService traderDecisionService;
    private final TravelService travelService;
    private final MarketUpdater marketUpdater;
    private final GalaxyEconomicMonitor galaxyEconomicMonitor;

    private long currentTick = 0;

    /**
     * Advances the simulation by one tick.
     *
     * Market conditions are updated before trader behavior so that
     * trade decisions use the latest market state.
     *
     * @return The current simulation tick.
     */
    @Transactional
    public long runTick() {

        currentTick++;

        marketUpdater.updateMarkets();
        evaluateTraders();

        galaxyEconomicMonitor.printEconomicSummary();

        return currentTick;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    /**
     * Advances every trader by one simulation step.
     */
    private void evaluateTraders() {

        List<Trader> traders =
                traderRepository.findAll();

        for (Trader trader : traders) {
            processTrader(trader);
        }

        traderRepository.saveAll(traders);
    }

    /**
     * Advances a trader according to its current simulation state.
     *
     * Each trader follows a state machine that governs trade selection,
     * travel, buying, and selling.
     *
     * @param trader The trader to process.
     */
    private void processTrader(Trader trader) {

        switch (trader.getStatus()) {

            case IDLE -> assignTrade(trader);

            case TRAVELING_TO_BUY -> travelToBuy(trader);

            case BUYING -> buy(trader);

            case TRAVELING_TO_SELL -> travelToSell(trader);

            case SELLING -> sell(trader);
        }
    }

    /**
     * Assigns the highest-scoring trade opportunity to an idle trader.
     *
     * @param trader The trader to update.
     */
    private void assignTrade(Trader trader) {

        TradeOpportunity opportunity =
                traderDecisionService.findBestTrade(trader);

        if (opportunity == null) {
            return;
        }

        tradeOpportunityRepository.save(opportunity);

        trader.setCurrentTrade(opportunity);

        int travelTicks =
                travelService.calculateTravelTicks(
                        trader.getCurrentSystem(),
                        opportunity.getBuySystem()
                );

        trader.setTravelTicksRemaining(travelTicks);
        trader.setTotalTravelTicks(travelTicks);

        trader.setStatus(
                TraderStatus.TRAVELING_TO_BUY
        );
    }

    /**
     * Advances a trader toward its purchase location.
     *
     * @param trader The trader to update.
     */
    private void travelToBuy(Trader trader) {

        int remaining =
                trader.getTravelTicksRemaining() - 1;

        trader.setTravelTicksRemaining(remaining);

        if (remaining > 0) {
            return;
        }

        trader.setCurrentSystem(
                trader.getCurrentTrade().getBuySystem()
        );

        trader.setStatus(TraderStatus.BUYING);
    }

    /**
     * Purchases as much cargo as the trader can afford, carry, and
     * the source market can supply.
     *
     * @param trader The trader executing the purchase.
     */
    private void buy(Trader trader) {

        TradeOpportunity trade =
                trader.getCurrentTrade();

        Market market =
                findMarket(
                        trade.getBuySystem(),
                        trade.getCommodity()
                );

        if (market == null) {
            cancelTrade(trader);
            return;
        }

        long affordableUnits =
                trader.getCredits()
                        / trade.getBuyPrice();

        long cargoUnits =
                trader.getCargoCapacity();

        long availableUnits =
                market.getInventory();

        long units =
                Math.min(
                        affordableUnits,
                        Math.min(
                                cargoUnits,
                                availableUnits
                        )
                );

        if (units <= 0) {
            cancelTrade(trader);
            return;
        }

        int purchaseAmount =
                (int) units;

        int purchaseCost =
                purchaseAmount
                        * trade.getBuyPrice();

        market.setInventory(
                market.getInventory()
                        - purchaseAmount
        );

        trader.setCredits(
                trader.getCredits()
                        - purchaseCost
        );

        trader.setCargoCommodity(
                trade.getCommodity()
        );

        trader.setCargoAmount(
                purchaseAmount
        );

        marketRepository.save(market);

        int travelTicks =
                travelService.calculateTravelTicks(
                        trade.getBuySystem(),
                        trade.getSellSystem()
                );

        trader.setTravelTicksRemaining(travelTicks);
        trader.setTotalTravelTicks(travelTicks);

        trader.setStatus(
                TraderStatus.TRAVELING_TO_SELL
        );
    }

    /**
     * Advances a trader toward its sell location.
     *
     * @param trader The trader to update.
     */
    private void travelToSell(Trader trader) {

        int remaining =
                trader.getTravelTicksRemaining() - 1;

        trader.setTravelTicksRemaining(remaining);

        if (remaining > 0) {
            return;
        }

        trader.setCurrentSystem(
                trader.getCurrentTrade().getSellSystem()
        );

        trader.setStatus(TraderStatus.SELLING);
    }

    /**
     * Sells the trader's cargo at the destination market.
     *
     * @param trader The trader executing the sale.
     */
    private void sell(Trader trader) {

        TradeOpportunity trade =
                trader.getCurrentTrade();

        Market market =
                findMarket(
                        trade.getSellSystem(),
                        trade.getCommodity()
                );

        if (market == null) {
            cancelTrade(trader);
            return;
        }

        int cargoAmount =
                trader.getCargoAmount();

        int saleRevenue =
                cargoAmount
                        * trade.getSellPrice();

        market.setInventory(
                market.getInventory()
                        + cargoAmount
        );

        trader.setCredits(
                trader.getCredits()
                        + saleRevenue
        );

        trader.setCargoCommodity(null);
        trader.setCargoAmount(0);

        marketRepository.save(market);

        tradeOpportunityRepository.delete(trade);

        trader.setCurrentTrade(null);

        trader.setStatus(
                TraderStatus.IDLE
        );
    }

    /**
     * Finds the market for a commodity within a star system.
     *
     * @param system The star system containing the market.
     * @param commodity The commodity traded by the market.
     * @return The matching market, or {@code null} if none exists.
     */
    private Market findMarket(
            StarSystem system,
            CommodityType commodity) {

         return marketRepository
                .findByStarSystemIdAndCommodityType(
                        system.getId(),
                        commodity
                )
                .orElse(null);
    }

    /**
     * Cancels a trader's current trade and returns it to the idle state.
     *
     * @param trader The trader whose trade should be cancelled.
     */
    private void cancelTrade(Trader trader) {

        TradeOpportunity trade =
                trader.getCurrentTrade();

        if (trade != null) {
            tradeOpportunityRepository.delete(trade);
        }

        trader.setCurrentTrade(null);
        trader.setCargoCommodity(null);
        trader.setCargoAmount(0);
        trader.setTravelTicksRemaining(0);

        trader.setStatus(TraderStatus.IDLE);
    }
}
