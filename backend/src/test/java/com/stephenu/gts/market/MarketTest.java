package com.stephenu.gts.market;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemEconomicProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketTest {

    private StarSystem system;
    private StarSystemEconomicProfile economicProfile;
    private Commodity commodity;

    @BeforeEach
    void setUp() {
        system = new StarSystem();
        economicProfile = new StarSystemEconomicProfile();
        commodity = createCommodity(CommodityType.MANUFACTURED_PARTS, 100);

        system.setEconomicProfile(economicProfile);
    }

    @Test
    void constructorSetsStarSystemAndCommodity() {

        Market market = new Market(system, commodity);

        assertEquals(system, market.getStarSystem());
        assertEquals(commodity, market.getCommodity());
    }

    @Test
    void targetInventoryUsesProductionWhenGreaterThanConsumption() {

        economicProfile.getExtractionCapacity()
                .put(CommodityType.MANUFACTURED_PARTS, 20.0);

        economicProfile.getConsumption()
                .put(CommodityType.MANUFACTURED_PARTS, 10.0);

        Market market = new Market(system, commodity);

        assertEquals(600, market.getTargetInventory());
    }

    @Test
    void targetInventoryUsesConsumptionWhenGreaterThanProduction() {

        economicProfile.getExtractionCapacity()
                .put(CommodityType.MANUFACTURED_PARTS, 10.0);

        economicProfile.getConsumption()
                .put(CommodityType.MANUFACTURED_PARTS, 20.0);

        Market market = new Market(system, commodity);

        assertEquals(600, market.getTargetInventory());
    }

    @Test
    void priceIncreasesWhenStartingInventoryIsBelowTarget() {

        economicProfile.getExtractionCapacity()
                .put(CommodityType.MANUFACTURED_PARTS, 100.0);

        Market market = new Market(system, commodity);

        assertTrue(market.getPrice() > commodity.getBasePrice());
    }

    @Test
    void priceUsesBasePriceWhenTargetInventoryIsZero() {

        Market market = new Market(system, commodity);

        assertEquals(
                commodity.getBasePrice(),
                market.getPrice()
        );

        assertEquals(0, market.getTargetInventory());
        assertEquals(0, market.getInventory());
    }

    @Test
    void priceIsBoundedByDoubleBasePrice() {

        economicProfile.getExtractionCapacity()
                .put(CommodityType.MANUFACTURED_PARTS, 100.0);

        Market market = new Market(system, commodity);

        assertTrue(
                market.getPrice() <= commodity.getBasePrice() * 2
        );
    }

    private Commodity createCommodity(
            CommodityType type,
            int basePrice) {

        Commodity commodity = new Commodity();
        commodity.setType(type);
        commodity.setBasePrice(basePrice);

        return commodity;
    }
}
