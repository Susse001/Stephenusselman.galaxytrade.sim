package com.stephenu.gts.planet;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.stephenu.gts.commodity.CommodityType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlanetProductionProfile {

    private Map<CommodityType, Double> extractionCapacity =
            new EnumMap<>(CommodityType.class);

    private Map<CommodityType, Double> manufacturingPotential =
            new EnumMap<>(CommodityType.class);

    public PlanetProductionProfile(
            Map<CommodityType, Double> extractionCapacity,
            Map<CommodityType, Double> manufacturingPotential) {

        this.extractionCapacity = extractionCapacity;
        this.manufacturingPotential = manufacturingPotential;
    }

    public PlanetProductionProfile generateProfile(Planet planet) {

        Map<CommodityType, Double> extractionCapacity =
                calculateExtractionCapacity(planet);

        Map<CommodityType, Double> manufacturingPotential =
                calculateManufacturingPotential(planet);

        return new PlanetProductionProfile(
                extractionCapacity,
                manufacturingPotential
        );
    }

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

    private static final Map<CommodityType, Double> BASE_MANUFACTURING =
         Map.ofEntries(

                // Tier 2
                Map.entry(CommodityType.REFINED_METALS, 42.0),
                Map.entry(CommodityType.PETROCHEMICALS, 35.0),
                Map.entry(CommodityType.ADVANCED_MATERIALS, 30.0),
                Map.entry(CommodityType.MANUFACTURED_PARTS, 38.0),
                Map.entry(CommodityType.ELECTRONIC_COMPONENTS, 32.0),
                Map.entry(CommodityType.PHARMACEUTICALS, 35.0),
                Map.entry(CommodityType.FUEL, 30.0),

                // Tier 3
                Map.entry(CommodityType.CONSUMER_GOODS, 32.0),
                Map.entry(CommodityType.MEDICAL_SUPPLIES, 15.0),
                Map.entry(CommodityType.INDUSTRIAL_MACHINERY, 14.0),
                Map.entry(CommodityType.ELECTRONICS, 14.0),
                Map.entry(CommodityType.CAPITAL_GOODS, 7.0),
                Map.entry(CommodityType.LUXURY_GOODS, 4.0)
        );

    private static final Map<PopulationLevel, Double> MANUFACTURING_POPULATION_MULTIPLIERS =
        Map.of(
                PopulationLevel.TENS_OF_MILLIONS, 0.70,
                PopulationLevel.HUNDREDS_OF_MILLIONS, 0.85,
                PopulationLevel.BILLIONS, 1.00,
                PopulationLevel.TENS_OF_BILLIONS, 1.15
        );

    private static final Map<InfrastructureLevel, Double> MANUFACTURING_INFRASTRUCTURE_MULTIPLIERS =
        Map.of(
                InfrastructureLevel.POOR, 0.80,
                InfrastructureLevel.MODEST, 0.90,
                InfrastructureLevel.GOOD, 1.10,
                InfrastructureLevel.EXCELLENT, 1.20
        );
    
    private static final Map<DevelopmentLevel,Map<CommodityType, Double>> DEVELOPMENT_MANUFACTURING_MODIFIERS =
        Map.ofEntries(

                Map.entry(
                        DevelopmentLevel.COLONIAL,
                        Map.ofEntries(
                                Map.entry(CommodityType.REFINED_METALS, 0.50),
                                Map.entry(CommodityType.PETROCHEMICALS, 0.50),
                                Map.entry(CommodityType.ADVANCED_MATERIALS, 0.50),
                                Map.entry(CommodityType.MANUFACTURED_PARTS, 0.50),
                                Map.entry(CommodityType.ELECTRONIC_COMPONENTS, 0.50),
                                Map.entry(CommodityType.PHARMACEUTICALS, 0.50),
                                Map.entry(CommodityType.FUEL, 0.50),
                                Map.entry(CommodityType.CONSUMER_GOODS, 0.30),
                                Map.entry(CommodityType.MEDICAL_SUPPLIES, 0.30),
                                Map.entry(CommodityType.INDUSTRIAL_MACHINERY, 0.30),
                                Map.entry(CommodityType.ELECTRONICS, 0.30),
                                Map.entry(CommodityType.CAPITAL_GOODS, 0.30),
                                Map.entry(CommodityType.LUXURY_GOODS, 0.30)
                        )
                ),

                Map.entry(
                        DevelopmentLevel.AGRARIAN,
                        Map.ofEntries(
                                Map.entry(CommodityType.REFINED_METALS, 0.75),
                                Map.entry(CommodityType.PETROCHEMICALS, 0.75),
                                Map.entry(CommodityType.ADVANCED_MATERIALS, 0.75),
                                Map.entry(CommodityType.MANUFACTURED_PARTS, 0.75),
                                Map.entry(CommodityType.ELECTRONIC_COMPONENTS, 0.75),
                                Map.entry(CommodityType.PHARMACEUTICALS, 0.75),
                                Map.entry(CommodityType.FUEL, 0.75),
                                Map.entry(CommodityType.CONSUMER_GOODS, 0.60),
                                Map.entry(CommodityType.MEDICAL_SUPPLIES, 0.60),
                                Map.entry(CommodityType.INDUSTRIAL_MACHINERY, 0.60),
                                Map.entry(CommodityType.ELECTRONICS, 0.60),
                                Map.entry(CommodityType.CAPITAL_GOODS, 0.60),
                                Map.entry(CommodityType.LUXURY_GOODS, 0.60)
                        )
                ),

                Map.entry(
                        DevelopmentLevel.DEVELOPING,
                        Map.ofEntries(
                                Map.entry(CommodityType.CONSUMER_GOODS, 0.90),
                                Map.entry(CommodityType.MEDICAL_SUPPLIES, 0.90),
                                Map.entry(CommodityType.INDUSTRIAL_MACHINERY, 0.90),
                                Map.entry(CommodityType.ELECTRONICS, 0.80),
                                Map.entry(CommodityType.CAPITAL_GOODS, 0.80),
                                Map.entry(CommodityType.LUXURY_GOODS, 0.80)
                        )
                ),

                Map.entry(
                        DevelopmentLevel.INDUSTRIAL,
                        Map.ofEntries(
                                Map.entry(CommodityType.REFINED_METALS, 1.20),
                                Map.entry(CommodityType.PETROCHEMICALS, 1.20),
                                Map.entry(CommodityType.ADVANCED_MATERIALS, 1.20),
                                Map.entry(CommodityType.MANUFACTURED_PARTS, 1.20),
                                Map.entry(CommodityType.ELECTRONIC_COMPONENTS, 1.20),
                                Map.entry(CommodityType.PHARMACEUTICALS, 1.20),
                                Map.entry(CommodityType.FUEL, 1.20)
                        )
                ),

                Map.entry(
                        DevelopmentLevel.ADVANCED,
                        Map.ofEntries(
                                Map.entry(CommodityType.CONSUMER_GOODS, 1.25),
                                Map.entry(CommodityType.MEDICAL_SUPPLIES, 1.25),
                                Map.entry(CommodityType.INDUSTRIAL_MACHINERY, 1.25),
                                Map.entry(CommodityType.ELECTRONICS, 1.25),
                                Map.entry(CommodityType.CAPITAL_GOODS, 1.25),
                                Map.entry(CommodityType.LUXURY_GOODS, 1.25)
                        )
                )
        );
    
    private Map<CommodityType, Double> calculateManufacturingPotential(Planet planet) {

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);

        double populationMultiplier =
                MANUFACTURING_POPULATION_MULTIPLIERS
                        .get(planet.getPopulation());

        double infrastructureMultiplier =
                MANUFACTURING_INFRASTRUCTURE_MULTIPLIERS
                        .get(planet.getInfrastructure());

        Map<CommodityType, Double> developmentModifiers =
                DEVELOPMENT_MANUFACTURING_MODIFIERS
                        .getOrDefault(
                                planet.getDevelopment(),
                                Map.of()
                        );

        for (Map.Entry<CommodityType, Double> entry :
                BASE_MANUFACTURING.entrySet()) {

            CommodityType commodity = entry.getKey();

            double baseline = entry.getValue();

            double developmentMultiplier =
                    developmentModifiers.getOrDefault(
                            commodity,
                            1.0
                    );

            double potential =
                    baseline
                    * populationMultiplier
                    * infrastructureMultiplier
                    * developmentMultiplier;

            manufacturing.put(
                    commodity,
                    potential
            );
        }

        return manufacturing;
    }

    public Map<CommodityType, Double> getBaseManufacturing() {
        return BASE_MANUFACTURING;
    }
}
