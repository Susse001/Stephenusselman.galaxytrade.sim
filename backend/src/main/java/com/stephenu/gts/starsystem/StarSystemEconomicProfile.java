package com.stephenu.gts.starsystem;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.commodity.ProductionRecipe;
import com.stephenu.gts.planet.Planet;
import com.stephenu.gts.planet.PlanetProductionProfile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StarSystemEconomicProfile {

    /**
     * Combined extraction capacity of all planets in the system.
     */
    private Map<CommodityType, Double> extractionCapacity =
            new EnumMap<>(CommodityType.class);

    /**
     * Combined manufacturing potential of all planets in the system.
     */
    private Map<CommodityType, Double> manufacturingPotential =
            new EnumMap<>(CommodityType.class);

    /**
     * Current manufacturing allocation of all planets in the system.
     */
    private Map<CommodityType, Double> manufacturing =
            new EnumMap<>(CommodityType.class);

    /**
     * Combined consumption of all planets in the system.
     */
    private Map<CommodityType, Double> consumption =
            new EnumMap<>(CommodityType.class);

    public StarSystemEconomicProfile(
            Map<CommodityType, Double> extractionCapacity,
            Map<CommodityType, Double> manufacturingPotential,
            Map<CommodityType, Double> consumption) {

        this.extractionCapacity = extractionCapacity;
        this.manufacturingPotential = manufacturingPotential;
        this.consumption = consumption;
    }

    /**
     * Generates the complete economic profile for a star system.
     *
     * Planetary extraction capacity, manufacturing potential, and
     * consumption are combined before available manufacturing capacity
     * is allocated across system industries.
     *
     * @param system Star system whose economy is being generated.
     * @param commodities Commodities indexed by commodity type.
     * @return Completed economic profile for the star system.
     */
    public StarSystemEconomicProfile generateProfile(
            StarSystem system,
            Map<CommodityType, Commodity> commodities) {

        Map<CommodityType, Double> extraction =
                new EnumMap<>(CommodityType.class);

        Map<CommodityType, Double> manufacturingPotential =
                new EnumMap<>(CommodityType.class);

        Map<CommodityType, Double> consumption =
                new EnumMap<>(CommodityType.class);

        for (Planet planet : system.getPlanets()) {

            PlanetProductionProfile production =
                    planet.getProductionProfile();

            addToMap(
                    extraction,
                    production.getExtractionCapacity()
            );

            addToMap(
                    manufacturingPotential,
                    production.getManufacturingPotential()
            );

            addToMap(
                    consumption,
                    planet.getConsumptionProfile()
                            .getConsumption()
            );
        }

        StarSystemEconomicProfile profile =
                new StarSystemEconomicProfile(
                        extraction,
                        manufacturingPotential,
                        consumption
                );

        profile.allocateManufacturing(
                system,
                commodities
        );

        return profile;
    }

    /**
     * Adds every value from a source map into the target map.
     */
    private void addToMap(
            Map<CommodityType, Double> target,
            Map<CommodityType, Double> source) {

        source.forEach(
                (commodity, value) ->
                        target.merge(
                                commodity,
                                value,
                                Double::sum
                        )
        );
    }

    /**
     * Allocates available manufacturing potential across industries.
     *
     * Every industry begins at twenty percent utilization. Planetary
     * specialization slots can increase individual industries to forty
     * percent and then eighty percent utilization.
     *
     * @param system Star system containing the planets allocating
     *               manufacturing capacity.
     * @param commodities Commodities indexed by commodity type.
     */
    private void allocateManufacturing(
            StarSystem system,
            Map<CommodityType, Commodity> commodities) {

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);

        Map<Planet, Integer> specializationSlots =
                new HashMap<>();

        Map<Planet, Map<CommodityType, Integer>>
                planetSpecializations =
                new HashMap<>();

        Map<CommodityType, Integer> systemSpecializations =
                new EnumMap<>(CommodityType.class);

        for (Planet planet : system.getPlanets()) {

            specializationSlots.put(
                    planet,
                    calculateSpecializationSlots(planet)
            );

            planetSpecializations.put(
                    planet,
                    new EnumMap<>(CommodityType.class)
            );
        }

        for (CommodityType commodity : CommodityType.values()) {
            systemSpecializations.put(commodity, 0);
        }

        for (Planet planet : system.getPlanets()) {

            Map<CommodityType, Double> potential =
                    planet.getProductionProfile()
                            .getManufacturingPotential();

            potential.forEach(
                    (commodity, value) ->
                            manufacturing.merge(
                                    commodity,
                                    value * 0.20,
                                    Double::sum
                            )
            );
        }

        int maximumRounds =
                specializationSlots.values()
                        .stream()
                        .max(Integer::compareTo)
                        .orElse(0);

        for (int round = 0;
             round < maximumRounds;
             round++) {

            for (Planet planet : system.getPlanets()) {

                int slots =
                        specializationSlots.get(planet);

                if (round >= slots) {
                    continue;
                }

                CommodityType commodity =
                        chooseSpecialization(
                                planet,
                                systemSpecializations,
                                planetSpecializations.get(planet),
                                commodities,
                                manufacturing
                        );

                if (commodity == null) {
                    continue;
                }

                Map<CommodityType, Integer>
                        specializations =
                        planetSpecializations.get(planet);

                int previous =
                        specializations.getOrDefault(
                                commodity,
                                0
                        );

                double oldUtilization =
                        calculateUtilization(previous);

                double newUtilization =
                        calculateUtilization(previous + 1);

                double additionalUtilization =
                        newUtilization - oldUtilization;

                double potential =
                        planet.getProductionProfile()
                                .getManufacturingPotential()
                                .get(commodity);

                manufacturing.merge(
                        commodity,
                        potential * additionalUtilization,
                        Double::sum
                );

                specializations.merge(
                        commodity,
                        1,
                        Integer::sum
                );

                systemSpecializations.merge(
                        commodity,
                        1,
                        Integer::sum
                );
            }
        }

        this.manufacturing = manufacturing;
    }

    /**
     * Converts specialization count into manufacturing utilization.
     *
     * @param specializationCount Number of specializations assigned to
     *                            an industry.
     * @return Manufacturing utilization multiplier.
     */
    private double calculateUtilization(
            int specializationCount) {

        return switch (specializationCount) {
            case 0 -> 0.20;
            case 1 -> 0.40;
            default -> 0.80;
        };
    }

    /**
     * Determines how many manufacturing specialization slots a planet
     * receives from its development and infrastructure levels.
     *
     * @param planet Planet being evaluated.
     * @return Number of available specialization slots.
     */
    private int calculateSpecializationSlots(
            Planet planet) {

        int developmentSlots =
                switch (planet.getDevelopment()) {
                    case COLONIAL -> 0;
                    case AGRARIAN -> 1;
                    case DEVELOPING -> 2;
                    case INDUSTRIAL -> 3;
                    case ADVANCED -> 4;
                };

        int infrastructureSlots =
                switch (planet.getInfrastructure()) {
                    case POOR -> 0;
                    case MODEST -> 1;
                    case GOOD -> 2;
                    case EXCELLENT -> 3;
                };

        return 2
                + developmentSlots
                + infrastructureSlots;
    }

    /**
     * Selects the strongest available manufacturing specialization for
     * a planet.
     */
    public CommodityType chooseSpecialization(
            Planet planet,
            Map<CommodityType, Integer> systemSpecializations,
            Map<CommodityType, Integer> planetSpecializations,
            Map<CommodityType, Commodity> commodities,
            Map<CommodityType, Double> manufacturing) {

        Map<CommodityType, Double> potential =
                planet.getProductionProfile()
                        .getManufacturingPotential();

        CommodityType bestCommodity = null;

        double bestScore =
                Double.NEGATIVE_INFINITY;

        for (CommodityType commodity :
                potential.keySet()) {

            int existing =
                    planetSpecializations.getOrDefault(
                            commodity,
                            0
                    );

            if (existing >= 2) {
                continue;
            }

            double score =
                    calculateSpecializationScore(
                            planet,
                            commodity,
                            systemSpecializations,
                            commodities,
                            manufacturing
                    );

            if (score > bestScore) {
                bestScore = score;
                bestCommodity = commodity;
            }
        }

        return bestCommodity;
    }

    /**
     * Calculates the complete specialization score for a commodity.
     */
    private double calculateSpecializationScore(
            Planet planet,
            CommodityType commodity,
            Map<CommodityType, Integer> systemSpecializations,
            Map<CommodityType, Commodity> commodities,
            Map<CommodityType, Double> manufacturing) {

        double potentialScore =
                calculatePotentialScore(
                        planet,
                        commodity
                );

        double selfSufficiencyModifier =
                calculateSelfSufficiencyModifier(
                        commodity,
                        commodities
                );

        double distributionConsumptionModifier =
                calculateDistributionConsumptionModifier(
                        commodity,
                        manufacturing,
                        systemSpecializations
                );

        double supplyChainModifier =
                calculateSupplyChainModifier(
                        commodity,
                        manufacturing,
                        commodities
                );

        return potentialScore
                * selfSufficiencyModifier
                * distributionConsumptionModifier
                * supplyChainModifier;
    }

    /**
     * Calculates the base specialization score from manufacturing
     * potential relative to baseline manufacturing capacity.
     */
    private double calculatePotentialScore(
            Planet planet,
            CommodityType commodity) {

        double potential =
                planet.getProductionProfile()
                        .getManufacturingPotential()
                        .getOrDefault(
                                commodity,
                                0.0
                        );

        double baseline =
                planet.getProductionProfile()
                        .getBaseManufacturing()
                        .getOrDefault(
                                commodity,
                                1.0
                        );

        return potential / baseline;
    }

    /**
     * Measures how well the system can supply Tier 1 resources required
     * by the candidate industry's production recipe.
     */
    public double calculateSelfSufficiencyModifier(
            CommodityType commodity,
            Map<CommodityType, Commodity> commodities) {

        Commodity candidate =
                commodities.get(commodity);

        if (candidate == null ||
                candidate.getProductionRecipe() == null) {
            return 1.0;
        }

        ProductionRecipe recipe =
                candidate.getProductionRecipe();

        double totalRatio = 0.0;
        int inputCount = 0;

        for (Map.Entry<Commodity, Double> entry :
                recipe.getTier1GoodTotals().entrySet()) {

            Commodity input =
                    entry.getKey();

            double requiredPerOutput =
                    entry.getValue()
                            / recipe.getOutputAmount();

            double available =
                    extractionCapacity.getOrDefault(
                            input.getType(),
                            0.0
                    );

            double ratio =
                    available /
                            Math.max(
                                    requiredPerOutput,
                                    0.0001
                            );

            totalRatio +=
                    Math.min(
                            1.5,
                            Math.max(
                                    0.5,
                                    ratio
                            )
                    );

            inputCount++;
        }

        if (inputCount == 0) {
            return 1.0;
        }

        return totalRatio / inputCount;
    }

    /**
     * Favors commodities with unmet consumption and fewer existing
     * system-wide specializations.
     */
    public double calculateDistributionConsumptionModifier(
            CommodityType commodity,
            Map<CommodityType, Double> manufacturing,
            Map<CommodityType, Integer>
                    systemSpecializations) {

        double requiredConsumption =
                consumption.getOrDefault(
                        commodity,
                        0.0
                );

        double currentProduction =
                manufacturing.getOrDefault(
                        commodity,
                        0.0
                );

        double productionRatio =
                currentProduction /
                        Math.max(
                                requiredConsumption,
                                1.0
                        );

        double consumptionModifier =
                productionRatio < 1.0
                        ? 1.0
                                + (1.0 - productionRatio)
                                * 0.5
                        : 1.0;

        int specializations =
                systemSpecializations.getOrDefault(
                        commodity,
                        0
                );

        double distributionModifier =
                switch (specializations) {
                    case 0 -> 1.20;
                    case 1 -> 1.10;
                    case 2 -> 1.05;
                    default -> 1.00;
                };

        return consumptionModifier
                * distributionModifier;
    }

    /**
     * Favors industries whose production inputs are already being
     * manufactured within the system.
     */
    public double calculateSupplyChainModifier(
            CommodityType commodity,
            Map<CommodityType, Double> manufacturing,
            Map<CommodityType, Commodity> commodities) {

        Commodity candidate =
                commodities.get(commodity);

        if (candidate == null ||
                candidate.getProductionRecipe() == null) {
            return 1.0;
        }

        Map<Commodity, Double> inputs =
                candidate.getProductionRecipe()
                        .getInputs();

        if (inputs.isEmpty()) {
            return 1.0;
        }

        double modifier = 1.0;

        for (Commodity input :
                inputs.keySet()) {

            if (manufacturing.containsKey(
                    input.getType())) {

                modifier += 0.05;
            }
        }

        return Math.min(
                modifier,
                1.25
        );
    }
}