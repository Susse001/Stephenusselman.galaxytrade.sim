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

    private final Random random = new Random();
    private CommodityRepository commodityRepository;

    public void generateAndAttachResources(Planet planet) {

    Map<CommodityType, ResourceLevel> resources =
            generateResources(planet);

    resources.forEach((commodityType, resourceLevel) -> {

        Commodity commodity = commodityRepository.findByType(commodityType);

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
            Planet planet) {

        return switch (planet.getPlanetType()) {

            case PlanetType.CONTINENTAL ->
                    generateContinentalResources(planet);

            case PlanetType.OCEANIC ->
                    generateOceanicResources(planet);

            case PlanetType.ALPINE ->
                    generateAlpineResources(planet);

            case PlanetType.ARID ->
                    generateAridResources(planet);

            case PlanetType.BARREN ->
                    generateBarrenResources(planet);

            case PlanetType.VOLCANIC ->
                    generateVolcanicResources(planet);

            case PlanetType.FROZEN ->
                    generateFrozenResources(planet);

            case PlanetType.CRYOVOLCANIC ->
                    generateCryovolcanicResources(planet);
        };
    }

    private ResourceLevel choose(List<ResourceLevel> pool) {
        return chooseWeighted(pool);
    }

    private <T> T chooseWeighted(List<T> choices) {
    return choices.get(random.nextInt(choices.size()));
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

        switch (population) {

            case TENS_OF_MILLIONS -> {
                
            }

            case HUNDREDS_OF_MILLIONS -> {

                decrease(resources, CommodityType.HYDROCARBONS);
            }

            case BILLIONS -> {

                decrease(resources, CommodityType.HYDROCARBONS);
                decrease(resources, CommodityType.RARE_METALS);
            }

            case TENS_OF_BILLIONS -> {

                decrease(resources, CommodityType.HYDROCARBONS);
                decrease(resources, CommodityType.HYDROCARBONS);

                decrease(resources, CommodityType.COMMON_METALS);
                decrease(resources, CommodityType.RARE_METALS);
                decrease(resources, CommodityType.RARE_ELEMENTS);
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
                Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateContinentalFoodPool(planet)));

        resources.put(
                CommodityType.WATER,
                choose(generateContinentalWaterPool(planet)));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateContinentalBiomaterialsPool(planet)));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateContinentalCommonMetalsPool(planet)));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateContinentalRareMetalsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateContinentalIndustrialMineralsPool(planet)));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateContinentalHydrocarbonsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateContinentalIndustrialChemicalsPool(planet)));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateContinentalRareElementsPool(planet)));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());
        
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
    * Oceanic
    * ==========================================================
    */

    /**
     * Generates the resource levels for an Oceanic planet.
     *
     * Oceanic worlds excel at biological production and water
     * resources while generally possessing fewer accessible
     * mineral deposits than Continental worlds.
     */
    private Map<CommodityType, ResourceLevel> generateOceanicResources(
            Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateOceanicFoodPool(planet)));

        resources.put(
                CommodityType.WATER,
                choose(generateOceanicWaterPool(planet)));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateOceanicBiomaterialsPool(planet)));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateOceanicCommonMetalsPool(planet)));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateOceanicRareMetalsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateOceanicIndustrialMineralsPool(planet)));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateOceanicHydrocarbonsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateOceanicIndustrialChemicalsPool(planet)));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateOceanicRareElementsPool(planet)));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());

        return resources;
    }

    private List<ResourceLevel> generateOceanicFoodPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicWaterPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicBiomaterialsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicCommonMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicRareMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateOceanicIndustrialMineralsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicHydrocarbonsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicIndustrialChemicalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateOceanicRareElementsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    /*
    * ==========================================================
    * Arid
    * ==========================================================
    */

    /**
     * Generates the resource levels for an Arid planet.
     *
     * Arid worlds trade biological productivity for mineral
     * wealth and are frequently developed as extraction colonies.
     */
    private Map<CommodityType, ResourceLevel> generateAridResources(
            Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateAridFoodPool(planet)));

        resources.put(
                CommodityType.WATER,
                choose(generateAridWaterPool(planet)));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateAridBiomaterialsPool(planet)));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateAridCommonMetalsPool(planet)));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateAridRareMetalsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateAridIndustrialMineralsPool(planet)));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateAridHydrocarbonsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateAridIndustrialChemicalsPool(planet)));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateAridRareElementsPool(planet)));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());

        return resources;
    }

    private List<ResourceLevel> generateAridFoodPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridWaterPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateAridBiomaterialsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateAridCommonMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridRareMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridIndustrialMineralsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridHydrocarbonsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridIndustrialChemicalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAridRareElementsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
    * ==========================================================
    * Alpine
    * ==========================================================
    */

    /**
     * Generates the resource levels for an Alpine planet.
     *
     * Alpine worlds are rugged but habitable, combining
     * moderate biological productivity with above-average
     * mineral wealth.
     */
    private Map<CommodityType, ResourceLevel> generateAlpineResources(
            Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateAlpineFoodPool(planet)));

        resources.put(
                CommodityType.WATER,
                choose(generateAlpineWaterPool(planet)));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateAlpineBiomaterialsPool(planet)));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateAlpineCommonMetalsPool(planet)));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateAlpineRareMetalsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateAlpineIndustrialMineralsPool(planet)));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateAlpineHydrocarbonsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateAlpineIndustrialChemicalsPool(planet)));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateAlpineRareElementsPool(planet)));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());

        return resources;
    }

    private List<ResourceLevel> generateAlpineFoodPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineWaterPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineBiomaterialsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineCommonMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineRareMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineIndustrialMineralsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineHydrocarbonsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineIndustrialChemicalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateAlpineRareElementsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
    * ==========================================================
    * Frozen
    * ==========================================================
    */

    /**
     * Generates the resource levels for a Frozen planet.
     *
     * Frozen worlds possess enormous freshwater reserves and
     * respectable mineral wealth, but limited biological
     * productivity due to their harsh climates.
     */
    private Map<CommodityType, ResourceLevel> generateFrozenResources(
            Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateFrozenFoodPool(planet)));

        resources.put(
                CommodityType.WATER,
                choose(generateFrozenWaterPool(planet)));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateFrozenBiomaterialsPool(planet)));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateFrozenCommonMetalsPool(planet)));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateFrozenRareMetalsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateFrozenIndustrialMineralsPool(planet)));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateFrozenHydrocarbonsPool(planet)));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateFrozenIndustrialChemicalsPool(planet)));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateFrozenRareElementsPool(planet)));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());

        return resources;
    }

    private List<ResourceLevel> generateFrozenFoodPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateFrozenWaterPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenBiomaterialsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateFrozenCommonMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenRareMetalsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenIndustrialMineralsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenHydrocarbonsPool(Planet planet) {
        return List.of(
                ResourceLevel.SCARCE,
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE
        );
    }

    private List<ResourceLevel> generateFrozenIndustrialChemicalsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    private List<ResourceLevel> generateFrozenRareElementsPool(Planet planet) {
        return List.of(
                ResourceLevel.AVERAGE,
                ResourceLevel.AVERAGE,
                ResourceLevel.RICH
        );
    }

    /*
    * ==========================================================
    * Barren
    * ==========================================================
    */

    /**
     * Generates the resource levels for a Barren planet.
     *
     * Barren worlds are poor in biological resources but are
     * often among the galaxy's richest sources of mineral wealth.
     */
    private Map<CommodityType, ResourceLevel> generateBarrenResources(
            Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateBarrenFoodPool()));

        resources.put(
                CommodityType.WATER,
                choose(generateBarrenWaterPool()));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateBarrenBiomaterialsPool()));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateBarrenCommonMetalsPool()));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateBarrenRareMetalsPool()));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateBarrenIndustrialMineralsPool()));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateBarrenHydrocarbonsPool()));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateBarrenIndustrialChemicalsPool()));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateBarrenRareElementsPool()));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());

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
    * Volcanic
    * ==========================================================
    */

    /**
     * Generates the resource levels for a Volcanic planet.
     *
     * Volcanic worlds are extremely mineral rich but biologically poor.
     * They excel at metals, minerals, and geothermal resources while
     * producing very little food or water.
     */
    private Map<CommodityType, ResourceLevel> generateVolcanicResources(
            Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateVolcanicFoodPool()));

        resources.put(
                CommodityType.WATER,
                choose(generateVolcanicWaterPool()));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateVolcanicBiomaterialsPool()));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateVolcanicCommonMetalsPool()));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateVolcanicRareMetalsPool()));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateVolcanicIndustrialMineralsPool()));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateVolcanicHydrocarbonsPool()));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateVolcanicIndustrialChemicalsPool()));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateVolcanicRareElementsPool()));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());

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
    * Cryovolcanic
    * ==========================================================
    */

    /**
     * Generates the resource levels for a Cryovolcanic planet.
     *
     * Cryovolcanic worlds are geologically active frozen planets.
     * They are poor agricultural worlds but rich in volatiles,
     * industrial chemicals, and exotic elements.
     */
    private Map<CommodityType, ResourceLevel> generateCryovolcanicResources(
            Planet planet) {

        Set<PlanetFeature> features = planet.getFeatures();

        Map<CommodityType, ResourceLevel> resources =
                new EnumMap<>(CommodityType.class);

        resources.put(
                CommodityType.FOOD,
                choose(generateCryovolcanicFoodPool()));

        resources.put(
                CommodityType.WATER,
                choose(generateCryovolcanicWaterPool()));

        resources.put(
                CommodityType.BIOMATERIALS,
                choose(generateCryovolcanicBiomaterialsPool()));

        resources.put(
                CommodityType.COMMON_METALS,
                choose(generateCryovolcanicCommonMetalsPool()));

        resources.put(
                CommodityType.RARE_METALS,
                choose(generateCryovolcanicRareMetalsPool()));

        resources.put(
                CommodityType.INDUSTRIAL_MINERALS,
                choose(generateCryovolcanicIndustrialMineralsPool()));

        resources.put(
                CommodityType.HYDROCARBONS,
                choose(generateCryovolcanicHydrocarbonsPool()));

        resources.put(
                CommodityType.INDUSTRIAL_CHEMICALS,
                choose(generateCryovolcanicIndustrialChemicalsPool()));

        resources.put(
                CommodityType.RARE_ELEMENTS,
                choose(generateCryovolcanicRareElementsPool()));

        resources = applyPlanetFeatures(resources, features);
        resources = applyPopulationLevel(resources, planet.getPopulation());

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
