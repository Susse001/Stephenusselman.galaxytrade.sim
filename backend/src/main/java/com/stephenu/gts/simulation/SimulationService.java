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

            case TRAVELING -> travel(trader);

            case BUYING -> buy(trader);

            case SELLING -> sell(trader);
        }
    }

    private void assignTrade(Trader trader) {

        TradeOpportunity opportunity =
                traderDecisionService.findBestTrade();

        if (opportunity == null) {
                return;
        }

        tradeOpportunityRepository.save(opportunity);

        trader.setCurrentTrade(opportunity);
        trader.setStatus(TraderStatus.TRAVELING);
    }

    private void travel(Trader trader) {

        TradeOpportunity trade =
            trader.getCurrentTrade();


        trader.setCurrentSystem(
                trade.getBuySystem()
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

        trader.setStatus(
                TraderStatus.SELLING
        );
    }

    private void sell(Trader trader) {

        TradeOpportunity trade =
            trader.getCurrentTrade();

        trader.setCurrentSystem(
                trade.getSellSystem()
        );

        trader.setCredits(
                trader.getCredits()
                + trade.getExpectedProfit()
        );

        trader.setCargoCommodity(null);
        trader.setCargoAmount(0);

        trader.setStatus(
                TraderStatus.IDLE
        );
    }

}
