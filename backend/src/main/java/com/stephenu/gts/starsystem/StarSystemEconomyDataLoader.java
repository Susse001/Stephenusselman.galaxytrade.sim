package com.stephenu.gts.starsystem;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityRepository;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.commodity.ProductionRecipe;
import com.stephenu.gts.planet.Planet;

@Component
public class StarSystemEconomyDataLoader {

    private CommodityRepository commodityRepository;

    public void generateEconomies(
            List<StarSystem> systems) {

        for (StarSystem system : systems) {

            StarSystemEconomicProfile profile =
                    new StarSystemEconomicProfile()
                            .generateProfile(system);

            allocateManufacturing(
                    system,
                    profile
            );

            system.setEconomicProfile(profile);
        }
    }

    /**
     * Allocates manufacturing potential across the system's planets.
     * Every industry begins at 20% utilization, with specializations
     * increasing individual industries to 40% and then 80%.
     */
    private void allocateManufacturing(
            StarSystem system,
            StarSystemEconomicProfile profile) {

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);

        Map<Planet, Integer> specializationSlots =
                new HashMap<>();

        Map<Planet, Map<CommodityType, Integer>> planetSpecializations =
                new HashMap<>();

        Map<CommodityType, Integer> systemSpecializations =
                new EnumMap<>(CommodityType.class);

         Map<CommodityType, Commodity> commodities =
            commodityRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(
                            Commodity::getType,
                            commodity -> commodity
                    ));

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

            potential.forEach((commodity, value) ->
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

        for (int round = 0; round < maximumRounds; round++) {

            for (Planet planet : system.getPlanets()) {

                int slots = specializationSlots.get(planet);

                if (round >= slots) {
                    continue;
                }

                CommodityType commodity =
                        chooseSpecialization(
                                planet,
                                profile,
                                systemSpecializations,
                                planetSpecializations.get(planet),
                                commodities

                        );

                if (commodity == null) {
                    continue;
                }

                Map<CommodityType, Integer> specializations =
                        planetSpecializations.get(planet);

                int previous =
                        specializations.getOrDefault(commodity, 0);

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

        profile.setManufacturing(manufacturing);
    }

    /**
     * Converts specialization count into manufacturing utilization.
     */
    private double calculateUtilization(int specializationCount) {

        return switch (specializationCount) {
            case 0 -> 0.20;
            case 1 -> 0.40;
            default -> 0.80;
        };
    }

    /**
     * Determines how many manufacturing specializations a planet receives
     * from its development and infrastructure levels.
     */
    private int calculateSpecializationSlots(Planet planet) {

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

        return 2 + developmentSlots + infrastructureSlots;
    }

    /**
     * Selects the strongest available manufacturing specialization for a planet.
     */
    private CommodityType chooseSpecialization(
            Planet planet,
            StarSystemEconomicProfile profile,
            Map<CommodityType, Integer> systemSpecializations,
            Map<CommodityType, Integer> planetSpecializations,
            Map<CommodityType, Commodity> commodities) {

        Map<CommodityType, Double> potential =
                planet.getProductionProfile()
                        .getManufacturingPotential();

        CommodityType bestCommodity = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (CommodityType commodity : potential.keySet()) {

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
                            profile,
                            systemSpecializations,
                            commodities
                    );

            if (score > bestScore) {
                bestScore = score;
                bestCommodity = commodity;
            }
        }

        return bestCommodity;
    }

    /**
     * Calculates the complete specialization score for a commodity
     * using potential, self-sufficiency, demand, distribution, and
     * existing supply-chain development.
     */
    private double calculateSpecializationScore(
            Planet planet,
            CommodityType commodity,
            StarSystemEconomicProfile profile,
            Map<CommodityType, Integer> systemSpecializations,
            Map<CommodityType, Commodity> commodities) {

        Map<CommodityType, Double> manufacturing = profile.getManufacturing();
        
        double potentialScore =
                calculatePotentialScore(
                        planet,
                        commodity
                );

        double selfSufficiencyModifier =
                calculateSelfSufficiencyModifier(
                        commodity,
                        profile,
                        commodities
                );

        double distributionConsumptionModifier =
                calculateDistributionConsumptionModifier(
                        commodity,
                        profile,
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
     * Calculates the base score from a planet's manufacturing potential
     * relative to its baseline manufacturing capacity.
     */
    private double calculatePotentialScore(
            Planet planet,
            CommodityType commodity) {

        double potential =
                planet.getProductionProfile()
                        .getManufacturingPotential()
                        .getOrDefault(commodity, 0.0);

        double baseline =
                planet.getProductionProfile()
                        .getBaseManufacturing()
                        .getOrDefault(commodity, 1.0);

        return potential / baseline;
    }

    /**
     * Measures how well the system can supply the Tier 1 resources
     * required to support the candidate industry's production.
     */
    private double calculateSelfSufficiencyModifier(
            CommodityType commodity,
            StarSystemEconomicProfile profile,
            Map<CommodityType, Commodity> commodities) {

        Commodity candidate = commodities.get(commodity);

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

            Commodity input = entry.getKey();

            double requiredPerOutput =
                    entry.getValue() / recipe.getOutputAmount();

            double available =
                    profile.getExtractionCapacity()
                            .getOrDefault(input.getType(), 0.0);

            double ratio =
                    available / Math.max(requiredPerOutput, 0.0001);

            totalRatio += Math.min(1.5, Math.max(0.5, ratio));
            inputCount++;
        }

        if (inputCount == 0) {
            return 1.0;
        }

        return totalRatio / inputCount;
    }

    /**
     * Favors commodities with unmet consumption and fewer existing
     * system-wide manufacturing specializations.
     */
    private double calculateDistributionConsumptionModifier(
            CommodityType commodity,
            StarSystemEconomicProfile profile,
            Map<CommodityType, Double> manufacturing,
            Map<CommodityType, Integer> systemSpecializations) {

        double consumption =
                profile.getConsumption()
                        .getOrDefault(commodity, 0.0);

        double currentProduction =
                manufacturing.getOrDefault(commodity, 0.0);

        double productionRatio =
                currentProduction /
                        Math.max(consumption, 1.0);

        double consumptionModifier =
                productionRatio < 1.0
                        ? 1.0 + (1.0 - productionRatio) * 0.5
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

        return consumptionModifier * distributionModifier;
    }

    /**
     * Favors industries whose inputs are already being manufactured
     * within the system, strengthening connected production chains.
     */
    private double calculateSupplyChainModifier(
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

        for (Commodity input : inputs.keySet()) {

            if (manufacturing.containsKey(input.getType())) {
                modifier += 0.05;
            }
        }

        return Math.min(modifier, 1.25);
    }
}