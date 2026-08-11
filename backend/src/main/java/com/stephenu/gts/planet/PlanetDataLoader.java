package com.stephenu.gts.planet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.stephenu.gts.starsystem.Region;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;

import lombok.RequiredArgsConstructor;

@Component
@Order(3)
@RequiredArgsConstructor
public class PlanetDataLoader implements CommandLineRunner {

    private final PlanetRepository planetRepository;
    private final StarSystemRepository starSystemRepository;
    PlanetResourceGenerator planetResourceGenerator = new PlanetResourceGenerator();

    private final Random random = new Random();

    @Override
    public void run(String... args) {

        if (planetRepository.count() > 0) {
            return;
        }

        List<Planet> planets = new ArrayList<>();

        for (StarSystem system : starSystemRepository.findAll()) {
            planets.addAll(generatePlanets(system));
        }

        planetRepository.saveAll(planets);
    }

    private List<Planet> generatePlanets(StarSystem system) {

        final List<Integer> PLANET_COUNTS = List.of(
            1,
            2, 2,
            3, 3, 3, 3,
            4, 4, 4, 4, 4,
            5, 5,
            6, 6,
            7
        );

        int planetCount = chooseWeighted(PLANET_COUNTS);
        
        List<Planet> planets = new ArrayList<>();

        for (int i = 1; i <= planetCount; i++) {
            planets.add(generatePlanet(system, i, planetCount));
        }

        return planets;
    }

    private Planet generatePlanet(
            StarSystem system,
            int orbitalOrder,
            int totalPlanets) 
    {
        Planet planet = new Planet();

        planet.setStarSystem(system);
        planet.setOrbitalOrder(orbitalOrder);
        planet.setOrbitZone(
                generateOrbitZone(
                    orbitalOrder,
                    totalPlanets
                )  
        );

        planet.setName(generatePlanetName(system, orbitalOrder));

        planet.setPlanetType(
                generatePlanetType(
                        system.getRegion(),
                        planet.getOrbitZone()
                )
        );

        planet.setFeatures(
                generatePlanetFeatures(
                        planet.getPlanetType(),
                        planet.getOrbitZone()
                )
        );

        planet.setDevelopment(
                generateDevelopment(planet.getPlanetType(),system.getRegion(),planet.getFeatures())
        );

        planet.setPopulation(
                generatePopulation(planet.getPlanetType(), planet.getDevelopment(),planet.getFeatures())
        );

        planetResourceGenerator.generateResources(planet);

        planet.setInfrastructure(
                generateInfrastructure(planet)
        );

        return planet;
    }

    private String generatePlanetName(
        StarSystem system,
        int orbitalOrder) 
    {
    return system.getName() + " " + orbitalOrder;
    }

    private OrbitZone generateOrbitZone(
            int orbitalOrder,
            int totalPlanets)
    {
        double ratio = (double) orbitalOrder / totalPlanets;

        if (ratio <= 0.33) {
        return OrbitZone.INNER;
        }

        if (ratio <= 0.66) {
            return OrbitZone.MIDDLE;
        }

        return OrbitZone.OUTER;
    }

    private PlanetType generatePlanetType(
            Region region,
            OrbitZone orbitZone) 
    {
        List<PlanetType> pool = new ArrayList<>(
            generatePlanetTypePool(region, orbitZone));

        return chooseWeighted(pool);
    }

    private Set<PlanetFeature> generatePlanetFeatures(
            PlanetType type,
            OrbitZone orbitZone)
    {
        final List<Integer> FEATURE_COUNTS = List.of(
            0,
            1,1,1,
            2,2,2,
            3
        );

        int featureCount = chooseWeighted(FEATURE_COUNTS);
        Set<PlanetFeature> features = new HashSet<>();

        List<PlanetFeature> pool =
            generateFeaturePool(
                    features,
                    type,
                    orbitZone
            );


        while (features.size() < featureCount) {

            features.add(
                    chooseWeighted(pool)
            );
            pool = generateFeaturePool(
                    features,
                    type,
                    orbitZone
            );
        }

        return features;
    }


    private PopulationLevel generatePopulation(
            PlanetType type,
            DevelopmentLevel development,
            Set<PlanetFeature> features) {

        List<PopulationLevel> pool =
                generatePopulationPool(type, development, features);

        return chooseWeighted(pool);
    }

    private DevelopmentLevel generateDevelopment(
            PlanetType planetType,
            Region region,
            Set<PlanetFeature> features) {

        List<DevelopmentLevel> pool =
                generateDevelopmentPool(
                        planetType,
                        region,
                        features
                );

        return chooseWeighted(pool);
    }

    private InfrastructureLevel generateInfrastructure(
            Planet planet) {

        List<InfrastructureLevel> pool =
                generateInfrastructurePool(planet);

        return chooseWeighted(pool);
    }

    private <T> T chooseWeighted(List<T> choices) {
    return choices.get(random.nextInt(choices.size()));
    }

    private List<PlanetType> generatePlanetTypePool(
            Region region,
            OrbitZone orbitZone)
    {
        switch (orbitZone) {

            case INNER -> {
                switch (region) {

                    case CORE -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.BARREN,
                                PlanetType.ARID,
                                PlanetType.ARID,
                                PlanetType.VOLCANIC,
                                PlanetType.VOLCANIC,
                                PlanetType.CONTINENTAL
                        );
                    }

                    case INNER_RIM -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.BARREN,
                                PlanetType.ARID,
                                PlanetType.ARID,
                                PlanetType.VOLCANIC,
                                PlanetType.CONTINENTAL,
                                PlanetType.OCEANIC
                        );
                    }

                    case OUTER_RIM -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.BARREN,
                                PlanetType.BARREN,
                                PlanetType.ARID,
                                PlanetType.ARID,
                                PlanetType.VOLCANIC,
                                PlanetType.CONTINENTAL
                        );
                    }
                }
            }

            case MIDDLE -> {
                switch (region) {

                    case CORE -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.VOLCANIC,
                                PlanetType.ARID,
                                PlanetType.CONTINENTAL,
                                PlanetType.CONTINENTAL,
                                PlanetType.CONTINENTAL,
                                PlanetType.OCEANIC,
                                PlanetType.OCEANIC,
                                PlanetType.ALPINE
                        );
                    }

                    case INNER_RIM -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.CONTINENTAL,
                                PlanetType.CONTINENTAL,
                                PlanetType.CONTINENTAL,
                                PlanetType.CONTINENTAL,
                                PlanetType.OCEANIC,
                                PlanetType.OCEANIC,
                                PlanetType.OCEANIC,
                                PlanetType.ARID,
                                PlanetType.ARID,
                                PlanetType.ALPINE,
                                PlanetType.ALPINE
                        );
                    }

                    case OUTER_RIM -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.CONTINENTAL,
                                PlanetType.CONTINENTAL,
                                PlanetType.OCEANIC,
                                PlanetType.ALPINE,
                                PlanetType.ALPINE,
                                PlanetType.FROZEN
                        );
                    }
                }
            }

            case OUTER -> {
                switch (region) {

                    case CORE -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.FROZEN,
                                PlanetType.CRYOVOLCANIC,
                                PlanetType.ALPINE
                        );
                    }

                    case INNER_RIM -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.BARREN,
                                PlanetType.CONTINENTAL,
                                PlanetType.ALPINE,
                                PlanetType.ALPINE,
                                PlanetType.CRYOVOLCANIC,
                                PlanetType.FROZEN,
                                PlanetType.FROZEN
                        );
                    }

                    case OUTER_RIM -> {
                        return List.of(
                                PlanetType.BARREN,
                                PlanetType.BARREN,
                                PlanetType.BARREN,
                                PlanetType.FROZEN,
                                PlanetType.FROZEN,
                                PlanetType.FROZEN,
                                PlanetType.CRYOVOLCANIC,
                                PlanetType.CRYOVOLCANIC,
                                PlanetType.ALPINE,
                                PlanetType.ALPINE
                        );
                    }
                }
            }
        }
        throw new IllegalStateException(
        "Unhandled planet type generation."
        );
    }

    private List<PlanetFeature> generateFeaturePool(
        Set<PlanetFeature> features,
        PlanetType type,
        OrbitZone orbitZone) {

        List<PlanetFeature> pool = new ArrayList<>();

        /*
        * ===== Orbital Features =====
        */

        switch (orbitZone) {

            case INNER -> {
                pool.add(PlanetFeature.TECTONIC_ACTIVITY);
                pool.add(PlanetFeature.VOLCANIC_ACTIVITY);
                pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);
                pool.add(PlanetFeature.WEAK_ATMOSPHERE);
                pool.add(PlanetFeature.VOLCANIC_MOON);
            }

            case MIDDLE -> {
                pool.add(PlanetFeature.TECTONIC_ACTIVITY);
                pool.add(PlanetFeature.POWERFUL_WEATHER_SYSTEMS);
                pool.add(PlanetFeature.DENSE_ATMOSPHERE);
                pool.add(PlanetFeature.HABITABLE_MOON);
                pool.add(PlanetFeature.METALLIC_MOON);
                pool.add(PlanetFeature.ASTEROID_BELT);
                pool.add(PlanetFeature.STRONG_GRAVITY);
            }

            case OUTER -> {
                pool.add(PlanetFeature.GAS_GIANT_ORBIT);
                pool.add(PlanetFeature.GAS_GIANT_ORBIT);
                pool.add(PlanetFeature.ICY_MOON);
                pool.add(PlanetFeature.RING_SYSTEM);
                pool.add(PlanetFeature.ASTEROID_BELT);
                pool.add(PlanetFeature.METALLIC_MOON);
                pool.add(PlanetFeature.CRYOVOLCANIC_MOON);
                pool.add(PlanetFeature.STRONG_GRAVITY);
            }
        }

        /*
        * ===== Planet Type Features =====
        */

        switch (type) {

            case CONTINENTAL -> {
                pool.remove(PlanetFeature.SUBSURFACE_OCEAN);

                pool.add(PlanetFeature.TECTONIC_ACTIVITY);
                pool.add(PlanetFeature.DENSE_FORESTS);
                pool.add(PlanetFeature.DENSE_FORESTS);
                pool.add(PlanetFeature.SHALLOW_SEAS);
                pool.add(PlanetFeature.DENSE_ATMOSPHERE);
                pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
                pool.add(PlanetFeature.RICH_FOSSIL_DEPOSITS);
            }

            case OCEANIC -> {
                pool.remove(PlanetFeature.SALT_FLATS);
                pool.remove(PlanetFeature.DENSE_FORESTS);
                pool.remove(PlanetFeature.WEAK_ATMOSPHERE);
                pool.remove(PlanetFeature.SUBSURFACE_OCEAN);

                pool.add(PlanetFeature.SHALLOW_SEAS);
                pool.add(PlanetFeature.SHALLOW_SEAS);
                pool.add(PlanetFeature.SHALLOW_SEAS);
                pool.add(PlanetFeature.DENSE_ATMOSPHERE);
                pool.add(PlanetFeature.POWERFUL_WEATHER_SYSTEMS);
                pool.add(PlanetFeature.RICH_FOSSIL_DEPOSITS);
            }

            case ALPINE -> {
                pool.remove(PlanetFeature.SALT_FLATS);
                pool.remove(PlanetFeature.SUBSURFACE_OCEAN);

                pool.add(PlanetFeature.TECTONIC_ACTIVITY);
                pool.add(PlanetFeature.TECTONIC_ACTIVITY);
                pool.add(PlanetFeature.WEAK_ATMOSPHERE);
                pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);
                pool.add(PlanetFeature.RICH_FOSSIL_DEPOSITS);
            }

            case ARID -> {
                pool.remove(PlanetFeature.DENSE_FORESTS);
                pool.remove(PlanetFeature.SUBSURFACE_OCEAN);

                pool.add(PlanetFeature.SALT_FLATS);
                pool.add(PlanetFeature.SALT_FLATS);
                pool.add(PlanetFeature.WEAK_ATMOSPHERE);
                pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
                pool.add(PlanetFeature.RICH_FOSSIL_DEPOSITS);
            }

            case BARREN -> {
                pool.remove(PlanetFeature.WEAK_ATMOSPHERE);
                pool.remove(PlanetFeature.DENSE_FORESTS);
                pool.remove(PlanetFeature.SHALLOW_SEAS);
                pool.remove(PlanetFeature.SUBSURFACE_OCEAN);
                pool.remove(PlanetFeature.DENSE_ATMOSPHERE);
                pool.remove(PlanetFeature.SALT_FLATS);
                pool.remove(PlanetFeature.POWERFUL_WEATHER_SYSTEMS);
                pool.remove(PlanetFeature.RICH_FOSSIL_DEPOSITS);

                pool.add(PlanetFeature.METALLIC_MOON);
                pool.add(PlanetFeature.ASTEROID_BELT);
                pool.add(PlanetFeature.HABITABLE_MOON);
                pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
                pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
            }

            case VOLCANIC -> {
                pool.remove(PlanetFeature.VOLCANIC_ACTIVITY);
                pool.remove(PlanetFeature.DENSE_FORESTS);
                pool.remove(PlanetFeature.SHALLOW_SEAS);
                pool.remove(PlanetFeature.SUBSURFACE_OCEAN);
                pool.remove(PlanetFeature.SALT_FLATS);
                pool.remove(PlanetFeature.RICH_FOSSIL_DEPOSITS);

                pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);
                pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);
                pool.add(PlanetFeature.STRONG_GRAVITY);
            }

            case FROZEN -> {
                pool.remove(PlanetFeature.SHALLOW_SEAS);
                pool.remove(PlanetFeature.SALT_FLATS);
                pool.remove(PlanetFeature.DENSE_FORESTS);

                pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
                pool.add(PlanetFeature.ICY_MOON);
                pool.add(PlanetFeature.SUBSURFACE_OCEAN);
                pool.add(PlanetFeature.WEAK_ATMOSPHERE);
            }

            case CRYOVOLCANIC -> {
                pool.remove(PlanetFeature.SUBSURFACE_OCEAN); 
                pool.remove(PlanetFeature.DENSE_FORESTS);
                pool.remove(PlanetFeature.SHALLOW_SEAS);
                pool.remove(PlanetFeature.SALT_FLATS);
                pool.remove(PlanetFeature.RICH_FOSSIL_DEPOSITS);

                pool.add(PlanetFeature.SUBSURFACE_OCEAN);
                pool.add(PlanetFeature.SUBSURFACE_OCEAN);
                pool.add(PlanetFeature.ICY_MOON);
                pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);
            }
        }

        /*
        * ===== Remove Existing Features =====
        */

        pool.removeAll(features);

        /*
        * ===== Mutual Exclusivity =====
        */

        if (features.contains(PlanetFeature.DENSE_ATMOSPHERE)) {

            pool.remove(PlanetFeature.WEAK_ATMOSPHERE);

            pool.add(PlanetFeature.POWERFUL_WEATHER_SYSTEMS);
            pool.add(PlanetFeature.POWERFUL_WEATHER_SYSTEMS);
        }

        if (features.contains(PlanetFeature.WEAK_ATMOSPHERE)) {

            pool.remove(PlanetFeature.DENSE_ATMOSPHERE);
            pool.remove(PlanetFeature.DENSE_FORESTS);
            pool.remove(PlanetFeature.POWERFUL_WEATHER_SYSTEMS);
        }

        if (features.contains(PlanetFeature.DENSE_FORESTS)) {

            pool.remove(PlanetFeature.SALT_FLATS);

            pool.add(PlanetFeature.RICH_FOSSIL_DEPOSITS);
        }

        if (features.contains(PlanetFeature.SALT_FLATS)) {

            pool.remove(PlanetFeature.DENSE_FORESTS);
            pool.remove(PlanetFeature.SHALLOW_SEAS);

            pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
        }

        if (features.contains(PlanetFeature.TECTONIC_ACTIVITY)) {

            pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);
            pool.add(PlanetFeature.VOLCANIC_ACTIVITY);
        }

        if (features.contains(PlanetFeature.VOLCANIC_ACTIVITY)) {

            pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);
            pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);

            pool.remove(PlanetFeature.ANCIENT_IMPACT_BASIN);
        }

        if (features.contains(PlanetFeature.GEOTHERMAL_ACTIVITY)) {

            pool.add(PlanetFeature.VOLCANIC_ACTIVITY);
        }

        if (features.contains(PlanetFeature.SHALLOW_SEAS)) {

            pool.add(PlanetFeature.DENSE_FORESTS);
            pool.add(PlanetFeature.RICH_FOSSIL_DEPOSITS);

            pool.remove(PlanetFeature.SALT_FLATS);
        }

        if (features.contains(PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {

            pool.add(PlanetFeature.DENSE_ATMOSPHERE);

            pool.remove(PlanetFeature.WEAK_ATMOSPHERE);
        }

        if (features.contains(PlanetFeature.SUBSURFACE_OCEAN)) {

            pool.add(PlanetFeature.GEOTHERMAL_ACTIVITY);

            pool.remove(PlanetFeature.SALT_FLATS);
        }

        if (features.contains(PlanetFeature.RICH_FOSSIL_DEPOSITS)) {

            pool.add(PlanetFeature.DENSE_FORESTS);

            pool.remove(PlanetFeature.VOLCANIC_ACTIVITY);
        }

        if (features.contains(PlanetFeature.ANCIENT_IMPACT_BASIN)) {

            pool.add(PlanetFeature.METALLIC_MOON);
        }

        if (features.contains(PlanetFeature.GAS_GIANT_ORBIT)) {

            pool.remove(PlanetFeature.STRONG_GRAVITY);

            pool.add(PlanetFeature.ICY_MOON);
            pool.add(PlanetFeature.VOLCANIC_MOON);
            pool.add(PlanetFeature.CRYOVOLCANIC_MOON);
            pool.add(PlanetFeature.HABITABLE_MOON);
            pool.add(PlanetFeature.RING_SYSTEM);
            pool.add(PlanetFeature.ASTEROID_BELT);
        }

        boolean hasMoon =
           features.contains(PlanetFeature.HABITABLE_MOON)
        || features.contains(PlanetFeature.METALLIC_MOON)
        || features.contains(PlanetFeature.ICY_MOON)
        || features.contains(PlanetFeature.VOLCANIC_MOON)
        || features.contains(PlanetFeature.CRYOVOLCANIC_MOON);

        if (hasMoon &&
            !features.contains(PlanetFeature.GAS_GIANT_ORBIT)) {

            pool.remove(PlanetFeature.HABITABLE_MOON);
            pool.remove(PlanetFeature.METALLIC_MOON);
            pool.remove(PlanetFeature.ICY_MOON);
            pool.remove(PlanetFeature.VOLCANIC_MOON);
            pool.remove(PlanetFeature.CRYOVOLCANIC_MOON);
        }

        if (hasMoon && !features.contains(PlanetFeature.WEAK_ATMOSPHERE)) {

            pool.add(PlanetFeature.POWERFUL_WEATHER_SYSTEMS);
        }

        if (features.contains(PlanetFeature.ASTEROID_BELT)) {

            pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
        }

        if (features.contains(PlanetFeature.RING_SYSTEM)) {

            pool.add(PlanetFeature.ANCIENT_IMPACT_BASIN);
        }


        return pool;
    }

    private List<DevelopmentLevel> generateDevelopmentPool(
            PlanetType type,
            Region region,
            Set<PlanetFeature> features) {

        List<DevelopmentLevel> pool = new ArrayList<>();

        /*
        * ===== Planet Type =====
        */

        switch (type) {

            case CONTINENTAL -> pool.addAll(List.of(
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED
            ));

            case OCEANIC -> pool.addAll(List.of(
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED,
                    DevelopmentLevel.ADVANCED
            ));

            case ALPINE -> pool.addAll(List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED
            ));

            case ARID -> pool.addAll(List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED
            ));

            case BARREN -> pool.addAll(List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.INDUSTRIAL
            ));

            case VOLCANIC -> pool.addAll(List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.INDUSTRIAL
            ));

            case FROZEN -> pool.addAll(List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN
            ));

            case CRYOVOLCANIC -> pool.addAll(List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.INDUSTRIAL
            ));
        }

        /*
        * ===== Region =====
        */

        switch (region) {

            case CORE -> {
                pool.add(DevelopmentLevel.DEVELOPING);
                pool.add(DevelopmentLevel.INDUSTRIAL);
            }

            case INNER_RIM -> {
                pool.add(DevelopmentLevel.INDUSTRIAL);
                pool.add(DevelopmentLevel.ADVANCED);
                pool.add(DevelopmentLevel.ADVANCED);
            }

            case OUTER_RIM -> {
                pool.add(DevelopmentLevel.COLONIAL);
                pool.add(DevelopmentLevel.DEVELOPING);
            }
        }

        /*
        * ===== Planet Features =====
        */

        if (features.contains(PlanetFeature.HABITABLE_MOON)) {
            pool.add(DevelopmentLevel.DEVELOPING);
            pool.add(DevelopmentLevel.AGRARIAN);
            pool.add(DevelopmentLevel.INDUSTRIAL);
            pool.add(DevelopmentLevel.ADVANCED);
        }

        if (features.contains(PlanetFeature.ASTEROID_BELT)) {
            pool.add(DevelopmentLevel.INDUSTRIAL);
        }

        if (features.contains(PlanetFeature.ICY_MOON)) {
            pool.add(DevelopmentLevel.DEVELOPING);
        }

        if (features.contains(PlanetFeature.METALLIC_MOON)) {
            pool.add(DevelopmentLevel.INDUSTRIAL);
        }

        if (features.contains(PlanetFeature.VOLCANIC_MOON)) {
            pool.add(DevelopmentLevel.INDUSTRIAL);
        }

        if (features.contains(PlanetFeature.CRYOVOLCANIC_MOON)) {
            pool.add(DevelopmentLevel.INDUSTRIAL);
        }

        if (features.contains(PlanetFeature.RING_SYSTEM)) {
            pool.add(DevelopmentLevel.ADVANCED);
        }

        if (features.contains(PlanetFeature.ANCIENT_IMPACT_BASIN)) {
            pool.add(DevelopmentLevel.INDUSTRIAL);
        }

        if (features.contains(PlanetFeature.GEOTHERMAL_ACTIVITY)) {
            pool.add(DevelopmentLevel.INDUSTRIAL);
        }

        if (features.contains(PlanetFeature.RICH_FOSSIL_DEPOSITS)) {
            pool.add(DevelopmentLevel.INDUSTRIAL);
        }

        if (features.contains(PlanetFeature.DENSE_FORESTS)) {
            pool.add(DevelopmentLevel.AGRARIAN);
        }

        if (features.contains(PlanetFeature.SHALLOW_SEAS)) {
            pool.add(DevelopmentLevel.AGRARIAN);
        }

        if (features.contains(PlanetFeature.SALT_FLATS)) {
            pool.add(DevelopmentLevel.AGRARIAN);
        }

        if (features.contains(PlanetFeature.STRONG_GRAVITY)) {
            pool.add(DevelopmentLevel.COLONIAL);
        }

        if (features.contains(PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {
            pool.add(DevelopmentLevel.COLONIAL);
        }

        if (features.contains(PlanetFeature.GAS_GIANT_ORBIT)) {
            pool.add(DevelopmentLevel.COLONIAL);
        }

        if (features.contains(PlanetFeature.WEAK_ATMOSPHERE)) {
            pool.add(DevelopmentLevel.COLONIAL);
        }

        if (features.contains(PlanetFeature.VOLCANIC_ACTIVITY)) {
            pool.add(DevelopmentLevel.COLONIAL);
        }

        return pool;
    }

    private List<PopulationLevel> generatePopulationPool(
            PlanetType type,
            DevelopmentLevel development,
            Set<PlanetFeature> features) {

        List<PopulationLevel> pool = new ArrayList<>();

        /*
        * ===== Base Development =====
        */

        switch (development) {

            case COLONIAL -> pool.addAll(List.of(
                    PopulationLevel.TENS_OF_MILLIONS,
                    PopulationLevel.TENS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS
            ));

            case DEVELOPING -> pool.addAll(List.of(
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS
            ));

            case AGRARIAN -> pool.addAll(List.of(
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS
            ));

            case INDUSTRIAL -> pool.addAll(List.of(
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS
            ));

            case ADVANCED -> pool.addAll(List.of(
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS
            ));
        }

        /*
        * ===== Planet Type Caps =====
        */

        if (type == PlanetType.BARREN ||
            type == PlanetType.VOLCANIC ||
            type == PlanetType.CRYOVOLCANIC) {

            pool.remove(PopulationLevel.BILLIONS);
            pool.remove(PopulationLevel.TENS_OF_BILLIONS);
        }

        /*
        * ===== Feature Modifiers =====
        */

        boolean hasResourceMoon =
                features.contains(PlanetFeature.METALLIC_MOON)
            || features.contains(PlanetFeature.ICY_MOON)
            || features.contains(PlanetFeature.VOLCANIC_MOON)
            || features.contains(PlanetFeature.CRYOVOLCANIC_MOON);

        if (hasResourceMoon) {
            pool.add(PopulationLevel.HUNDREDS_OF_MILLIONS);
            pool.add(PopulationLevel.BILLIONS);
        }

        if (features.contains(PlanetFeature.HABITABLE_MOON)) {
            pool.add(PopulationLevel.BILLIONS);
            pool.add(PopulationLevel.TENS_OF_BILLIONS);
        }

        if (features.contains(PlanetFeature.DENSE_FORESTS) ||
            features.contains(PlanetFeature.SHALLOW_SEAS)) {

            pool.add(PopulationLevel.BILLIONS);
        }

        if (features.contains(PlanetFeature.DENSE_ATMOSPHERE)) {
            pool.add(PopulationLevel.BILLIONS);
        }

        if (features.contains(PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {
            pool.remove(PopulationLevel.TENS_OF_BILLIONS);
        }

        if (features.contains(PlanetFeature.WEAK_ATMOSPHERE)) {
            pool.remove(PopulationLevel.TENS_OF_BILLIONS);
        }

        if (features.contains(PlanetFeature.STRONG_GRAVITY)) {
            pool.remove(PopulationLevel.TENS_OF_BILLIONS);
        }

        return pool;
    }

    private List<InfrastructureLevel> generateInfrastructurePool(
            Planet planet) {

        List<InfrastructureLevel> pool = new ArrayList<>(List.of(

                InfrastructureLevel.POOR,

                InfrastructureLevel.MODEST,
                InfrastructureLevel.MODEST,
                InfrastructureLevel.MODEST,

                InfrastructureLevel.GOOD,

                InfrastructureLevel.EXCELLENT
        ));

        Set<PlanetFeature> features = planet.getFeatures();

        if (features.contains(PlanetFeature.HABITABLE_MOON)) {
            pool.add(InfrastructureLevel.GOOD);
            pool.add(InfrastructureLevel.EXCELLENT);
        }

        if (features.contains(PlanetFeature.ASTEROID_BELT)) {
            pool.add(InfrastructureLevel.GOOD);
        }

        if (features.contains(PlanetFeature.GAS_GIANT_ORBIT)) {
            pool.add(InfrastructureLevel.GOOD);
        }

        if (features.contains(PlanetFeature.RING_SYSTEM)) {
            pool.add(InfrastructureLevel.GOOD);
        }

        if (features.contains(PlanetFeature.ANCIENT_IMPACT_BASIN)) {
            pool.add(InfrastructureLevel.GOOD);
        }

        if (features.contains(PlanetFeature.GEOTHERMAL_ACTIVITY)) {
            pool.add(InfrastructureLevel.GOOD);
        }

        if (features.contains(PlanetFeature.STRONG_GRAVITY)) {
            pool.add(InfrastructureLevel.POOR);
            pool.add(InfrastructureLevel.POOR);
        }

        if (features.contains(PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {
            pool.add(InfrastructureLevel.POOR);
        }

        if (features.contains(PlanetFeature.WEAK_ATMOSPHERE)) {
            pool.add(InfrastructureLevel.POOR);
        }

        if (features.contains(PlanetFeature.VOLCANIC_ACTIVITY)) {
            pool.add(InfrastructureLevel.POOR);
        }

        if (planet.getDevelopment() == DevelopmentLevel.AGRARIAN) {
            pool.add(InfrastructureLevel.POOR);
            pool.add(InfrastructureLevel.MODEST);
        }

        return pool;
    }
    
}
