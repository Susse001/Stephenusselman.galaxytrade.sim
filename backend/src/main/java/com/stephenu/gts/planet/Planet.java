package com.stephenu.gts.planet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.stephenu.gts.starsystem.Region;
import com.stephenu.gts.starsystem.StarSystem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "planets")
public class Planet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent star system.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "star_system_id")
    private StarSystem starSystem;

    /**
     * Display name.
     */
    private String name;

    /**
     * The position of the planet in system in reference to the center
     */
    private Integer orbitalOrder;

    /**
     * 
     */
    @Enumerated(EnumType.STRING)
    private OrbitZone orbitZone;

    /**
     * Dominant planetary environment.
     */
    @Enumerated(EnumType.STRING)
    private PlanetType planetType;

    /**
     * Approximate population.
     */
    @Enumerated(EnumType.STRING)
    private PopulationLevel population;

    /**
     * Overall technological and industrial development.
     */
    @Enumerated(EnumType.STRING)
    private DevelopmentLevel development;

    /**
     * Transportation and industrial infrastructure.
     */
    @Enumerated(EnumType.STRING)
    private InfrastructureLevel infrastructure;

    /**
     * Natural resource deposits available on the planet.
     */
    @OneToMany(
        mappedBy = "planet",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PlanetResource> resources = new ArrayList<>();

    /**
     * Planet's calculated extraction and manufacturing potential.
     */
    @Transient
    private PlanetProductionProfile productionProfile;

    /**
     * Calculated consumption requirements of this planet.
     */
    @Transient
    private PlanetConsumptionProfile consumptionProfile;

    /**
     * Unique planetary features.
     */
    @ElementCollection(targetClass = PlanetFeature.class)
    @CollectionTable(
        name = "planet_features",
        joinColumns = @JoinColumn(name = "planet_id")
    )
    @Column(name = "feature")
    @Enumerated(EnumType.STRING)
    private Set<PlanetFeature> features = new HashSet<>();

    /**
     * 
     * @param feature The planetary feature you want to check for
     * @return A boolean indicating if the given planet contains the feature.
     */
    public boolean hasFeature(
        PlanetFeature feature) 
    {
        return this.getFeatures().contains(feature);
    }

     /**
     * Generates the planet's characteristics and production profiles.
     *
     * Generation is performed in dependency order so that later
     * characteristics can be influenced by earlier ones.
     *
     * @param system Parent star system.
     * @param orbitalOrder Position of the planet within the system.
     * @param totalPlanets Total number of planets in the system.
     * @param random Random number generator used for weighted selection.
     */
    public void generate(
            StarSystem system,
            int orbitalOrder,
            int totalPlanets,
            Random random) {

        this.starSystem = system;
        this.orbitalOrder = orbitalOrder;

        this.orbitZone =
                generateOrbitZone(
                        orbitalOrder,
                        totalPlanets
                );

        this.name =
                generatePlanetName(
                        system,
                        orbitalOrder
                );

        this.planetType =
                generatePlanetType(
                        system.getRegion(),
                        this.orbitZone,
                        random
                );

        this.features =
                generatePlanetFeatures(
                        this.planetType,
                        this.orbitZone,
                        random
                );

        this.development =
                generateDevelopment(
                        this.planetType,
                        system.getRegion(),
                        this.features,
                        random
                );

        this.population =
                generatePopulation(
                        this.planetType,
                        this.development,
                        this.features,
                        random
                );

        this.resources.clear();

        PlanetResourceGenerator resourceGenerator =
                new PlanetResourceGenerator();

        resourceGenerator.generateAndAttachResources(this);

        this.infrastructure =
                generateInfrastructure(random);

        PlanetConsumptionProfile consumptionGenerator =
                new PlanetConsumptionProfile();

        this.consumptionProfile =
                new PlanetConsumptionProfile(
                        consumptionGenerator.generateConsumption(this)
                );

        PlanetProductionProfile productionGenerator =
                new PlanetProductionProfile();

        this.productionProfile =
                productionGenerator.generateProfile(this);
    }

    /**
     * Generates the planet's name from its parent system
     * and orbital position.
     *
     * @param system Parent star system.
     * @param orbitalOrder Position of the planet within the system.
     * @return Generated planet name.
     */
    private String generatePlanetName(
            StarSystem system,
            int orbitalOrder) {

        return system.getName() + " " + orbitalOrder;
    }

    /**
     * Determines the orbital zone based on the planet's
     * relative position within the system.
     *
     * @param orbitalOrder Position of the planet.
     * @param totalPlanets Total number of planets.
     * @return Generated orbital zone.
     */
    private OrbitZone generateOrbitZone(
            int orbitalOrder,
            int totalPlanets) {

        double ratio =
                (double) orbitalOrder / totalPlanets;

        if (ratio <= 0.33) {
            return OrbitZone.INNER;
        }

        if (ratio <= 0.66) {
            return OrbitZone.MIDDLE;
        }

        return OrbitZone.OUTER;
    }

    /**
     * Selects a planet type from the weighted pool
     * appropriate for the system region and orbital zone.
     *
     * @param region Parent system region.
     * @param orbitZone Planet's orbital zone.
     * @param random Random number generator.
     * @return Generated planet type.
     */
    private PlanetType generatePlanetType(
            Region region,
            OrbitZone orbitZone,
            Random random) {

        List<PlanetType> pool =
                generatePlanetTypePool(
                        region,
                        orbitZone
                );

        return chooseWeighted(pool, random);
    }

    /**
     * Generates the planet's unique features.
     *
     * Features are selected one at a time so that the
     * available pool can be modified after each selection.
     *
     * @param type Planet type.
     * @param orbitZone Planet's orbital zone.
     * @param random Random number generator.
     * @return Generated planetary features.
     */
    private Set<PlanetFeature> generatePlanetFeatures(
            PlanetType type,
            OrbitZone orbitZone,
            Random random) {

        int featureCount =
                chooseWeighted(
                        FEATURE_COUNTS,
                        random
                );

        Set<PlanetFeature> features =
                new HashSet<>();

        while (features.size() < featureCount) {

            List<PlanetFeature> pool =
                    generateFeaturePool(
                            features,
                            type,
                            orbitZone
                    );

            if (pool.isEmpty()) {
                break;
            }

            features.add(
                    chooseWeighted(
                            pool,
                            random
                    )
            );
        }

        return features;
    }

    /**
     * Selects a development level from the weighted pool
     * appropriate for the planet's type, region, and features.
     *
     * @param type Planet type.
     * @param region Parent system region.
     * @param features Planetary features.
     * @param random Random number generator.
     * @return Generated development level.
     */
    private DevelopmentLevel generateDevelopment(
            PlanetType type,
            Region region,
            Set<PlanetFeature> features,
            Random random) {

        List<DevelopmentLevel> pool =
                generateDevelopmentPool(
                        type,
                        region,
                        features
                );

        return chooseWeighted(pool, random);
    }

    /**
     * Selects a population level based on development,
     * planet type, and planetary features.
     *
     * @param type Planet type.
     * @param development Development level.
     * @param features Planetary features.
     * @param random Random number generator.
     * @return Generated population level.
     */
    private PopulationLevel generatePopulation(
            PlanetType type,
            DevelopmentLevel development,
            Set<PlanetFeature> features,
            Random random) {

        List<PopulationLevel> pool =
                generatePopulationPool(
                        type,
                        development,
                        features
                );

        return chooseWeighted(pool, random);
    }


    /**
     * Selects an infrastructure level based on the
     * planet's characteristics.
     *
     * @param random Random number generator.
     * @return Generated infrastructure level.
     */
    private InfrastructureLevel generateInfrastructure(
            Random random) {

        List<InfrastructureLevel> pool =
                generateInfrastructurePool();

        return chooseWeighted(pool, random);
    }

    /**
     * Selects a random entry from a weighted list.
     *
     * Duplicate entries within the list represent
     * increased probability.
     *
     * @param choices Weighted selection pool.
     * @param random Random number generator.
     * @param <T> Type of value being selected.
     * @return Selected value.
     */
    private <T> T chooseWeighted(
            List<T> choices,
            Random random) {

        return choices.get(
                random.nextInt(choices.size())
        );
    }

    /**
     * Generates the weighted planet type pool based on
     * orbital zone and system region.
     *
     * @param region Parent system region.
     * @param orbitZone Planet's orbital zone.
     * @return Weighted planet type pool.
     */
    private List<PlanetType> generatePlanetTypePool(
            Region region,
            OrbitZone orbitZone) {

        return switch (orbitZone) {

            case INNER -> switch (region) {

                case CORE -> CORE_INNER_PLANET_TYPES;

                case INNER_RIM -> INNER_RIM_INNER_PLANET_TYPES;

                case OUTER_RIM -> OUTER_RIM_INNER_PLANET_TYPES;
            };

            case MIDDLE -> switch (region) {

                case CORE -> CORE_MIDDLE_PLANET_TYPES;

                case INNER_RIM -> INNER_RIM_MIDDLE_PLANET_TYPES;

                case OUTER_RIM -> OUTER_RIM_MIDDLE_PLANET_TYPES;
            };

            case OUTER -> switch (region) {

                case CORE -> CORE_OUTER_PLANET_TYPES;

                case INNER_RIM -> INNER_RIM_OUTER_PLANET_TYPES;

                case OUTER_RIM -> OUTER_RIM_OUTER_PLANET_TYPES;
            };
        };
    }

    /**
     * Builds the available planetary feature pool.
     *
     * The pool is modified according to orbital zone,
     * planet type, existing features, and feature
     * compatibility rules.
     *
     * @param features Features already selected.
     * @param type Planet type.
     * @param orbitZone Planet's orbital zone.
     * @return Weighted feature pool.
     */
    private List<PlanetFeature> generateFeaturePool(
            Set<PlanetFeature> features,
            PlanetType type,
            OrbitZone orbitZone) {

        List<PlanetFeature> pool =
                new ArrayList<>();

        /*
         * ===== Orbital Features =====
         */

        switch (orbitZone) {

            case INNER -> pool.addAll(
                    INNER_ORBIT_FEATURES
            );

            case MIDDLE -> pool.addAll(
                    MIDDLE_ORBIT_FEATURES
            );

            case OUTER -> pool.addAll(
                    OUTER_ORBIT_FEATURES
            );
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

        if (features.contains(
                PlanetFeature.DENSE_ATMOSPHERE)) {

            pool.remove(
                    PlanetFeature.WEAK_ATMOSPHERE
            );

            pool.add(
                    PlanetFeature.POWERFUL_WEATHER_SYSTEMS
            );

            pool.add(
                    PlanetFeature.POWERFUL_WEATHER_SYSTEMS
            );
        }

        if (features.contains(
                PlanetFeature.WEAK_ATMOSPHERE)) {

            pool.remove(
                    PlanetFeature.DENSE_ATMOSPHERE
            );

            pool.remove(
                    PlanetFeature.DENSE_FORESTS
            );

            pool.remove(
                    PlanetFeature.POWERFUL_WEATHER_SYSTEMS
            );
        }

        if (features.contains(
                PlanetFeature.DENSE_FORESTS)) {

            pool.remove(
                    PlanetFeature.SALT_FLATS
            );

            pool.add(
                    PlanetFeature.RICH_FOSSIL_DEPOSITS
            );
        }

        if (features.contains(
                PlanetFeature.SALT_FLATS)) {

            pool.remove(
                    PlanetFeature.DENSE_FORESTS
            );

            pool.remove(
                    PlanetFeature.SHALLOW_SEAS
            );

            pool.add(
                    PlanetFeature.ANCIENT_IMPACT_BASIN
            );
        }

        if (features.contains(
                PlanetFeature.TECTONIC_ACTIVITY)) {

            pool.add(
                    PlanetFeature.GEOTHERMAL_ACTIVITY
            );

            pool.add(
                    PlanetFeature.VOLCANIC_ACTIVITY
            );
        }

        if (features.contains(
                PlanetFeature.VOLCANIC_ACTIVITY)) {

            pool.add(
                    PlanetFeature.GEOTHERMAL_ACTIVITY
            );

            pool.add(
                    PlanetFeature.GEOTHERMAL_ACTIVITY
            );

            pool.remove(
                    PlanetFeature.ANCIENT_IMPACT_BASIN
            );
        }

        if (features.contains(
                PlanetFeature.GEOTHERMAL_ACTIVITY)) {

            pool.add(
                    PlanetFeature.VOLCANIC_ACTIVITY
            );
        }

        if (features.contains(
                PlanetFeature.SHALLOW_SEAS)) {

            pool.add(
                    PlanetFeature.DENSE_FORESTS
            );

            pool.add(
                    PlanetFeature.RICH_FOSSIL_DEPOSITS
            );

            pool.remove(
                    PlanetFeature.SALT_FLATS
            );
        }

        if (features.contains(
                PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {

            pool.add(
                    PlanetFeature.DENSE_ATMOSPHERE
            );

            pool.remove(
                    PlanetFeature.WEAK_ATMOSPHERE
            );
        }

        if (features.contains(
                PlanetFeature.SUBSURFACE_OCEAN)) {

            pool.add(
                    PlanetFeature.GEOTHERMAL_ACTIVITY
            );

            pool.remove(
                    PlanetFeature.SALT_FLATS
            );
        }

        if (features.contains(
                PlanetFeature.RICH_FOSSIL_DEPOSITS)) {

            pool.add(
                    PlanetFeature.DENSE_FORESTS
            );

            pool.remove(
                    PlanetFeature.VOLCANIC_ACTIVITY
            );
        }

        if (features.contains(
                PlanetFeature.ANCIENT_IMPACT_BASIN)) {

            pool.add(
                    PlanetFeature.METALLIC_MOON
            );
        }

        if (features.contains(
                PlanetFeature.GAS_GIANT_ORBIT)) {

            pool.remove(
                    PlanetFeature.STRONG_GRAVITY
            );

            pool.add(
                    PlanetFeature.ICY_MOON
            );

            pool.add(
                    PlanetFeature.VOLCANIC_MOON
            );

            pool.add(
                    PlanetFeature.CRYOVOLCANIC_MOON
            );

            pool.add(
                    PlanetFeature.HABITABLE_MOON
            );

            pool.add(
                    PlanetFeature.RING_SYSTEM
            );

            pool.add(
                    PlanetFeature.ASTEROID_BELT
            );
        }

        boolean hasMoon =
                features.contains(
                        PlanetFeature.HABITABLE_MOON
                )
                || features.contains(
                        PlanetFeature.METALLIC_MOON
                )
                || features.contains(
                        PlanetFeature.ICY_MOON
                )
                || features.contains(
                        PlanetFeature.VOLCANIC_MOON
                )
                || features.contains(
                        PlanetFeature.CRYOVOLCANIC_MOON
                );

        if (hasMoon &&
                !features.contains(
                        PlanetFeature.GAS_GIANT_ORBIT)) {

            pool.remove(
                    PlanetFeature.HABITABLE_MOON
            );

            pool.remove(
                    PlanetFeature.METALLIC_MOON
            );

            pool.remove(
                    PlanetFeature.ICY_MOON
            );

            pool.remove(
                    PlanetFeature.VOLCANIC_MOON
            );

            pool.remove(
                    PlanetFeature.CRYOVOLCANIC_MOON
            );
        }

        if (hasMoon &&
                !features.contains(
                        PlanetFeature.WEAK_ATMOSPHERE)) {

            pool.add(
                    PlanetFeature.POWERFUL_WEATHER_SYSTEMS
            );
        }

        if (features.contains(
                PlanetFeature.ASTEROID_BELT)) {

            pool.add(
                    PlanetFeature.ANCIENT_IMPACT_BASIN
            );
        }

        if (features.contains(
                PlanetFeature.RING_SYSTEM)) {

            pool.add(
                    PlanetFeature.ANCIENT_IMPACT_BASIN
            );
        }

        return pool;
    }

    /**
     * Generates the weighted development pool based on
     * planet type, system region, and planetary features.
     *
     * @param type Planet type.
     * @param region Parent system region.
     * @param features Planetary features.
     * @return Weighted development pool.
     */
    private List<DevelopmentLevel> generateDevelopmentPool(
            PlanetType type,
            Region region,
            Set<PlanetFeature> features) {

        List<DevelopmentLevel> pool =
                new ArrayList<>();

        /*
         * ===== Planet Type =====
         */

        switch (type) {

            case CONTINENTAL ->
                    pool.addAll(
                            CONTINENTAL_DEVELOPMENT_POOL
                    );

            case OCEANIC ->
                    pool.addAll(
                            OCEANIC_DEVELOPMENT_POOL
                    );

            case ALPINE ->
                    pool.addAll(
                            ALPINE_DEVELOPMENT_POOL
                    );

            case ARID ->
                    pool.addAll(
                            ARID_DEVELOPMENT_POOL
                    );

            case BARREN ->
                    pool.addAll(
                            BARREN_DEVELOPMENT_POOL
                    );

            case VOLCANIC ->
                    pool.addAll(
                            VOLCANIC_DEVELOPMENT_POOL
                    );

            case FROZEN ->
                    pool.addAll(
                            FROZEN_DEVELOPMENT_POOL
                    );

            case CRYOVOLCANIC ->
                    pool.addAll(
                            CRYOVOLCANIC_DEVELOPMENT_POOL
                    );
        }


        /*
         * ===== Region =====
         */

        switch (region) {

            case CORE ->
                    pool.addAll(CORE_DEVELOPMENT_POOL);

            case INNER_RIM ->
                    pool.addAll(INNER_RIM_DEVELOPMENT_POOL);

            case OUTER_RIM ->
                    pool.addAll(OUTER_RIM_DEVELOPMENT_POOL);
        }


        /*
         * ===== Planet Features =====
         */

        if (features.contains(
                PlanetFeature.HABITABLE_MOON)) {

            pool.addAll(
                    HABITABLE_MOON_DEVELOPMENT_POOL
            );
        }

        if (features.contains(
                PlanetFeature.ASTEROID_BELT)) {

            pool.add(
                    DevelopmentLevel.INDUSTRIAL
            );
        }

        if (features.contains(
                PlanetFeature.ICY_MOON)) {

            pool.add(
                    DevelopmentLevel.DEVELOPING
            );
        }

        if (features.contains(
                PlanetFeature.METALLIC_MOON)) {

            pool.add(
                    DevelopmentLevel.INDUSTRIAL
            );
        }

        if (features.contains(
                PlanetFeature.VOLCANIC_MOON)) {

            pool.add(
                    DevelopmentLevel.INDUSTRIAL
            );
        }

        if (features.contains(
                PlanetFeature.CRYOVOLCANIC_MOON)) {

            pool.add(
                    DevelopmentLevel.INDUSTRIAL
            );
        }

        if (features.contains(
                PlanetFeature.RING_SYSTEM)) {

            pool.add(
                    DevelopmentLevel.ADVANCED
            );
        }

        if (features.contains(
                PlanetFeature.ANCIENT_IMPACT_BASIN)) {

            pool.add(
                    DevelopmentLevel.INDUSTRIAL
            );
        }

        if (features.contains(
                PlanetFeature.GEOTHERMAL_ACTIVITY)) {

            pool.add(
                    DevelopmentLevel.INDUSTRIAL
            );
        }

        if (features.contains(
                PlanetFeature.RICH_FOSSIL_DEPOSITS)) {

            pool.add(
                    DevelopmentLevel.INDUSTRIAL
            );
        }

        if (features.contains(
                PlanetFeature.DENSE_FORESTS)) {

            pool.add(
                    DevelopmentLevel.AGRARIAN
            );
        }

        if (features.contains(
                PlanetFeature.SHALLOW_SEAS)) {

            pool.add(
                    DevelopmentLevel.AGRARIAN
            );
        }

        if (features.contains(
                PlanetFeature.SALT_FLATS)) {

            pool.add(
                    DevelopmentLevel.AGRARIAN
            );
        }

        if (features.contains(
                PlanetFeature.STRONG_GRAVITY)) {

            pool.add(
                    DevelopmentLevel.COLONIAL
            );
        }

        if (features.contains(
                PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {

            pool.add(
                    DevelopmentLevel.COLONIAL
            );
        }

        if (features.contains(
                PlanetFeature.GAS_GIANT_ORBIT)) {

            pool.add(
                    DevelopmentLevel.COLONIAL
            );
        }

        if (features.contains(
                PlanetFeature.WEAK_ATMOSPHERE)) {

            pool.add(
                    DevelopmentLevel.COLONIAL
            );
        }

        if (features.contains(
                PlanetFeature.VOLCANIC_ACTIVITY)) {

            pool.add(
                    DevelopmentLevel.COLONIAL
            );
        }

        return pool;
    }

    /**
     * Generates the weighted population pool based on
     * development, planet type, and planetary features.
     *
     * @param type Planet type.
     * @param development Development level.
     * @param features Planetary features.
     * @return Weighted population pool.
     */
    private List<PopulationLevel> generatePopulationPool(
            PlanetType type,
            DevelopmentLevel development,
            Set<PlanetFeature> features) {

        List<PopulationLevel> pool =
                new ArrayList<>();

        /*
         * ===== Base Development =====
         */

        switch (development) {

            case COLONIAL ->
                    pool.addAll(
                            COLONIAL_POPULATION_POOL
                    );

            case DEVELOPING ->
                    pool.addAll(
                            DEVELOPING_POPULATION_POOL
                    );

            case AGRARIAN ->
                    pool.addAll(
                            AGRARIAN_POPULATION_POOL
                    );

            case INDUSTRIAL ->
                    pool.addAll(
                            INDUSTRIAL_POPULATION_POOL
                    );

            case ADVANCED ->
                    pool.addAll(
                            ADVANCED_POPULATION_POOL
                    );
        }


        /*
         * ===== Planet Type Caps =====
         */

        if (type == PlanetType.BARREN ||
            type == PlanetType.VOLCANIC ||
            type == PlanetType.CRYOVOLCANIC) {

            pool.remove(
                    PopulationLevel.BILLIONS
            );

            pool.remove(
                    PopulationLevel.TENS_OF_BILLIONS
            );
        }


        /*
         * ===== Feature Modifiers =====
         */

        boolean hasResourceMoon =
                features.contains(
                        PlanetFeature.METALLIC_MOON
                )
                || features.contains(
                        PlanetFeature.ICY_MOON
                )
                || features.contains(
                        PlanetFeature.VOLCANIC_MOON
                )
                || features.contains(
                        PlanetFeature.CRYOVOLCANIC_MOON
                );

        if (hasResourceMoon) {

            pool.add(
                    PopulationLevel.HUNDREDS_OF_MILLIONS
            );

            pool.add(
                    PopulationLevel.BILLIONS
            );
        }

        if (features.contains(
                PlanetFeature.HABITABLE_MOON)) {

            pool.add(
                    PopulationLevel.BILLIONS
            );

            pool.add(
                    PopulationLevel.TENS_OF_BILLIONS
            );
        }

        if (features.contains(
                PlanetFeature.DENSE_FORESTS)
                || features.contains(
                PlanetFeature.SHALLOW_SEAS)) {

            pool.add(
                    PopulationLevel.BILLIONS
            );
        }

        if (features.contains(
                PlanetFeature.DENSE_ATMOSPHERE)) {

            pool.add(
                    PopulationLevel.BILLIONS
            );
        }

        if (features.contains(
                PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {

            pool.remove(
                    PopulationLevel.TENS_OF_BILLIONS
            );
        }

        if (features.contains(
                PlanetFeature.WEAK_ATMOSPHERE)) {

            pool.remove(
                    PopulationLevel.TENS_OF_BILLIONS
            );
        }

        if (features.contains(
                PlanetFeature.STRONG_GRAVITY)) {

            pool.remove(
                    PopulationLevel.TENS_OF_BILLIONS
            );
        }

        return pool;
    }

    /**
     * Generates the weighted infrastructure pool based on
     * planetary features and development.
     *
     * @return Weighted infrastructure pool.
     */
    private List<InfrastructureLevel> generateInfrastructurePool() {

        List<InfrastructureLevel> pool =
                new ArrayList<>(
                        BASE_INFRASTRUCTURE_POOL
                );

        if (features.contains(
                PlanetFeature.HABITABLE_MOON)) {

            pool.add(
                    InfrastructureLevel.GOOD
            );

            pool.add(
                    InfrastructureLevel.EXCELLENT
            );
        }

        if (features.contains(
                PlanetFeature.ASTEROID_BELT)) {

            pool.add(
                    InfrastructureLevel.GOOD
            );
        }

        if (features.contains(
                PlanetFeature.GAS_GIANT_ORBIT)) {

            pool.add(
                    InfrastructureLevel.GOOD
            );
        }

        if (features.contains(
                PlanetFeature.RING_SYSTEM)) {

            pool.add(
                    InfrastructureLevel.GOOD
            );
        }

        if (features.contains(
                PlanetFeature.ANCIENT_IMPACT_BASIN)) {

            pool.add(
                    InfrastructureLevel.GOOD
            );
        }

        if (features.contains(
                PlanetFeature.GEOTHERMAL_ACTIVITY)) {

            pool.add(
                    InfrastructureLevel.GOOD
            );
        }

        if (features.contains(
                PlanetFeature.STRONG_GRAVITY)) {

            pool.add(
                    InfrastructureLevel.POOR
            );

            pool.add(
                    InfrastructureLevel.POOR
            );
        }

        if (features.contains(
                PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {

            pool.add(
                    InfrastructureLevel.POOR
            );
        }

        if (features.contains(
                PlanetFeature.WEAK_ATMOSPHERE)) {

            pool.add(
                    InfrastructureLevel.POOR
            );
        }

        if (features.contains(
                PlanetFeature.VOLCANIC_ACTIVITY)) {

            pool.add(
                    InfrastructureLevel.POOR
            );
        }

        if (development == DevelopmentLevel.AGRARIAN) {

            pool.add(
                    InfrastructureLevel.POOR
            );

            pool.add(
                    InfrastructureLevel.MODEST
            );
        }

        return pool;
    }

    public static int generatePlanetCount(Random random) {

        return PLANET_COUNTS.get(
                random.nextInt(PLANET_COUNTS.size())
        );
    }

    /*
     * ========================================================================
     * ===== Weighted Generation Pools
     * ========================================================================
     */


    /*
     * ===== Planet Counts =====
     */

    private static final List<Integer> PLANET_COUNTS =
            List.of(
                    1,
                    2, 2,
                    3, 3, 3, 3,
                    4, 4, 4, 4, 4,
                    5, 5,
                    6, 6,
                    7
            );


    /*
     * ===== Feature Counts =====
     */

    private static final List<Integer> FEATURE_COUNTS =
            List.of(
                    0,
                    1, 1, 1,
                    2, 2, 2,
                    3
            );


    /*
     * ===== Planet Type Pools =====
     */

    private static final List<PlanetType> CORE_INNER_PLANET_TYPES =
            List.of(
                    PlanetType.BARREN,
                    PlanetType.BARREN,
                    PlanetType.ARID,
                    PlanetType.ARID,
                    PlanetType.VOLCANIC,
                    PlanetType.VOLCANIC,
                    PlanetType.CONTINENTAL
            );

    private static final List<PlanetType> INNER_RIM_INNER_PLANET_TYPES =
            List.of(
                    PlanetType.BARREN,
                    PlanetType.BARREN,
                    PlanetType.ARID,
                    PlanetType.ARID,
                    PlanetType.VOLCANIC,
                    PlanetType.CONTINENTAL,
                    PlanetType.OCEANIC
            );

    private static final List<PlanetType> OUTER_RIM_INNER_PLANET_TYPES =
            List.of(
                    PlanetType.BARREN,
                    PlanetType.BARREN,
                    PlanetType.BARREN,
                    PlanetType.ARID,
                    PlanetType.ARID,
                    PlanetType.VOLCANIC,
                    PlanetType.CONTINENTAL
            );

    private static final List<PlanetType> CORE_MIDDLE_PLANET_TYPES =
            List.of(
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

    private static final List<PlanetType> INNER_RIM_MIDDLE_PLANET_TYPES =
            List.of(
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

    private static final List<PlanetType> OUTER_RIM_MIDDLE_PLANET_TYPES =
            List.of(
                    PlanetType.BARREN,
                    PlanetType.CONTINENTAL,
                    PlanetType.CONTINENTAL,
                    PlanetType.OCEANIC,
                    PlanetType.ALPINE,
                    PlanetType.ALPINE,
                    PlanetType.FROZEN
            );

    private static final List<PlanetType> CORE_OUTER_PLANET_TYPES =
            List.of(
                    PlanetType.BARREN,
                    PlanetType.FROZEN,
                    PlanetType.CRYOVOLCANIC,
                    PlanetType.ALPINE
            );

    private static final List<PlanetType> INNER_RIM_OUTER_PLANET_TYPES =
            List.of(
                    PlanetType.BARREN,
                    PlanetType.BARREN,
                    PlanetType.CONTINENTAL,
                    PlanetType.ALPINE,
                    PlanetType.ALPINE,
                    PlanetType.CRYOVOLCANIC,
                    PlanetType.FROZEN,
                    PlanetType.FROZEN
            );

    private static final List<PlanetType> OUTER_RIM_OUTER_PLANET_TYPES =
            List.of(
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


    /*
     * ===== Orbital Feature Pools =====
     */

    private static final List<PlanetFeature> INNER_ORBIT_FEATURES =
            List.of(
                    PlanetFeature.TECTONIC_ACTIVITY,
                    PlanetFeature.VOLCANIC_ACTIVITY,
                    PlanetFeature.GEOTHERMAL_ACTIVITY,
                    PlanetFeature.WEAK_ATMOSPHERE,
                    PlanetFeature.VOLCANIC_MOON
            );

    private static final List<PlanetFeature> MIDDLE_ORBIT_FEATURES =
            List.of(
                    PlanetFeature.TECTONIC_ACTIVITY,
                    PlanetFeature.POWERFUL_WEATHER_SYSTEMS,
                    PlanetFeature.DENSE_ATMOSPHERE,
                    PlanetFeature.HABITABLE_MOON,
                    PlanetFeature.METALLIC_MOON,
                    PlanetFeature.ASTEROID_BELT,
                    PlanetFeature.STRONG_GRAVITY
            );

    private static final List<PlanetFeature> OUTER_ORBIT_FEATURES =
            List.of(
                    PlanetFeature.GAS_GIANT_ORBIT,
                    PlanetFeature.GAS_GIANT_ORBIT,
                    PlanetFeature.ICY_MOON,
                    PlanetFeature.RING_SYSTEM,
                    PlanetFeature.ASTEROID_BELT,
                    PlanetFeature.METALLIC_MOON,
                    PlanetFeature.CRYOVOLCANIC_MOON,
                    PlanetFeature.STRONG_GRAVITY
            );


    /*
     * ===== Development Pools =====
     */

    private static final List<DevelopmentLevel> CONTINENTAL_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED
            );

    private static final List<DevelopmentLevel> OCEANIC_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED,
                    DevelopmentLevel.ADVANCED
            );

    private static final List<DevelopmentLevel> ALPINE_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED
            );

    private static final List<DevelopmentLevel> ARID_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED
            );

    private static final List<DevelopmentLevel> BARREN_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.INDUSTRIAL
            );

    private static final List<DevelopmentLevel> VOLCANIC_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.INDUSTRIAL
            );

    private static final List<DevelopmentLevel> FROZEN_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN
            );

    private static final List<DevelopmentLevel> CRYOVOLCANIC_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.INDUSTRIAL
            );


    /*
     * ===== Regional Development Pools =====
     */

    private static final List<DevelopmentLevel> CORE_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.INDUSTRIAL
            );

    private static final List<DevelopmentLevel> INNER_RIM_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED,
                    DevelopmentLevel.ADVANCED
            );

    private static final List<DevelopmentLevel> OUTER_RIM_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.COLONIAL,
                    DevelopmentLevel.DEVELOPING
            );

    private static final List<DevelopmentLevel> HABITABLE_MOON_DEVELOPMENT_POOL =
            List.of(
                    DevelopmentLevel.DEVELOPING,
                    DevelopmentLevel.AGRARIAN,
                    DevelopmentLevel.INDUSTRIAL,
                    DevelopmentLevel.ADVANCED
            );


    /*
     * ===== Population Pools =====
     */

    private static final List<PopulationLevel> COLONIAL_POPULATION_POOL =
            List.of(
                    PopulationLevel.TENS_OF_MILLIONS,
                    PopulationLevel.TENS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS
            );

    private static final List<PopulationLevel> DEVELOPING_POPULATION_POOL =
            List.of(
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS
            );

    private static final List<PopulationLevel> AGRARIAN_POPULATION_POOL =
            List.of(
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS
            );

    private static final List<PopulationLevel> INDUSTRIAL_POPULATION_POOL =
            List.of(
                    PopulationLevel.HUNDREDS_OF_MILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS
            );

    private static final List<PopulationLevel> ADVANCED_POPULATION_POOL =
            List.of(
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS,
                    PopulationLevel.TENS_OF_BILLIONS
            );


    /*
     * ===== Infrastructure Pool =====
     */

    private static final List<InfrastructureLevel> BASE_INFRASTRUCTURE_POOL =
            List.of(
                    InfrastructureLevel.POOR,

                    InfrastructureLevel.MODEST,
                    InfrastructureLevel.MODEST,
                    InfrastructureLevel.MODEST,

                    InfrastructureLevel.GOOD,

                    InfrastructureLevel.EXCELLENT
            );
}