package com.stephenu.gts.planet;

import java.util.EnumMap;
import java.util.Map;

import com.stephenu.gts.commodity.CommodityType;

public class PlanetConsumptionProfile {

    public Map<CommodityType, Double> generateConsumption(
            Planet planet) {

        Map<CommodityType, Double> consumption =
                createBaselineConsumption();

        applyPopulationLevel(
                consumption,
                planet.getPopulation());

        applyDevelopmentLevel(
                consumption,
                planet.getDevelopment());

        return consumption;
    }

    private Map<CommodityType, Double> createBaselineConsumption() {

        Map<CommodityType, Double> consumption =
                new EnumMap<>(CommodityType.class);

        consumption.put(CommodityType.FOOD, 120.0);
        consumption.put(CommodityType.WATER, 100.0);

        consumption.put(CommodityType.BIOMATERIALS, 12.0);
        consumption.put(CommodityType.COMMON_METALS, 15.0);
        consumption.put(CommodityType.RARE_METALS, 7.0);
        consumption.put(CommodityType.INDUSTRIAL_MINERALS, 10.0);
        consumption.put(CommodityType.HYDROCARBONS, 9.0);
        consumption.put(CommodityType.INDUSTRIAL_CHEMICALS, 7.0);
        consumption.put(CommodityType.RARE_ELEMENTS, 3.0);

        consumption.put(CommodityType.REFINED_METALS, 12.0);
        consumption.put(CommodityType.PETROCHEMICALS, 10.0);
        consumption.put(CommodityType.ADVANCED_MATERIALS, 5.0);
        consumption.put(CommodityType.MANUFACTURED_PARTS, 8.0);
        consumption.put(CommodityType.ELECTRONIC_COMPONENTS, 5.0);
        consumption.put(CommodityType.PHARMACEUTICALS, 10.0);
        consumption.put(CommodityType.FUEL, 15.0);

        consumption.put(CommodityType.CONSUMER_GOODS, 40.0);
        consumption.put(CommodityType.MEDICAL_SUPPLIES, 14.0);
        consumption.put(CommodityType.INDUSTRIAL_MACHINERY, 10.0);
        consumption.put(CommodityType.ELECTRONICS, 15.0);
        consumption.put(CommodityType.CAPITAL_GOODS, 5.0);
        consumption.put(CommodityType.LUXURY_GOODS, 3.0);

        return consumption;
    }

    private Map<CommodityType, Double> applyPopulationLevel(
        Map<CommodityType, Double> consumption,
        PopulationLevel population) {

        double multiplier = switch (population) {

            case TENS_OF_MILLIONS -> 0.10;
            case HUNDREDS_OF_MILLIONS -> 0.30;
            case BILLIONS -> 1.00;
            case TENS_OF_BILLIONS -> 3.00;
        };

        consumption.replaceAll((k, v) -> v * multiplier);

        return consumption;
    }

    private Map<CommodityType, Double> applyDevelopmentLevel(
        Map<CommodityType, Double> consumption,
        DevelopmentLevel development) {

        switch (development) {

            case COLONIAL -> {

                increase(consumption, CommodityType.FUEL, 30.0);
                increase(consumption, CommodityType.INDUSTRIAL_MACHINERY,20.0);

                decrease(consumption, CommodityType.FOOD, 20.0);
                decrease(consumption, CommodityType.WATER, 20.0);
                decrease(consumption, CommodityType.ELECTRONICS, 60.0);
                decrease(consumption, CommodityType.MEDICAL_SUPPLIES, 60.0);
                decrease(consumption, CommodityType.CAPITAL_GOODS, 60.0);
                decrease(consumption, CommodityType.LUXURY_GOODS, 80.0);
                decrease(consumption, CommodityType.CONSUMER_GOODS, 40.0);
            }

            case AGRARIAN -> {

                increase(consumption, CommodityType.FOOD, 10.0);
                increase(consumption, CommodityType.INDUSTRIAL_MINERALS,20.0);

                decrease(consumption, CommodityType.ELECTRONICS, 40.0);
                decrease(consumption, CommodityType.CAPITAL_GOODS, 60.0);
                decrease(consumption, CommodityType.INDUSTRIAL_MACHINERY, 20.0);
                decrease(consumption, CommodityType.FUEL, 20.0);
                decrease(consumption, CommodityType.CONSUMER_GOODS, 20.0);
                decrease(consumption, CommodityType.LUXURY_GOODS, 40.0);
            }

            case DEVELOPING -> {
                // Baseline
            }

            case INDUSTRIAL -> {


                increase(consumption, CommodityType.INDUSTRIAL_CHEMICALS, 20.0);
                increase(consumption, CommodityType.FUEL, 20.0);
                increase(consumption, CommodityType.INDUSTRIAL_MACHINERY, 40.0);
                increase(consumption, CommodityType.MANUFACTURED_PARTS, 20.0);
                increase(consumption, CommodityType.ELECTRONIC_COMPONENTS, 20.0);
                increase(consumption, CommodityType.WATER, 20.0);
                increase(consumption, CommodityType.CONSUMER_GOODS, 10.0);
                increase(consumption, CommodityType.LUXURY_GOODS, 20.0);
            }

            case ADVANCED -> {

                increase(consumption, CommodityType.WATER, 20.0);
                increase(consumption, CommodityType.FOOD, 20.0);
                increase(consumption, CommodityType.ELECTRONICS, 30.0);
                increase(consumption, CommodityType.MEDICAL_SUPPLIES, 20.0);
                increase(consumption, CommodityType.CAPITAL_GOODS, 30.0);
                increase(consumption, CommodityType.LUXURY_GOODS, 50.0);
                increase(consumption, CommodityType.PHARMACEUTICALS, 40.0);
                increase(consumption, CommodityType.CONSUMER_GOODS, 30.0);
            }
        }

        return consumption;
    }

    /**
     * Increases the specified commodity by the given percentage.
     *
     * @param consumption Consumption map to modify.
     * @param commodity Commodity to modify.
     * @param percent Percentage increase (e.g. 20 = +20%).
     */
    private void increase(
            Map<CommodityType, Double> consumption,
            CommodityType commodity,
            double percent) {

        double current = consumption.getOrDefault(commodity, 0.0);

        consumption.put(
            commodity,
            current * (1.0 + percent / 100.0)
        );
    }

    /**
     * Decreases the specified commodity by the given percentage.
     *
     * @param consumption Consumption map to modify.
     * @param commodity Commodity to modify.
     * @param percent Percentage decrease (e.g. 20 = -20%).
     */
    private void decrease(
            Map<CommodityType, Double> consumption,
            CommodityType commodity,
            double percent) {

        double current = consumption.getOrDefault(commodity, 0.0);

        consumption.put(
            commodity,
            current * (1.0 - percent / 100.0)
        );
    }
}
