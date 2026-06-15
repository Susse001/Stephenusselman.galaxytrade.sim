package com.stephenu.gts.simulation;

import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.trader.Trader;
import com.stephenu.gts.trader.TraderRepository;

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

            TradeOpportunity opportunity =
                    traderDecisionService.findBestTrade();

            trader.setTargetCommodity(
                    opportunity.commodity()
            );

            trader.setTargetSystem(
                    opportunity.sellSystem()
            );

            System.out.println(
                trader.getName()
                        + " selected "
                        + opportunity.commodity()
                        + " -> "
                        + opportunity.sellSystem().getName()
                        + " profit="
                        + opportunity.expectedProfit()
            );
        }

        traderRepository.saveAll(traders);
    }

}
