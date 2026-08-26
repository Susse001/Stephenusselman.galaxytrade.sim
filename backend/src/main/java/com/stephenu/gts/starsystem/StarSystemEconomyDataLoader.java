package com.stephenu.gts.starsystem;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.planet.Planet;

@Component
public class StarSystemEconomyDataLoader {

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
                                planetSpecializations.get(planet)
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
     * Selects the strongest available manufacturing specialization
     * for a planet while considering system-wide distribution.
     */
    private CommodityType chooseSpecialization(
            Planet planet,
            StarSystemEconomicProfile profile,
            Map<CommodityType, Integer> systemSpecializations,
            Map<CommodityType, Integer> planetSpecializations) {

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
                            commodity,
                            profile,
                            systemSpecializations
                    );

            if (score > bestScore) {
                bestScore = score;
                bestCommodity = commodity;
            }
        }

        return bestCommodity;
    }

    /**
     * Scores a commodity based on system demand and existing specialization.
     */
    private double calculateSpecializationScore(
            CommodityType commodity,
            StarSystemEconomicProfile profile,
            Map<CommodityType, Integer> systemSpecializations) {

        double consumption =
                profile.getConsumption()
                        .getOrDefault(commodity, 0.0);

        double manufacturing =
                profile.getManufacturingPotential()
                        .getOrDefault(commodity, 0.0);

        int specializationCount =
                systemSpecializations.getOrDefault(
                        commodity,
                        0
                );

        double demandWeight =
                consumption / Math.max(manufacturing, 1.0);

        double distributionWeight =
                calculateDistributionWeight(
                        specializationCount
                );

        return demandWeight + distributionWeight;
    }

    /**
     * Favors commodities that have received fewer system-wide
     * manufacturing specializations.
     */
    private double calculateDistributionWeight(
            int specializationCount) {

        return switch (specializationCount) {
            case 0 -> 3.0;
            case 1 -> 1.5;
            default -> 0.0;
        };
    }
}