package com.stephenu.gts.planet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stephenu.gts.commodity.CommodityType;

class PlanetConsumptionProfileTest {

    private PlanetConsumptionProfile generator;

    @BeforeEach
    void setUp() {
        generator = new PlanetConsumptionProfile();
    }

    private Planet createPlanet(
            PopulationLevel population,
            DevelopmentLevel development) {

        Planet planet = new Planet();

        planet.setPopulation(population);
        planet.setDevelopment(development);

        return planet;
    }

    @Test
    void shouldGenerateBaselineConsumptionWhenPopulationAndDevelopmentAreNull() {

        Planet planet = createPlanet(null, null);

        Map<CommodityType, Double> baseline =
                generator.createBaselineConsumption();

        PlanetConsumptionProfile profile =
                generator.generateConsumption(planet);

        Map<CommodityType, Double> consumption =
                profile.getConsumption();

        assertEquals(
                baseline.get(CommodityType.FOOD),
                consumption.get(CommodityType.FOOD)
        );

        assertEquals(
                baseline.get(CommodityType.WATER),
                consumption.get(CommodityType.WATER)
        );

        assertEquals(
                baseline.get(CommodityType.COMMON_METALS),
                consumption.get(CommodityType.COMMON_METALS)
        );

        assertEquals(
                baseline.get(CommodityType.FUEL),
                consumption.get(CommodityType.FUEL)
        );

        assertEquals(
                baseline.get(CommodityType.LUXURY_GOODS),
                consumption.get(CommodityType.LUXURY_GOODS)
        );
    }

    @Test
    void tensOfMillionsShouldApplyTenPercentPopulationMultiplier() {

        Planet planet =
                createPlanet(
                        PopulationLevel.TENS_OF_MILLIONS,
                        DevelopmentLevel.DEVELOPING
                );

        Map<CommodityType, Double> baseline =
                generator.createBaselineConsumption();

        PlanetConsumptionProfile profile =
                generator.generateConsumption(planet);

        assertEquals(
                baseline.get(CommodityType.FOOD) * 0.10,
                profile.getConsumption().get(CommodityType.FOOD)
        );

        assertEquals(
                baseline.get(CommodityType.WATER) * 0.10,
                profile.getConsumption().get(CommodityType.WATER)
        );
    }

    @Test
    void billionsShouldApplyBaselinePopulationMultiplier() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING
                );

        Map<CommodityType, Double> baseline =
                generator.createBaselineConsumption();

        PlanetConsumptionProfile profile =
                generator.generateConsumption(planet);

        assertEquals(
                baseline.get(CommodityType.FOOD),
                profile.getConsumption().get(CommodityType.FOOD)
        );
    }

    @Test
    void colonialDevelopmentShouldModifyConsumption() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.COLONIAL
                );

        Map<CommodityType, Double> baseline =
                generator.createBaselineConsumption();

        PlanetConsumptionProfile profile =
                generator.generateConsumption(planet);

        Map<CommodityType, Double> consumption =
                profile.getConsumption();

        assertEquals(
                baseline.get(CommodityType.FUEL) * 1.30,
                consumption.get(CommodityType.FUEL)
        );

        assertEquals(
                baseline.get(CommodityType.FOOD) * 0.80,
                consumption.get(CommodityType.FOOD)
        );

        assertEquals(
                baseline.get(CommodityType.LUXURY_GOODS) * 0.20,
                consumption.get(CommodityType.LUXURY_GOODS)
        );
    }

    @Test
    void industrialDevelopmentShouldIncreaseIndustrialConsumption() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.INDUSTRIAL
                );

        Map<CommodityType, Double> baseline =
                generator.createBaselineConsumption();

        PlanetConsumptionProfile profile =
                generator.generateConsumption(planet);

        Map<CommodityType, Double> consumption =
                profile.getConsumption();

        assertEquals(
                baseline.get(CommodityType.FUEL) * 1.20,
                consumption.get(CommodityType.FUEL)
        );

        assertEquals(
                baseline.get(CommodityType.INDUSTRIAL_MACHINERY) * 1.40,
                consumption.get(CommodityType.INDUSTRIAL_MACHINERY)
        );

        assertEquals(
                baseline.get(CommodityType.LUXURY_GOODS) * 1.20,
                consumption.get(CommodityType.LUXURY_GOODS)
        );
    }

    @Test
    void populationAndDevelopmentShouldApplyTogether() {

        Planet planet =
                createPlanet(
                        PopulationLevel.HUNDREDS_OF_MILLIONS,
                        DevelopmentLevel.INDUSTRIAL
                );

        Map<CommodityType, Double> baseline =
                generator.createBaselineConsumption();

        PlanetConsumptionProfile profile =
                generator.generateConsumption(planet);

        Map<CommodityType, Double> consumption =
                profile.getConsumption();

        assertEquals(
                baseline.get(CommodityType.FUEL)
                        * 0.30
                        * 1.20,
                consumption.get(CommodityType.FUEL)
        );

        assertEquals(
                baseline.get(CommodityType.FOOD)
                        * 0.30,
                consumption.get(CommodityType.FOOD)
        );
    }
}
