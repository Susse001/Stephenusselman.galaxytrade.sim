package com.stephenu.gts.simulation;

import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.trader.Trader;
import com.stephenu.gts.trader.TraderRepository;
import com.stephenu.gts.trader.TraderStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

/**
 * Coordinates each simulation tick.
 *
 * Every tick updates market conditions before advancing each trader
 * through its current state. Market prices, supply, demand, and trader
 * actions are processed once per simulation cycle.
 */
@Service
@RequiredArgsConstructor
public class SimulationService {
    
    private final TraderRepository traderRepository;
    private final MarketRepository marketRepository;
    private final TradeOpportunityRepository tradeOpportunityRepository;
    private final TraderDecisionService traderDecisionService;
	private final TravelService travelService;

    private long currentTick = 0;
    private final Random random = new Random();

    /**
	 * Advances the simulation by one tick.
	 *
	 * Each tick updates market conditions before processing trader behavior
	 * to ensure all trading decisions are based on the latest market data.
	 *
	 * @return The current simulation tick.
	 */
    @Transactional
    public long runTick() {

        currentTick++;

        updateMarkets();
        evaluateTraders();

        return currentTick;
    }

    public long getCurrentTick() {
        return currentTick;
    }

	/**
	 * Updates every market in the simulation.
	 */
    private void updateMarkets() {

    List<Market> markets = marketRepository.findAll();

        for (Market market : markets) {
            updateSupplyAndDemand(market);
            updatePrice(market);
        }

        marketRepository.saveAll(markets);
    }

	/**
	 * Applies random supply and demand fluctuations to a market.
	 *
	 * @param market The market to update.
	 */
    private void updateSupplyAndDemand(Market market) {

        int supplyChange = random.nextInt(41) - 20;

        int demandChange = random.nextInt(41) - 20;

        market.setSupply(
                Math.max(
                        1,
                        market.getSupply() + supplyChange
                )
        );

        market.setDemand(
                Math.max(
                        1,
                        market.getDemand() + demandChange
                )
        );
    }

	/**
	 * Recalculates a market's price.
	 *
	 * Prices increase when demand exceeds supply and decrease when supply
	 * exceeds demand. A minimum price is enforced.
	 *
	 * @param market The market to update.
	 */
    private void updatePrice(Market market) {

        int price = market.getPrice();

        if (market.getDemand() > market.getSupply()) {
            price += 5;
        }

        if (market.getSupply() > market.getDemand()) {
            price -= 5;
        }

        market.setPrice(Math.max(10, price));
    }

	/**
	 * Advances every trader by one simulation step.
	 */
    private void evaluateTraders() {

        List<Trader> traders = traderRepository.findAll();

        for (Trader trader : traders) {
            processTrader(trader);
        }

        traderRepository.saveAll(traders);
    }

	/**
	 * Advances a trader according to its current simulation state.
	 *
	 * Each trader follows a simple state machine that governs trade
	 * selection, travel, buying, and selling.
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
	 * Assigns the most profitable trade opportunity to an idle trader.
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

        trader.setCurrentSystem(trader.getCurrentTrade().getBuySystem());

        trader.setStatus(TraderStatus.BUYING);
    }

	/**
	 * Purchases cargo and begins travel to the destination market.
	 *
	 * @param trader The trader to update.
	 */
    private void buy(Trader trader) {

        TradeOpportunity trade = trader.getCurrentTrade();

        trader.setCargoCommodity(trade.getCommodity());
        trader.setCargoAmount(trader.getCargoCapacity());

        int travelTicks =
                travelService.calculateTravelTicks(
                        trade.getBuySystem(),
                        trade.getSellSystem()
                );

        trader.setTravelTicksRemaining(travelTicks);
		trader.setTotalTravelTicks(travelTicks);

        trader.setStatus(TraderStatus.TRAVELING_TO_SELL);
    }

	/**
	 * Advances a trader toward its sell location.
	 *
	 * @param trader The trader to update.
	 */
    private void travelToSell(Trader trader) {

        int remaining = trader.getTravelTicksRemaining() - 1;
        trader.setTravelTicksRemaining(remaining);

        if (remaining > 0) {return;}

        trader.setCurrentSystem(trader.getCurrentTrade().getSellSystem());

        trader.setStatus(TraderStatus.SELLING);
    }

	/**
	 * Completes the current trade and returns the trader to the idle state.
	 *
	 * @param trader The trader to update.
	 */
    private void sell(Trader trader) {

        TradeOpportunity trade = trader.getCurrentTrade();

        trader.setCredits(
                trader.getCredits()
                        + trade.getExpectedProfitPerUnit() * trader.getCargoAmount()
        );

        trader.setCargoCommodity(null);
        trader.setCargoAmount(0);

        tradeOpportunityRepository.delete(trade);

        trader.setCurrentTrade(null);

        trader.setStatus(
                TraderStatus.IDLE
        );
    }

}
