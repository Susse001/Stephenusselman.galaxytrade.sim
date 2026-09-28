package com.stephenu.gts.planet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityType;

class PlanetProductionProfileTest {

    private PlanetProductionProfileGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PlanetProductionProfileGenerator();
    }

    private Planet createPlanet(
            PopulationLevel population,
            DevelopmentLevel development,
            InfrastructureLevel infrastructure) {

        Planet planet = new Planet();

        planet.setPopulation(population);
        planet.setDevelopment(development);
        planet.setInfrastructure(infrastructure);

        return planet;
    }

    private Commodity createCommodity(
            CommodityType commodityType) {

        Commodity commodity = new Commodity();

        commodity.setType(commodityType);

        return commodity;
    }

    private void addResource(
            Planet planet,
            CommodityType commodityType,
            ResourceLevel resourceLevel) {

        Commodity commodity =
                createCommodity(commodityType);

        PlanetResource resource =
                new PlanetResource(
                        planet,
                        commodity,
                        resourceLevel
                );

        planet.getResources().add(resource);
    }

    private void addAllResources(
            Planet planet,
            ResourceLevel resourceLevel) {

        for (CommodityType commodityType :
                generator.getBaseExtraction().keySet()) {

            addResource(
                    planet,
                    commodityType,
                    resourceLevel
            );
        }
    }

    @Test
    void shouldGenerateBaselineExtractionForAverageResources() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        Map<CommodityType, Double> extraction =
                profile.getExtractionCapacity();

        Map<CommodityType, Double> baseline =
                generator.getBaseExtraction();

        assertEquals(
                baseline.get(CommodityType.FOOD)
                        * 1.15,
                extraction.get(CommodityType.FOOD),
                0.0000001
        );

        assertEquals(
                baseline.get(CommodityType.COMMON_METALS)
                        * 1.15,
                extraction.get(CommodityType.COMMON_METALS),
                0.0000001
        );

        assertEquals(
                baseline.get(CommodityType.RARE_ELEMENTS)
                        * 1.15,
                extraction.get(CommodityType.RARE_ELEMENTS),
                0.0000001
        );
    }

    @Test
    void resourceLevelNoneShouldProduceZeroExtraction() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.NONE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        assertEquals(
                0.0,
                profile.getExtractionCapacity()
                        .get(CommodityType.FOOD),
                0.0000001
        );
    }

    @Test
    void scarceResourceShouldReduceExtraction() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.GOOD
                );

        for (CommodityType commodityType :
                generator.getBaseExtraction().keySet()) {

            ResourceLevel level =
                    commodityType == CommodityType.COMMON_METALS
                            ? ResourceLevel.SCARCE
                            : ResourceLevel.AVERAGE;

            addResource(
                    planet,
                    commodityType,
                    level
            );
        }

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseExtraction()
                        .get(CommodityType.COMMON_METALS)
                        * 0.35
                        * 1.15;

        assertEquals(
                expected,
                profile.getExtractionCapacity()
                        .get(CommodityType.COMMON_METALS),
                0.0000001
        );
    }

    @Test
    void richResourceShouldIncreaseExtraction() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.GOOD
                );

        for (CommodityType commodityType :
                generator.getBaseExtraction().keySet()) {

            ResourceLevel level =
                    commodityType == CommodityType.RARE_METALS
                            ? ResourceLevel.RICH
                            : ResourceLevel.AVERAGE;

            addResource(
                    planet,
                    commodityType,
                    level
            );
        }

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseExtraction()
                        .get(CommodityType.RARE_METALS)
                        * 1.75
                        * 1.15;

        assertEquals(
                expected,
                profile.getExtractionCapacity()
                        .get(CommodityType.RARE_METALS),
                0.0000001
        );
    }

    @Test
    void populationShouldModifyExtractionCapacity() {

        Planet planet =
                createPlanet(
                        PopulationLevel.TENS_OF_MILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseExtraction()
                        .get(CommodityType.FOOD)
                        * 0.50
                        * 1.15;

        assertEquals(
                expected,
                profile.getExtractionCapacity()
                        .get(CommodityType.FOOD),
                0.0000001
        );
    }

    @Test
    void developmentShouldModifyExtractionCapacity() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.ADVANCED,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseExtraction()
                        .get(CommodityType.WATER)
                        * 1.20
                        * 1.15;

        assertEquals(
                expected,
                profile.getExtractionCapacity()
                        .get(CommodityType.WATER),
                0.0000001
        );
    }

    @Test
    void infrastructureShouldModifyExtractionCapacity() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.POOR
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseExtraction()
                        .get(CommodityType.INDUSTRIAL_MINERALS)
                        * 0.65;

        assertEquals(
                expected,
                profile.getExtractionCapacity()
                        .get(CommodityType.INDUSTRIAL_MINERALS),
                0.0000001
        );
    }

    @Test
    void allExtractionMultipliersShouldApplyTogether() {

        Planet planet =
                createPlanet(
                        PopulationLevel.TENS_OF_BILLIONS,
                        DevelopmentLevel.ADVANCED,
                        InfrastructureLevel.EXCELLENT
                );

        for (CommodityType commodityType :
                generator.getBaseExtraction().keySet()) {

            ResourceLevel level =
                    commodityType == CommodityType.RARE_ELEMENTS
                            ? ResourceLevel.RICH
                            : ResourceLevel.AVERAGE;

            addResource(
                    planet,
                    commodityType,
                    level
            );
        }

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseExtraction()
                        .get(CommodityType.RARE_ELEMENTS)
                        * 1.75
                        * 1.10
                        * 1.20
                        * 1.40;

        assertEquals(
                expected,
                profile.getExtractionCapacity()
                        .get(CommodityType.RARE_ELEMENTS),
                0.0000001
        );
    }

    @Test
    void shouldThrowExceptionWhenExtractionResourceIsMissing() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        planet.getResources().removeIf(
                resource ->
                        resource.getCommodity().getType()
                                == CommodityType.FOOD
        );

        assertThrows(
                IllegalStateException.class,
                () -> generator.generateProduction(planet)
        );
    }

    @Test
    void shouldGenerateBaselineManufacturingPotential() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.MODEST
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        Map<CommodityType, Double> manufacturing =
                profile.getManufacturingPotential();

        Map<CommodityType, Double> baseline =
                generator.getBaseManufacturing();

        assertEquals(
                baseline.get(CommodityType.REFINED_METALS)
                        * 1.0
                        * 0.90
                        * 1.0,
                manufacturing.get(
                        CommodityType.REFINED_METALS
                ),
                0.0000001
        );
    }

    @Test
    void populationShouldModifyManufacturingPotential() {

        Planet planet =
                createPlanet(
                        PopulationLevel.TENS_OF_MILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseManufacturing()
                        .get(CommodityType.REFINED_METALS)
                        * 0.70
                        * 1.10;

        assertEquals(
                expected,
                profile.getManufacturingPotential()
                        .get(CommodityType.REFINED_METALS),
                0.0000001
        );
    }

    @Test
    void infrastructureShouldModifyManufacturingPotential() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.DEVELOPING,
                        InfrastructureLevel.EXCELLENT
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseManufacturing()
                        .get(CommodityType.REFINED_METALS)
                        * 1.20;

        assertEquals(
                expected,
                profile.getManufacturingPotential()
                        .get(CommodityType.REFINED_METALS),
                0.0000001
        );
    }

    @Test
    void colonialDevelopmentShouldReduceManufacturingPotential() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.COLONIAL,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseManufacturing()
                        .get(CommodityType.CONSUMER_GOODS)
                        * 0.30
                        * 1.10;

        assertEquals(
                expected,
                profile.getManufacturingPotential()
                        .get(CommodityType.CONSUMER_GOODS),
                0.0000001
        );
    }

    @Test
    void industrialDevelopmentShouldIncreaseTierTwoManufacturing() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.INDUSTRIAL,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseManufacturing()
                        .get(CommodityType.REFINED_METALS)
                        * 1.20
                        * 1.10;

        assertEquals(
                expected,
                profile.getManufacturingPotential()
                        .get(CommodityType.REFINED_METALS),
                0.0000001
        );
    }

    @Test
    void manufacturingDevelopmentModifierShouldNotAffectUnlistedCommodity() {

        Planet planet =
                createPlanet(
                        PopulationLevel.BILLIONS,
                        DevelopmentLevel.INDUSTRIAL,
                        InfrastructureLevel.GOOD
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseManufacturing()
                        .get(CommodityType.CONSUMER_GOODS)
                        * 1.10;

        assertEquals(
                expected,
                profile.getManufacturingPotential()
                        .get(CommodityType.CONSUMER_GOODS),
                0.0000001
        );
    }

    @Test
    void populationInfrastructureAndDevelopmentShouldApplyTogetherToManufacturing() {

        Planet planet =
                createPlanet(
                        PopulationLevel.TENS_OF_BILLIONS,
                        DevelopmentLevel.ADVANCED,
                        InfrastructureLevel.EXCELLENT
                );

        addAllResources(
                planet,
                ResourceLevel.AVERAGE
        );

        PlanetProductionProfile profile =
                generator.generateProduction(planet);

        double expected =
                generator.getBaseManufacturing()
                        .get(CommodityType.LUXURY_GOODS)
                        * 1.15
                        * 1.20
                        * 1.25;

        assertEquals(
                expected,
                profile.getManufacturingPotential()
                        .get(CommodityType.LUXURY_GOODS),
                0.0000001
        );
    }
}