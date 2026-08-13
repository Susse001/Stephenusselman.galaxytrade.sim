package com.stephenu.gts.planet;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.stephenu.gts.commodity.CommodityType;

public class PlanetProductionProfile {

    Planet planet;

    Map<CommodityType, Double> extractionCapacity;

    Map<CommodityType, Double> manufacturingCapacity;

    private static final Map<CommodityType, Double> BASE_EXTRACTION =
        Map.of(

                    CommodityType.FOOD, 160.0,
                    CommodityType.WATER, 150.0,
                    CommodityType.BIOMATERIALS, 65.0,

                    CommodityType.COMMON_METALS, 95.0,
                    CommodityType.RARE_METALS, 32.0,
                    CommodityType.INDUSTRIAL_MINERALS, 50.0,

                    CommodityType.HYDROCARBONS, 80.0,
                    CommodityType.INDUSTRIAL_CHEMICALS, 60.0,
                    CommodityType.RARE_ELEMENTS, 24.0
            );

    private static final Map<ResourceLevel, Double> RESOURCE_MULTIPLIERS =
        Map.of(
                ResourceLevel.NONE, 0.0,
                ResourceLevel.SCARCE, 0.35,
                ResourceLevel.AVERAGE, 1.0,
                ResourceLevel.RICH, 1.75,
                ResourceLevel.ABUNDANT, 2.5
        );

    private static final Map<PopulationLevel, Double> EXTRACTION_POPULATION_MULTIPLIERS =
        Map.of(
                PopulationLevel.TENS_OF_MILLIONS, 0.50,
                PopulationLevel.HUNDREDS_OF_MILLIONS, 0.8,
                PopulationLevel.BILLIONS, 1.00,
                PopulationLevel.TENS_OF_BILLIONS, 1.1
        );

    private static final Map<DevelopmentLevel, Double> EXTRACTION_DEVELOPMENT_MULTIPLIERS =
        Map.of(
                DevelopmentLevel.COLONIAL, 0.80,
                DevelopmentLevel.AGRARIAN, 0.90,
                DevelopmentLevel.DEVELOPING, 1.00,
                DevelopmentLevel.INDUSTRIAL, 1.10,
                DevelopmentLevel.ADVANCED, 1.20
        );

    private static final Map<InfrastructureLevel, Double> EXTRACTION_INFRASTRUCTURE_MULTIPLIERS =
        Map.of(
                InfrastructureLevel.POOR, 0.65,
                InfrastructureLevel.MODEST, 0.85,
                InfrastructureLevel.GOOD, 1.15,
                InfrastructureLevel.EXCELLENT, 1.40
        );

    private Map<CommodityType, Double> calculateExtractionCapacity(
            Planet planet) {

        Map<CommodityType, Double> extraction =
                new EnumMap<>(CommodityType.class);

        Map<CommodityType, ResourceLevel> resourceLevels =
            planet.getResources()
                    .stream()
                    .collect(Collectors.toMap(
                            resource -> resource.getCommodity().getType(),
                            PlanetResource::getAbundance
                    ));

        double populationMultiplier =
                EXTRACTION_POPULATION_MULTIPLIERS
                        .get(planet.getPopulation());

        double developmentMultiplier =
                EXTRACTION_DEVELOPMENT_MULTIPLIERS
                        .get(planet.getDevelopment());

        double infrastructureMultiplier =
                EXTRACTION_INFRASTRUCTURE_MULTIPLIERS
                        .get(planet.getInfrastructure());

        for (Map.Entry<CommodityType, Double> entry :
                BASE_EXTRACTION.entrySet()) {

            CommodityType commodity = entry.getKey();
            double baseline = entry.getValue();

            ResourceLevel resourceLevel =
                resourceLevels.get(commodity);

            if (resourceLevel == null) {
                throw new IllegalStateException(
                        "Planet is missing resource level for: " + commodity
                );
            }

            double resourceMultiplier =
                    RESOURCE_MULTIPLIERS.get(resourceLevel);

            double capacity =
                    baseline
                    * resourceMultiplier
                    * populationMultiplier
                    * developmentMultiplier
                    * infrastructureMultiplier;

            extraction.put(commodity, capacity);
        }

        return extraction;
    }
}
