package com.stephenu.gts.simulation;

import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.trader.Trader;
import com.stephenu.gts.trader.TraderRepository;
import com.stephenu.gts.trader.TraderStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationService {
    
    private final TraderRepository traderRepository;
    private final MarketRepository marketRepository;
    private final TradeOpportunityRepository tradeOpportunityRepository;
    private final TraderDecisionService traderDecisionService;

    private long currentTick = 0;
    private final Random random = new Random();

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

    private void updateMarkets() {

    List<Market> markets = marketRepository.findAll();

        for (Market market : markets) {

            updateSupplyAndDemand(market);

            updatePrice(market);
        }

        marketRepository.saveAll(markets);
    }

    private void updateSupplyAndDemand(Market market) {

        int supplyChange =
                random.nextInt(41) - 20;

        int demandChange =
                random.nextInt(41) - 20;

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

    private void updatePrice(Market market) {

        int price = market.getPrice();

        if (market.getDemand() > market.getSupply()) {
            price += 5;
        }

        if (market.getSupply() > market.getDemand()) {
            price -= 5;
        }

        market.setPrice(
                Math.max(10, price)
        );
    }

    private void evaluateTraders() {

        List<Trader> traders =
                traderRepository.findAll();

        for (Trader trader : traders) {

                processTrader(trader);
        }

        traderRepository.saveAll(traders);
    }

    private void processTrader(Trader trader) {

        switch (trader.getStatus()) {

            case IDLE -> assignTrade(trader);

            case TRAVELING_TO_BUY -> travelToBuy(trader);

            case BUYING -> buy(trader);

            case TRAVELING_TO_SELL -> travelToSell(trader);

            case SELLING -> sell(trader);
        }
    }

    private void assignTrade(Trader trader) {

        TradeOpportunity opportunity =
            traderDecisionService.findBestTrade(trader);

        if (opportunity == null) {
            return;
        }

        tradeOpportunityRepository.save(opportunity);

        trader.setCurrentTrade(opportunity);

        trader.setTravelTicksRemaining(
                calculateTravelTicks(
                        trader.getCurrentSystem(),
                        opportunity.getBuySystem()
                )
        );

        trader.setStatus(
                TraderStatus.TRAVELING_TO_BUY
        );
    }

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

        trader.setStatus(
                TraderStatus.BUYING
        );
    }

    private void buy(Trader trader) {

        TradeOpportunity trade =
            trader.getCurrentTrade();

        trader.setCargoCommodity(
                trade.getCommodity()
        );

        trader.setCargoAmount(
                trader.getCargoCapacity()
        );

        trader.setTravelTicksRemaining(
                calculateTravelTicks(
                        trade.getBuySystem(),
                        trade.getSellSystem()
                )
        );

        trader.setStatus(
                TraderStatus.TRAVELING_TO_SELL
        );
    }

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

        trader.setStatus(
                TraderStatus.SELLING
        );
    }

    private void sell(Trader trader) {

        TradeOpportunity trade =
                trader.getCurrentTrade();

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

        private int calculateTravelTicks(
            StarSystem from,
            StarSystem to) {

        double dx =
                from.getXCoordinate() - to.getXCoordinate();

        double dy =
                from.getYCoordinate() - to.getYCoordinate();

        double distance =
                Math.sqrt(dx * dx + dy * dy);

        return Math.max(
                1,
                (int) Math.ceil(distance / 7.5)
        );
    }
}
