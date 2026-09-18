package com.stephenu.gts.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.trader.Trader;
import com.stephenu.gts.trader.TraderRepository;
import com.stephenu.gts.trader.TraderStatus;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private TraderRepository traderRepository;

    @Mock
    private MarketRepository marketRepository;

    @Mock
    private TradeOpportunityRepository tradeOpportunityRepository;

    @Mock
    private TraderDecisionService traderDecisionService;

    @Mock
    private TravelService travelService;

    @Mock
    private MarketUpdater marketUpdater;

    private SimulationService simulationService;

    @BeforeEach
    void setUp() {
        simulationService = new SimulationService(
                traderRepository,
                marketRepository,
                tradeOpportunityRepository,
                traderDecisionService,
                travelService,
                marketUpdater
        );
    }

    @Test
    void runTick_incrementsTickAndUpdatesMarketsAndTraders() {
        when(traderRepository.findAll())
                .thenReturn(List.of());

        long tick = simulationService.runTick();

        assertEquals(1, tick);
        assertEquals(1, simulationService.getCurrentTick());

        verify(marketUpdater).updateMarkets();
        verify(traderRepository).findAll();
        verify(traderRepository).saveAll(List.of());
    }

    @Test
    void runTick_incrementsTickOnEachCall() {
        when(traderRepository.findAll())
                .thenReturn(List.of());

        simulationService.runTick();
        simulationService.runTick();
        simulationService.runTick();

        assertEquals(3, simulationService.getCurrentTick());

        verify(marketUpdater, times(3))
                .updateMarkets();
    }

    @Test
    void idleTrader_withOpportunity_assignsTradeAndStartsTravel() {
        Trader trader = createTrader();
        TradeOpportunity opportunity =
                createTradeOpportunity();

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(traderDecisionService.findBestTrade(trader))
                .thenReturn(opportunity);

        when(travelService.calculateTravelTicks(
                trader.getCurrentSystem(),
                opportunity.getBuySystem()))
                .thenReturn(3);

        simulationService.runTick();

        assertSame(opportunity, trader.getCurrentTrade());
        assertEquals(3, trader.getTravelTicksRemaining());
        assertEquals(3, trader.getTotalTravelTicks());
        assertEquals(
                TraderStatus.TRAVELING_TO_BUY,
                trader.getStatus()
        );

        verify(tradeOpportunityRepository)
                .save(opportunity);
    }

    @Test
    void idleTrader_withoutOpportunity_remainsIdle() {
        Trader trader = createTrader();

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(traderDecisionService.findBestTrade(trader))
                .thenReturn(null);

        simulationService.runTick();

        assertEquals(
                TraderStatus.IDLE,
                trader.getStatus()
        );
        assertNull(trader.getCurrentTrade());

        verify(tradeOpportunityRepository, never())
                .save(any());
    }

    @Test
    void travelingToBuy_withTicksRemaining_decrementsTravelTicks() {
        Trader trader = createTrader();
        TradeOpportunity opportunity =
                createTradeOpportunity();

        trader.setCurrentTrade(opportunity);
        trader.setTravelTicksRemaining(3);
        trader.setStatus(
                TraderStatus.TRAVELING_TO_BUY
        );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        simulationService.runTick();

        assertEquals(2, trader.getTravelTicksRemaining());
        assertEquals(
                TraderStatus.TRAVELING_TO_BUY,
                trader.getStatus()
        );
    }

    @Test
    void travelingToBuy_onArrival_entersBuyingState() {
        Trader trader = createTrader();
        TradeOpportunity opportunity =
                createTradeOpportunity();

        trader.setCurrentTrade(opportunity);
        trader.setTravelTicksRemaining(1);
        trader.setStatus(
                TraderStatus.TRAVELING_TO_BUY
        );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        simulationService.runTick();

        assertEquals(0, trader.getTravelTicksRemaining());
        assertSame(
                opportunity.getBuySystem(),
                trader.getCurrentSystem()
        );
        assertEquals(
                TraderStatus.BUYING,
                trader.getStatus()
        );
    }

    @Test
    void buying_respectsCreditsCapacityAndInventory() {
        Trader trader = createTrader();
        trader.setCredits((long) 500);
        trader.setCargoCapacity(10);
        trader.setCargoAmount(0);
        trader.setStatus(TraderStatus.BUYING);

        TradeOpportunity opportunity =
                createTradeOpportunity();

        opportunity.setBuyPrice(40);
        opportunity.setSellPrice(100);

        trader.setCurrentTrade(opportunity);

        Market market =
                createMarket(
                        opportunity.getBuySystem(),
                        opportunity.getCommodity(),
                        20
                );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(marketRepository
                .findByStarSystemIdAndCommodityType(
                        opportunity.getBuySystem().getId(),
                        opportunity.getCommodity()
                ))
                .thenReturn(Optional.of(market));

        when(travelService.calculateTravelTicks(
                opportunity.getBuySystem(),
                opportunity.getSellSystem()))
                .thenReturn(4);

        simulationService.runTick();

        // 500 / 40 = 12 affordable,
        // but cargo capacity limits this to 10.
        assertEquals(100, trader.getCredits());
        assertEquals(10, trader.getCargoAmount());
        assertEquals(
                opportunity.getCommodity(),
                trader.getCargoCommodity()
        );

        assertEquals(10, market.getInventory());

        assertEquals(4, trader.getTravelTicksRemaining());
        assertEquals(4, trader.getTotalTravelTicks());
        assertEquals(
                TraderStatus.TRAVELING_TO_SELL,
                trader.getStatus()
        );

        verify(marketRepository).save(market);
    }

    @Test
    void buying_respectsMarketInventory() {
        Trader trader = createTrader();
        trader.setCredits((long) 1000);
        trader.setCargoCapacity(20);
        trader.setStatus(TraderStatus.BUYING);

        TradeOpportunity opportunity =
                createTradeOpportunity();

        opportunity.setBuyPrice(10);
        opportunity.setSellPrice(20);

        trader.setCurrentTrade(opportunity);

        Market market =
                createMarket(
                        opportunity.getBuySystem(),
                        opportunity.getCommodity(),
                        6
                );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(marketRepository
                .findByStarSystemIdAndCommodityType(
                        opportunity.getBuySystem().getId(),
                        opportunity.getCommodity()
                ))
                .thenReturn(Optional.of(market));

        when(travelService.calculateTravelTicks(
                opportunity.getBuySystem(),
                opportunity.getSellSystem()))
                .thenReturn(2);

        simulationService.runTick();

        assertEquals(940, trader.getCredits());
        assertEquals(6, trader.getCargoAmount());
        assertEquals(0, market.getInventory());

        assertEquals(
                TraderStatus.TRAVELING_TO_SELL,
                trader.getStatus()
        );
    }

    @Test
    void buying_withNoAvailableUnits_cancelsTrade() {
        Trader trader = createTrader();
        trader.setCredits((long) 500);
        trader.setCargoCapacity(10);
        trader.setStatus(TraderStatus.BUYING);

        TradeOpportunity opportunity =
                createTradeOpportunity();

        opportunity.setBuyPrice(50);

        trader.setCurrentTrade(opportunity);

        Market market =
                createMarket(
                        opportunity.getBuySystem(),
                        opportunity.getCommodity(),
                        0
                );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(marketRepository
                .findByStarSystemIdAndCommodityType(
                        opportunity.getBuySystem().getId(),
                        opportunity.getCommodity()
                ))
                .thenReturn(Optional.of(market));

        simulationService.runTick();

        assertEquals(
                TraderStatus.IDLE,
                trader.getStatus()
        );
        assertNull(trader.getCurrentTrade());
        assertNull(trader.getCargoCommodity());
        assertEquals(0, trader.getCargoAmount());
        assertEquals(0, trader.getTravelTicksRemaining());

        verify(tradeOpportunityRepository)
                .delete(opportunity);
    }

    @Test
    void travelingToSell_onArrival_entersSellingState() {
        Trader trader = createTrader();
        TradeOpportunity opportunity =
                createTradeOpportunity();

        trader.setCurrentTrade(opportunity);
        trader.setCargoCommodity(
                opportunity.getCommodity()
        );
        trader.setCargoAmount(5);
        trader.setTravelTicksRemaining(1);
        trader.setStatus(
                TraderStatus.TRAVELING_TO_SELL
        );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        simulationService.runTick();

        assertEquals(0, trader.getTravelTicksRemaining());
        assertSame(
                opportunity.getSellSystem(),
                trader.getCurrentSystem()
        );
        assertEquals(
                TraderStatus.SELLING,
                trader.getStatus()
        );
    }

    @Test
    void selling_transfersCargoAndRevenueAndCompletesTrade() {
        Trader trader = createTrader();
        trader.setCredits((long) 100);
        trader.setCargoAmount(5);
        trader.setStatus(TraderStatus.SELLING);

        TradeOpportunity opportunity =
                createTradeOpportunity();

        opportunity.setBuyPrice(20);
        opportunity.setSellPrice(50);

        trader.setCurrentTrade(opportunity);
        trader.setCargoCommodity(
                opportunity.getCommodity()
        );

        Market market =
                createMarket(
                        opportunity.getSellSystem(),
                        opportunity.getCommodity(),
                        10
                );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(marketRepository
                .findByStarSystemIdAndCommodityType(
                        opportunity.getSellSystem().getId(),
                        opportunity.getCommodity()
                ))
                .thenReturn(Optional.of(market));

        simulationService.runTick();

        assertEquals(350, trader.getCredits());
        assertEquals(15, market.getInventory());

        assertNull(trader.getCargoCommodity());
        assertEquals(0, trader.getCargoAmount());
        assertNull(trader.getCurrentTrade());

        assertEquals(
                TraderStatus.IDLE,
                trader.getStatus()
        );

        verify(marketRepository).save(market);
        verify(tradeOpportunityRepository)
                .delete(opportunity);
    }

    @Test
    void buying_withMissingMarket_cancelsTrade() {
        Trader trader = createTrader();
        trader.setStatus(TraderStatus.BUYING);

        TradeOpportunity opportunity =
                createTradeOpportunity();

        trader.setCurrentTrade(opportunity);

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(marketRepository
                .findByStarSystemIdAndCommodityType(
                        opportunity.getBuySystem().getId(),
                        opportunity.getCommodity()
                ))
                .thenReturn(Optional.empty());

        simulationService.runTick();

        assertEquals(
                TraderStatus.IDLE,
                trader.getStatus()
        );
        assertNull(trader.getCurrentTrade());
        assertEquals(0, trader.getCargoAmount());

        verify(tradeOpportunityRepository)
                .delete(opportunity);
    }

    @Test
    void selling_withMissingMarket_cancelsTrade() {
        Trader trader = createTrader();
        trader.setCargoAmount(5);
        trader.setStatus(TraderStatus.SELLING);

        TradeOpportunity opportunity =
                createTradeOpportunity();

        trader.setCurrentTrade(opportunity);
        trader.setCargoCommodity(
                opportunity.getCommodity()
        );

        when(traderRepository.findAll())
                .thenReturn(List.of(trader));

        when(marketRepository
                .findByStarSystemIdAndCommodityType(
                        opportunity.getSellSystem().getId(),
                        opportunity.getCommodity()
                ))
                .thenReturn(Optional.empty());

        simulationService.runTick();

        assertEquals(
                TraderStatus.IDLE,
                trader.getStatus()
        );
        assertNull(trader.getCurrentTrade());
        assertNull(trader.getCargoCommodity());
        assertEquals(0, trader.getCargoAmount());

        verify(tradeOpportunityRepository)
                .delete(opportunity);
    }

    private Trader createTrader() {
        Trader trader = new Trader();

        trader.setCredits((long) 1000);
        trader.setCargoCapacity(10);
        trader.setCargoAmount(0);
        trader.setStatus(TraderStatus.IDLE);

        StarSystem currentSystem = createSystem(1L);
        trader.setCurrentSystem(currentSystem);

        return trader;
    }

    private TradeOpportunity createTradeOpportunity() {
        StarSystem buySystem = createSystem(2L);
        StarSystem sellSystem = createSystem(3L);

        TradeOpportunity opportunity =
                new TradeOpportunity();

        opportunity.setCommodity(
                CommodityType.FOOD
        );
        opportunity.setBuySystem(buySystem);
        opportunity.setSellSystem(sellSystem);
        opportunity.setBuyPrice(20);
        opportunity.setSellPrice(50);
        opportunity.setExpectedProfitPerUnit(30);

        return opportunity;
    }

    private Market createMarket(
            StarSystem system,
            CommodityType commodityType,
            int inventory) {

        Commodity commodity = new Commodity();
        commodity.setType(commodityType);
        commodity.setBasePrice(100);

        Market market = new Market();

        market.setStarSystem(system);
        market.setCommodity(commodity);
        market.setInventory(inventory);
        market.setTargetInventory(100);
        market.setPrice(100);

        return market;
    }

    private StarSystem createSystem(Long id) {
        StarSystem system = new StarSystem();
        system.setId(id);
        return system;
    }
}
