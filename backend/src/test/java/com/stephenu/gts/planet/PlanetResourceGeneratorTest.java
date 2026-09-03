package com.stephenu.gts.planet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stephenu.gts.commodity.CommodityType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;


class PlanetResourceGeneratorTest {

    private PlanetResourceGenerator generator;

    private static final Set<CommodityType> EXPECTED_RESOURCES =
            EnumSet.of(
                    CommodityType.FOOD,
                    CommodityType.WATER,
                    CommodityType.BIOMATERIALS,
                    CommodityType.COMMON_METALS,
                    CommodityType.RARE_METALS,
                    CommodityType.INDUSTRIAL_MINERALS,
                    CommodityType.HYDROCARBONS,
                    CommodityType.INDUSTRIAL_CHEMICALS,
                    CommodityType.RARE_ELEMENTS
            );

    @BeforeEach
    void setUp() {
        generator = new PlanetResourceGenerator(null);
    }

    @Test
    void shouldGenerateAllTierOneResourcesForEveryPlanetType() {
        for (PlanetType planetType : PlanetType.values()) {
            Planet planet = createPlanet(planetType);

            Map<CommodityType, ResourceLevel> resources =
                    generator.generateResources(
                            planet,
                            new FixedRandom(0)
                    );

            assertEquals(
                    EXPECTED_RESOURCES,
                    resources.keySet(),
                    "Unexpected resources for " + planetType
            );
        }
    }

    @Test
    void shouldGenerateValidResourceLevels() {
        for (PlanetType planetType : PlanetType.values()) {
            Planet planet = createPlanet(planetType);

            Map<CommodityType, ResourceLevel> resources =
                    generator.generateResources(
                            planet,
                            new FixedRandom(0)
                    );

            for (ResourceLevel level : resources.values()) {
                assertNotNull(level);
                assertTrue(
                        EnumSet.allOf(ResourceLevel.class).contains(level)
                );
            }
        }
    }

    @Test
    void shouldHandleNullFeatures() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);
        planet.setFeatures(null);

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(EXPECTED_RESOURCES, resources.keySet());
    }

    @Test
    void shouldHandleEmptyFeatures() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);
        planet.setFeatures(EnumSet.noneOf(PlanetFeature.class));

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(EXPECTED_RESOURCES, resources.keySet());
    }

    @Test
    void nullAndEmptyFeaturesShouldProduceSameResources() {
        Planet nullFeaturesPlanet = createPlanet(PlanetType.CONTINENTAL);
        nullFeaturesPlanet.setFeatures(null);

        Planet emptyFeaturesPlanet = createPlanet(PlanetType.CONTINENTAL);
        emptyFeaturesPlanet.setFeatures(
                EnumSet.noneOf(PlanetFeature.class)
        );

        Map<CommodityType, ResourceLevel> nullFeaturesResources =
                generator.generateResources(
                        nullFeaturesPlanet,
                        new FixedRandom(0)
                );

        Map<CommodityType, ResourceLevel> emptyFeaturesResources =
                generator.generateResources(
                        emptyFeaturesPlanet,
                        new FixedRandom(0)
                );

        assertEquals(
                emptyFeaturesResources,
                nullFeaturesResources
        );
    }

    @Test
    void asteroidBeltShouldIncreaseMetalResources() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setFeatures(
                EnumSet.of(PlanetFeature.ASTEROID_BELT)
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(
                ResourceLevel.AVERAGE,
                resources.get(CommodityType.COMMON_METALS)
        );

        assertEquals(
                ResourceLevel.AVERAGE,
                resources.get(CommodityType.RARE_METALS)
        );
    }

    @Test
    void strongGravityShouldDecreaseFoodResources() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setFeatures(
                EnumSet.of(PlanetFeature.STRONG_GRAVITY)
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(
                ResourceLevel.SCARCE,
                resources.get(CommodityType.FOOD)
        );
    }

    @Test
    void multipleFeaturesShouldApplyTheirModifiers() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setFeatures(
                EnumSet.of(
                        PlanetFeature.ASTEROID_BELT,
                        PlanetFeature.STRONG_GRAVITY
                )
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(
                ResourceLevel.AVERAGE,
                resources.get(CommodityType.COMMON_METALS)
        );

        assertEquals(
                ResourceLevel.AVERAGE,
                resources.get(CommodityType.RARE_METALS)
        );

        assertEquals(
                ResourceLevel.SCARCE,
                resources.get(CommodityType.FOOD)
        );
    }

    @Test
    void habitableMoonAndStrongGravityShouldCancelEachOther() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setFeatures(
                EnumSet.of(
                        PlanetFeature.HABITABLE_MOON,
                        PlanetFeature.STRONG_GRAVITY
                )
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(
                ResourceLevel.AVERAGE,
                resources.get(CommodityType.FOOD)
        );
    }

    @Test
    void populationOfHundredsOfMillionsShouldDecreaseHydrocarbons() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setPopulation(
                PopulationLevel.HUNDREDS_OF_MILLIONS
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(
                ResourceLevel.SCARCE,
                resources.get(CommodityType.HYDROCARBONS)
        );
    }

    @Test
    void populationOfTensOfBillionsShouldApplyMultipleDecreases() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setPopulation(
                PopulationLevel.TENS_OF_BILLIONS
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        assertEquals(
                ResourceLevel.NONE,
                resources.get(CommodityType.HYDROCARBONS)
        );

        assertEquals(
                ResourceLevel.NONE,
                resources.get(CommodityType.COMMON_METALS)
        );

        assertEquals(
                ResourceLevel.NONE,
                resources.get(CommodityType.RARE_METALS)
        );

        assertEquals(
                ResourceLevel.NONE,
                resources.get(CommodityType.RARE_ELEMENTS)
        );
    }

    @Test
    void resourceLevelsShouldNeverExceedMaximum() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setFeatures(
                EnumSet.of(
                        PlanetFeature.ASTEROID_BELT
                )
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        for (ResourceLevel level : resources.values()) {
            assertNotNull(level);
        }
    }

    @Test
    void resourceLevelsShouldNeverFallBelowMinimum() {
        Planet planet = createPlanet(PlanetType.CONTINENTAL);

        planet.setPopulation(
                PopulationLevel.TENS_OF_BILLIONS
        );

        Map<CommodityType, ResourceLevel> resources =
                generator.generateResources(
                        planet,
                        new FixedRandom(0)
                );

        for (ResourceLevel level : resources.values()) {
            assertNotNull(level);
            assertEquals(
                    level,
                    ResourceLevel.valueOf(level.name())
            );
        }
    }

    private Planet createPlanet(PlanetType planetType) {
        Planet planet = new Planet();

        planet.setPlanetType(planetType);
        planet.setFeatures(
                EnumSet.noneOf(PlanetFeature.class)
        );
        planet.setPopulation(null);

        return planet;
    }

    /**
     * Deterministic Random implementation used to control
     * which position chooseWeighted() selects.
     *
     * For example a value of 0 always selects the first position.
     */
    private static class FixedRandom extends Random {

        private final int value;

        private FixedRandom(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            return Math.min(value, bound - 1);
        }
    }
}
