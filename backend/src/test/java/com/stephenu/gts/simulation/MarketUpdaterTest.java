package com.stephenu.gts.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import com.stephenu.gts.starsystem.StarSystemEconomicProfile;

@ExtendWith(MockitoExtension.class)
class MarketUpdaterTest {

    @Mock
    private MarketRepository marketRepository;

    private MarketUpdater marketUpdater;

    @BeforeEach
    void setUp() {
        marketUpdater = new MarketUpdater(marketRepository);
    }

    @Test
    void updateMarkets_updatesAllMarketsAndSavesThem() {
        Market market1 = createMarket(
                CommodityType.FOOD,
                100,
                200,
                20.0,
                5.0
        );

        Market market2 = createMarket(
                CommodityType.WATER,
                150,
                300,
                10.0,
                20.0
        );

        List<Market> markets =
                List.of(market1, market2);

        when(marketRepository.findAll())
                .thenReturn(markets);

        marketUpdater.updateMarkets();

        assertEquals(115, market1.getInventory());
        assertEquals(140, market2.getInventory());

        verify(marketRepository).findAll();
        verify(marketRepository).saveAll(markets);
    }

    @Test
    void updateMarkets_increasesInventoryWhenProductionExceedsConsumption() {
        Market market = createMarket(
                CommodityType.FOOD,
                100,
                200,
                25.0,
                10.0
        );

        when(marketRepository.findAll())
                .thenReturn(List.of(market));

        marketUpdater.updateMarkets();

        assertEquals(115, market.getInventory());
    }

    @Test
    void updateMarkets_decreasesInventoryWhenConsumptionExceedsProduction() {
        Market market = createMarket(
                CommodityType.FOOD,
                100,
                200,
                10.0,
                25.0
        );

        when(marketRepository.findAll())
                .thenReturn(List.of(market));

        marketUpdater.updateMarkets();

        assertEquals(85, market.getInventory());
    }

    @Test
    void updateMarkets_doesNotAllowInventoryBelowZero() {
        Market market = createMarket(
                CommodityType.FOOD,
                10,
                200,
                5.0,
                50.0
        );

        when(marketRepository.findAll())
                .thenReturn(List.of(market));

        marketUpdater.updateMarkets();

        assertEquals(0, market.getInventory());
    }

    @Test
    void updateMarkets_roundsFractionalInventoryChange() {
        Market market = createMarket(
                CommodityType.FOOD,
                100,
                200,
                10.4,
                5.1
        );

        when(marketRepository.findAll())
                .thenReturn(List.of(market));

        marketUpdater.updateMarkets();

        assertEquals(105, market.getInventory());
    }

    private Market createMarket(
            CommodityType commodityType,
            int inventory,
            int targetInventory,
            double production,
            double consumption) {

        StarSystemEconomicProfile profile =
                new StarSystemEconomicProfile();

        profile.getExtractionCapacity()
                .put(commodityType, production);

        profile.getConsumption()
                .put(commodityType, consumption);

        StarSystem system = new StarSystem();
        system.setEconomicProfile(profile);

        Commodity commodity = new Commodity();
        commodity.setType(commodityType);
        commodity.setBasePrice(100);

        Market market = new Market();

        market.setStarSystem(system);
        market.setCommodity(commodity);
        market.setInventory(inventory);
        market.setTargetInventory(targetInventory);

        return market;
    }
}
