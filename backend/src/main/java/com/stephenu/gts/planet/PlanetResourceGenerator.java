package com.stephenu.gts.planet;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityRepository;
import com.stephenu.gts.commodity.CommodityType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlanetResourceGenerator {

    private final CommodityRepository commodityRepository;

    public void generateAndAttachResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                generateResources(planet, random);

        resources.forEach((commodityType, resourceLevel) -> {

            Commodity commodity =
                    commodityRepository.findByType(commodityType);

            planet.getResources().add(
                    new PlanetResource(
                            planet,
                            commodity,
                            resourceLevel
                    )
            );
        });
    }

    public Map<CommodityType, ResourceLevel> generateResources(
            Planet planet,
            Random random) {

        return switch (planet.getPlanetType()) {

            case PlanetType.CONTINENTAL ->
                    generateContinentalResources(
                            planet,
                            random
                    );

            case PlanetType.OCEANIC ->
                    generateOceanicResources(
                            planet,
                            random
                    );

            case PlanetType.ALPINE ->
                    generateAlpineResources(
                            planet,
                            random
                    );

            case PlanetType.ARID ->
                    generateAridResources(
                            planet,
                            random
                    );

            case PlanetType.BARREN ->
                    generateBarrenResources(
                            planet,
                            random
                    );

            case PlanetType.VOLCANIC ->
                    generateVolcanicResources(
                            planet,
                            random
                    );

            case PlanetType.FROZEN ->
                    generateFrozenResources(
                            planet,
                            random
                    );

            case PlanetType.CRYOVOLCANIC ->
                    generateCryovolcanicResources(
                            planet,
                            random
                    );
        };
    }

    private <T> T chooseWeighted(
            List<T> choices,
            Random random) {

        return choices.get(
                random.nextInt(choices.size())
        );
    }

    private void increase(
            Map<CommodityType, ResourceLevel> resources,
            CommodityType commodity) {

        ResourceLevel current = resources.get(commodity);

        ResourceLevel upgraded = switch (current) {
            case NONE -> ResourceLevel.SCARCE;
            case SCARCE -> ResourceLevel.AVERAGE;
            case AVERAGE -> ResourceLevel.RICH;
            case RICH -> ResourceLevel.ABUNDANT;
            case ABUNDANT -> ResourceLevel.ABUNDANT;
        };

        resources.put(commodity, upgraded);
    }

    private void decrease(
            Map<CommodityType, ResourceLevel> resources,
            CommodityType commodity) {

        ResourceLevel current = resources.get(commodity);

        ResourceLevel downgraded = switch (current) {
            case NONE -> ResourceLevel.NONE;
            case SCARCE -> ResourceLevel.NONE;
            case AVERAGE -> ResourceLevel.SCARCE;
            case RICH -> ResourceLevel.AVERAGE;
            case ABUNDANT -> ResourceLevel.RICH;
        };

        resources.put(commodity, downgraded);
    }

    /**
     * Applies resource bonuses from planetary features after the base
     * resource distribution has been generated from the planet type.
     *
     * Features generally improve one or two related resources by one
     * ResourceLevel rather than replacing the planet's identity.
     */
    private Map<CommodityType, ResourceLevel> applyPlanetFeatures(
            Map<CommodityType, ResourceLevel> resources,
            Set<PlanetFeature> features) {

        if (features == null) {
            return resources;
        }

        if (features.contains(PlanetFeature.ASTEROID_BELT)) {

            increase(resources, CommodityType.COMMON_METALS);
            increase(resources, CommodityType.RARE_METALS);
        }

        if (features.contains(PlanetFeature.GAS_GIANT_ORBIT)) {

            increase(resources, CommodityType.HYDROCARBONS);
        }

        if (features.contains(PlanetFeature.HABITABLE_MOON)) {

            increase(resources, CommodityType.BIOMATERIALS);
            increase(resources, CommodityType.WATER);
            increase(resources, CommodityType.FOOD);
        }

        if (features.contains(PlanetFeature.METALLIC_MOON)) {

            increase(resources, CommodityType.COMMON_METALS);
            increase(resources, CommodityType.RARE_METALS);
        }

        if (features.contains(PlanetFeature.ICY_MOON)) {

            increase(resources, CommodityType.WATER);
        }

        if (features.contains(PlanetFeature.VOLCANIC_MOON)) {

            increase(resources, CommodityType.RARE_METALS);
            increase(resources, CommodityType.INDUSTRIAL_MINERALS);
        }

        if (features.contains(PlanetFeature.CRYOVOLCANIC_MOON)) {

            increase(resources, CommodityType.WATER);
            increase(resources, CommodityType.INDUSTRIAL_CHEMICALS);
        }

        if (features.contains(PlanetFeature.RING_SYSTEM)) {

            increase(resources, CommodityType.INDUSTRIAL_MINERALS);
        }

        if (features.contains(PlanetFeature.TECTONIC_ACTIVITY)) {

            increase(resources, CommodityType.COMMON_METALS);
            increase(resources, CommodityType.INDUSTRIAL_MINERALS);
        }

        if (features.contains(PlanetFeature.SALT_FLATS)) {

            increase(resources, CommodityType.INDUSTRIAL_CHEMICALS);
            decrease(resources, CommodityType.FOOD);
        }

        if (features.contains(PlanetFeature.DENSE_FORESTS)) {

            increase(resources, CommodityType.BIOMATERIALS);
        }

        if (features.contains(PlanetFeature.STRONG_GRAVITY)) {

            decrease(resources, CommodityType.FOOD);
        }

        if (features.contains(PlanetFeature.ANCIENT_IMPACT_BASIN)) {

            increase(resources, CommodityType.RARE_METALS);
            increase(resources, CommodityType.RARE_ELEMENTS);
        }

        if (features.contains(PlanetFeature.SUBSURFACE_OCEAN)) {

            increase(resources, CommodityType.WATER);
        }

        if (features.contains(PlanetFeature.SHALLOW_SEAS)) {

            increase(resources, CommodityType.FOOD);
            increase(resources, CommodityType.WATER);
        }

        if (features.contains(PlanetFeature.DENSE_ATMOSPHERE)) {

            increase(resources, CommodityType.BIOMATERIALS);
        }

        if (features.contains(PlanetFeature.GEOTHERMAL_ACTIVITY)) {

            increase(resources, CommodityType.INDUSTRIAL_CHEMICALS);
            increase(resources, CommodityType.RARE_ELEMENTS);
        }

        if (features.contains(PlanetFeature.VOLCANIC_ACTIVITY)) {

            increase(resources, CommodityType.RARE_METALS);
            increase(resources, CommodityType.INDUSTRIAL_MINERALS);

            decrease(resources, CommodityType.HYDROCARBONS);
        }

        if (features.contains(PlanetFeature.POWERFUL_WEATHER_SYSTEMS)) {

            increase(resources, CommodityType.WATER);
            decrease(resources, CommodityType.FOOD);
        }

        if (features.contains(PlanetFeature.WEAK_ATMOSPHERE)) {

            decrease(resources, CommodityType.FOOD);
            decrease(resources, CommodityType.BIOMATERIALS);
        }

        if (features.contains(PlanetFeature.RICH_FOSSIL_DEPOSITS)) {

            increase(resources, CommodityType.HYDROCARBONS);
        }

        return resources;
    }

    /**
     * Applies long-term resource depletion caused by the planet's population.
     *
     * Larger populations consume and exploit accessible non-renewable
     * resources over time. Renewable resources are intentionally left
     * unchanged, as production rather than deposits will determine their
     * eventual output.
     */
    private Map<CommodityType, ResourceLevel> applyPopulationLevel(
            Map<CommodityType, ResourceLevel> resources,
            PopulationLevel population) {

        if (population == null) {
            return resources;
        }

        switch (population) {

            case TENS_OF_MILLIONS -> {
            }

            case HUNDREDS_OF_MILLIONS -> {

                decrease(
                        resources,
                        CommodityType.HYDROCARBONS
                );
            }

            case BILLIONS -> {

                decrease(
                        resources,
                        CommodityType.HYDROCARBONS
                );

                decrease(
                        resources,
                        CommodityType.RARE_METALS
                );
            }

            case TENS_OF_BILLIONS -> {

                decrease(
                        resources,
                        CommodityType.HYDROCARBONS
                );

                decrease(
                        resources,
                        CommodityType.HYDROCARBONS
                );

                decrease(
                        resources,
                        CommodityType.COMMON_METALS
                );

                decrease(
                        resources,
                        CommodityType.RARE_METALS
                );

                decrease(
                        resources,
                        CommodityType.RARE_ELEMENTS
                );
            }
        }

        return resources;
    }

    /*
     * ==========================================================
     * CONTINENTAL
     * ==========================================================
     */

    /**
     * Generates the resource levels for a Continental planet.
     *
     * Continental worlds are the most balanced planet type,
     * capable of producing every Tier 1 resource without
     * specializing in any particular one.
     */
    private Map<CommodityType, ResourceLevel> generateContinentalResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateContinentalFoodPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateContinentalWaterPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateContinentalBiomaterialsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateContinentalCommonMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateContinentalRareMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateContinentalIndustrialMineralsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateContinentalHydrocarbonsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateContinentalIndustrialChemicalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateContinentalRareElementsPool(planet),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateContinentalFoodPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalWaterPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalBiomaterialsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalCommonMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalRareMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalIndustrialMineralsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalHydrocarbonsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalIndustrialChemicalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateContinentalRareElementsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
     * ==========================================================
     * OCEANIC
     * ==========================================================
     */

    private Map<CommodityType, ResourceLevel> generateOceanicResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateOceanicFoodPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateOceanicWaterPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateOceanicBiomaterialsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateOceanicCommonMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateOceanicRareMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateOceanicIndustrialMineralsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateOceanicHydrocarbonsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateOceanicIndustrialChemicalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateOceanicRareElementsPool(planet),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateOceanicFoodPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicWaterPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicBiomaterialsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicCommonMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicRareMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateOceanicIndustrialMineralsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicHydrocarbonsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicIndustrialChemicalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicRareElementsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    /*
     * ==========================================================
     * ARID
     * ==========================================================
     */

    private Map<CommodityType, ResourceLevel> generateAridResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateAridFoodPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateAridWaterPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateAridBiomaterialsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateAridCommonMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateAridRareMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateAridIndustrialMineralsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateAridHydrocarbonsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateAridIndustrialChemicalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateAridRareElementsPool(planet),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateAridFoodPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridWaterPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateAridBiomaterialsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateAridCommonMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridRareMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridIndustrialMineralsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridHydrocarbonsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridIndustrialChemicalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridRareElementsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
     * ==========================================================
     * ALPINE
     * ==========================================================
     */

    private Map<CommodityType, ResourceLevel> generateAlpineResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateAlpineFoodPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateAlpineWaterPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateAlpineBiomaterialsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateAlpineCommonMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateAlpineRareMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateAlpineIndustrialMineralsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateAlpineHydrocarbonsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateAlpineIndustrialChemicalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateAlpineRareElementsPool(planet),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateAlpineFoodPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineWaterPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineBiomaterialsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineCommonMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineRareMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineIndustrialMineralsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineHydrocarbonsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineIndustrialChemicalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineRareElementsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
     * ==========================================================
     * FROZEN
     * ==========================================================
     */

    private Map<CommodityType, ResourceLevel> generateFrozenResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateFrozenFoodPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateFrozenWaterPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateFrozenBiomaterialsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateFrozenCommonMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateFrozenRareMetalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateFrozenIndustrialMineralsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateFrozenHydrocarbonsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateFrozenIndustrialChemicalsPool(planet),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateFrozenRareElementsPool(planet),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateFrozenFoodPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateFrozenWaterPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenBiomaterialsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateFrozenCommonMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenRareMetalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenIndustrialMineralsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenHydrocarbonsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateFrozenIndustrialChemicalsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenRareElementsPool(
            Planet planet) {

        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
     * ==========================================================
     * BARREN
     * ==========================================================
     */

    private Map<CommodityType, ResourceLevel> generateBarrenResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateBarrenFoodPool(),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateBarrenWaterPool(),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateBarrenBiomaterialsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateBarrenCommonMetalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateBarrenRareMetalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateBarrenIndustrialMineralsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateBarrenHydrocarbonsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateBarrenIndustrialChemicalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateBarrenRareElementsPool(),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateBarrenFoodPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateBarrenWaterPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateBarrenBiomaterialsPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateBarrenCommonMetalsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateBarrenRareMetalsPool() {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateBarrenIndustrialMineralsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateBarrenHydrocarbonsPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateBarrenIndustrialChemicalsPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateBarrenRareElementsPool() {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
     * ==========================================================
     * VOLCANIC
     * ==========================================================
     */

    private Map<CommodityType, ResourceLevel> generateVolcanicResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateVolcanicFoodPool(),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateVolcanicWaterPool(),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateVolcanicBiomaterialsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateVolcanicCommonMetalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateVolcanicRareMetalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateVolcanicIndustrialMineralsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateVolcanicHydrocarbonsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateVolcanicIndustrialChemicalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateVolcanicRareElementsPool(),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateVolcanicFoodPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateVolcanicWaterPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateVolcanicBiomaterialsPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateVolcanicCommonMetalsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateVolcanicRareMetalsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateVolcanicIndustrialMineralsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateVolcanicHydrocarbonsPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateVolcanicIndustrialChemicalsPool() {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateVolcanicRareElementsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
     * ==========================================================
     * CRYOVOLCANIC
     * ==========================================================
     */

    private Map<CommodityType, ResourceLevel> generateCryovolcanicResources(
            Planet planet,
            Random random) {

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                chooseWeighted(
                        generateCryovolcanicFoodPool(),
                        random
                )
        );

        resources.put(
                CommodityType.WATER,
                chooseWeighted(
                        generateCryovolcanicWaterPool(),
                        random
                )
        );

        resources.put(
                CommodityType.BIOMATERIALS,
                chooseWeighted(
                        generateCryovolcanicBiomaterialsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.COMMON_METALS,
                chooseWeighted(
                        generateCryovolcanicCommonMetalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_METALS,
                chooseWeighted(
                        generateCryovolcanicRareMetalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                chooseWeighted(
                        generateCryovolcanicIndustrialMineralsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.HYDROCARBONS,
                chooseWeighted(
                        generateCryovolcanicHydrocarbonsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                chooseWeighted(
                        generateCryovolcanicIndustrialChemicalsPool(),
                        random
                )
        );

        resources.put(
                CommodityType.RARE_ELEMENTS,
                chooseWeighted(
                        generateCryovolcanicRareElementsPool(),
                        random
                )
        );

        resources = applyPlanetFeatures(
                resources,
                planet.getFeatures()
        );

        resources = applyPopulationLevel(
                resources,
                planet.getPopulation()
        );

        return resources;
    }

    private List<ResourceLevel> generateCryovolcanicFoodPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateCryovolcanicWaterPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateCryovolcanicBiomaterialsPool() {
        return List.of(
                ResourceLevel.NONE,
                ResourceLevel.SCARCE
        );
    }

    private List<ResourceLevel> generateCryovolcanicCommonMetalsPool() {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateCryovolcanicRareMetalsPool() {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateCryovolcanicIndustrialMineralsPool() {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateCryovolcanicHydrocarbonsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateCryovolcanicIndustrialChemicalsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateCryovolcanicRareElementsPool() {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }
}
