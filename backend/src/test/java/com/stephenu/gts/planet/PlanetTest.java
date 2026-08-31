package com.stephenu.gts.planet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.stephenu.gts.starsystem.Region;
import com.stephenu.gts.starsystem.StarSystem;

class PlanetTest {

    @Test
    void coreInnerPlanetTypePoolIsCorrect() {

        Planet planet = new Planet();

        List<PlanetType> pool =
                planet.generatePlanetTypePool(
                        Region.CORE,
                        OrbitZone.INNER
                );

        assertEquals(
                List.of(
                        PlanetType.BARREN,
                        PlanetType.BARREN,
                        PlanetType.ARID,
                        PlanetType.ARID,
                        PlanetType.VOLCANIC,
                        PlanetType.VOLCANIC,
                        PlanetType.CONTINENTAL
                ),
                pool
        );
    }

    @Test
    void allPlanetTypePoolsAreNonEmpty() {

        Planet planet = new Planet();

        for (Region region : Region.values()) {

            for (OrbitZone orbitZone : OrbitZone.values()) {

                List<PlanetType> pool =
                        planet.generatePlanetTypePool(
                                region,
                                orbitZone
                        );

                assertNotNull(pool);
                assertFalse(pool.isEmpty());
            }
        }
    }


    @Test
    void innerRimMiddlePlanetTypePoolContainsExpectedWeights() {

        Planet planet = new Planet();

        List<PlanetType> pool =
                planet.generatePlanetTypePool(
                        Region.INNER_RIM,
                        OrbitZone.MIDDLE
                );

        assertEquals(12, pool.size());

        assertEquals(
                1,
                pool.stream()
                        .filter(type -> type == PlanetType.BARREN)
                        .count()
        );

        assertEquals(
                4,
                pool.stream()
                        .filter(type -> type == PlanetType.CONTINENTAL)
                        .count()
        );

        assertEquals(
                3,
                pool.stream()
                        .filter(type -> type == PlanetType.OCEANIC)
                        .count()
        );

        assertEquals(
                2,
                pool.stream()
                        .filter(type -> type == PlanetType.ALPINE)
                        .count()
        );

        assertEquals(
                2,
                pool.stream()
                        .filter(type -> type == PlanetType.ARID)
                        .count()
        );
    }

    @Test
    void barrenPlanetRemovesIncompatibleFeatures() {

        Planet planet = new Planet();

        List<PlanetFeature> pool =
                planet.generateFeaturePool(
                        Set.of(),
                        PlanetType.BARREN,
                        OrbitZone.INNER
                );

        assertFalse(
                pool.contains(PlanetFeature.DENSE_FORESTS)
        );

        assertFalse(
                pool.contains(PlanetFeature.SHALLOW_SEAS)
        );

        assertFalse(
                pool.contains(PlanetFeature.SUBSURFACE_OCEAN)
        );

        assertFalse(
                pool.contains(PlanetFeature.DENSE_ATMOSPHERE)
        );

        assertFalse(
                pool.contains(PlanetFeature.SALT_FLATS)
        );

        assertFalse(
                pool.contains(
                        PlanetFeature.RICH_FOSSIL_DEPOSITS
                )
        );
    }

    @Test
    void denseAtmosphereRemovesWeakAtmosphere() {

        Planet planet = new Planet();

        List<PlanetFeature> pool =
                planet.generateFeaturePool(
                        Set.of(PlanetFeature.DENSE_ATMOSPHERE),
                        PlanetType.CONTINENTAL,
                        OrbitZone.MIDDLE
                );

        assertFalse(
                pool.contains(PlanetFeature.WEAK_ATMOSPHERE)
        );
    }

    @Test
    void existingFeaturesAreRemovedFromPool() {

        Planet planet = new Planet();

        Set<PlanetFeature> existing =
                Set.of(
                        PlanetFeature.ASTEROID_BELT,
                        PlanetFeature.RING_SYSTEM
                );

        List<PlanetFeature> pool =
                planet.generateFeaturePool(
                        existing,
                        PlanetType.BARREN,
                        OrbitZone.OUTER
                );

        assertFalse(
                pool.contains(PlanetFeature.ASTEROID_BELT)
        );

        assertFalse(
                pool.contains(PlanetFeature.RING_SYSTEM)
        );
    }

     @Test
    void colonialPlanetDevelopmentPoolContainsExpectedLevels() {

        Planet planet = new Planet();

        List<DevelopmentLevel> pool =
                planet.generateDevelopmentPool(
                        PlanetType.BARREN,
                        Region.OUTER_RIM,
                        Set.of()
                );

        assertTrue(
                pool.contains(DevelopmentLevel.COLONIAL)
        );

        assertTrue(
                pool.contains(DevelopmentLevel.DEVELOPING)
        );

        assertFalse(
                pool.contains(DevelopmentLevel.ADVANCED)
        );
    }

    @Test
    void asteroidBeltAddsIndustrialDevelopmentWeight() {

        Planet planet = new Planet();

        List<DevelopmentLevel> withoutFeature =
                planet.generateDevelopmentPool(
                        PlanetType.BARREN,
                        Region.CORE,
                        Set.of()
                );

        List<DevelopmentLevel> withFeature =
                planet.generateDevelopmentPool(
                        PlanetType.BARREN,
                        Region.CORE,
                        Set.of(PlanetFeature.ASTEROID_BELT)
                );

        long withoutIndustrial =
                withoutFeature.stream()
                        .filter(level ->
                                level ==
                                DevelopmentLevel.INDUSTRIAL)
                        .count();

        long withIndustrial =
                withFeature.stream()
                        .filter(level ->
                                level ==
                                DevelopmentLevel.INDUSTRIAL)
                        .count();

        assertEquals(
                withoutIndustrial + 1,
                withIndustrial
        );
    }

    @Test
    void colonialPopulationPoolContainsOnlyLowPopulationLevels() {

        Planet planet = new Planet();

        List<PopulationLevel> pool =
                planet.generatePopulationPool(
                        PlanetType.CONTINENTAL,
                        DevelopmentLevel.COLONIAL,
                        Set.of()
                );

        assertEquals(
                List.of(
                        PopulationLevel.TENS_OF_MILLIONS,
                        PopulationLevel.TENS_OF_MILLIONS,
                        PopulationLevel.HUNDREDS_OF_MILLIONS
                ),
                pool
        );
    }

    @Test
    void barrenPlanetRemovesBillionsAndTensOfBillions() {

        Planet planet = new Planet();

        List<PopulationLevel> pool =
                planet.generatePopulationPool(
                        PlanetType.BARREN,
                        DevelopmentLevel.INDUSTRIAL,
                        Set.of()
                );

        assertFalse(
                pool.contains(PopulationLevel.BILLIONS)
        );

        assertFalse(
                pool.contains(
                        PopulationLevel.TENS_OF_BILLIONS
                )
        );
    }

    @Test
    void habitableMoonAddsHighPopulationWeight() {

        Planet planet = new Planet();

        List<PopulationLevel> pool =
                planet.generatePopulationPool(
                        PlanetType.CONTINENTAL,
                        DevelopmentLevel.DEVELOPING,
                        Set.of(PlanetFeature.HABITABLE_MOON)
                );

        long tensOfBillions =
                pool.stream()
                        .filter(level ->
                                level ==
                                PopulationLevel.TENS_OF_BILLIONS)
                        .count();

        assertTrue(tensOfBillions >= 2);
    }

    @Test
    void weakAtmosphereRemovesTensOfBillions() {

        Planet planet = new Planet();

        List<PopulationLevel> pool =
                planet.generatePopulationPool(
                        PlanetType.CONTINENTAL,
                        DevelopmentLevel.ADVANCED,
                        Set.of(PlanetFeature.WEAK_ATMOSPHERE)
                );

        assertFalse(
                pool.contains(
                        PopulationLevel.TENS_OF_BILLIONS
                )
        );
    }

    @Test
    void baseInfrastructurePoolHasExpectedWeights() {

        Planet planet = new Planet();

        planet.setFeatures(Set.of());
        planet.setDevelopment(
                DevelopmentLevel.DEVELOPING
        );

        List<InfrastructureLevel> pool =
                planet.generateInfrastructurePool();

        assertEquals(7, pool.size());

        assertEquals(
                1,
                pool.stream()
                        .filter(level ->
                                level == InfrastructureLevel.POOR)
                        .count()
        );

        assertEquals(
                3,
                pool.stream()
                        .filter(level ->
                                level == InfrastructureLevel.MODEST)
                        .count()
        );

        assertEquals(
                1,
                pool.stream()
                        .filter(level ->
                                level == InfrastructureLevel.GOOD)
                        .count()
        );

        assertEquals(
                1,
                pool.stream()
                        .filter(level ->
                                level == InfrastructureLevel.EXCELLENT)
                        .count()
        );
    }

    @Test
    void strongGravityAddsPoorInfrastructureWeight() {

        Planet planet = new Planet();

        planet.setFeatures(
                Set.of(PlanetFeature.STRONG_GRAVITY)
        );

        planet.setDevelopment(
                DevelopmentLevel.DEVELOPING
        );

        List<InfrastructureLevel> pool =
                planet.generateInfrastructurePool();

        long poorCount =
                pool.stream()
                        .filter(level ->
                                level == InfrastructureLevel.POOR)
                        .count();

        assertEquals(3, poorCount);
    }

    @Test
    void generateCreatesCompletePlanet() {

        Planet planet = new Planet();

        StarSystem system = new StarSystem();

        system.setName("Test System");
        system.setRegion(Region.INNER_RIM);

        planet.generate(
                system,
                2,
                3
        );

        assertNotNull(planet.getStarSystem());
        assertEquals(system, planet.getStarSystem());

        assertNotNull(planet.getName());
        assertEquals("Test System 2", planet.getName());

        assertEquals(
                2,
                planet.getOrbitalOrder()
        );

        assertNotNull(planet.getOrbitZone());
        assertNotNull(planet.getPlanetType());
        assertNotNull(planet.getDevelopment());
        assertNotNull(planet.getPopulation());
        assertNotNull(planet.getInfrastructure());

        assertNotNull(planet.getFeatures());
        assertNotNull(planet.getResources());

        assertNotNull(
                planet.getConsumptionProfile()
        );

        assertNotNull(
                planet.getProductionProfile()
        );
    }

    @Test
    void generateCreatesValidNumberOfFeatures() {

        Planet planet = new Planet();

        StarSystem system = new StarSystem();

        system.setName("Test System");
        system.setRegion(Region.INNER_RIM);

        planet.generate(
                system,
                2,
                3
        );

        assertTrue(
                planet.getFeatures().size() <= 3
        );
    }

    @Test
    void generateFeaturesAreUnique() {

        Planet planet = new Planet();

        StarSystem system = new StarSystem();

        system.setName("Test System");
        system.setRegion(Region.INNER_RIM);

        planet.generate(
                system,
                2,
                3
        );

        assertEquals(
                planet.getFeatures().size(),
                new HashSet<>(
                        planet.getFeatures()
                ).size()
        );
    }

    @Test
    void generateSetsCorrectPlanetName() {

        Planet planet = new Planet();

        StarSystem system = new StarSystem();

        system.setName("Sol");

        system.setRegion(
                Region.CORE
        );

        planet.generate(
                system,
                4,
                6
        );

        assertEquals(
                "Sol 4",
                planet.getName()
        );
    }

    
}
