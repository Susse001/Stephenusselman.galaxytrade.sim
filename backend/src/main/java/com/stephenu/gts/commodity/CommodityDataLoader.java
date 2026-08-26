package com.stephenu.gts.commodity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the database with the default set of commodities.
 *
 * The loader executes during application startup and only populates the
 * database when no commodities are present.
 */
@Component
@RequiredArgsConstructor
@Order(1)
public class CommodityDataLoader implements CommandLineRunner {

    private final CommodityRepository commodityRepository;

    /**
     * Populates the database with the default commodity definitions
     * and initializes their production recipes.
     *
     * @param args Command-line arguments supplied during application startup.
     */
    @Override
    public void run(String... args) {

        if (commodityRepository.count() > 0) {
            return;
        }

        List<Commodity> commodities = commodityRepository.saveAll(List.of(
        /*
         * ===== Tier 1 =====
         */
        new Commodity(null, CommodityType.FOOD, 20, 1),
        new Commodity(null, CommodityType.WATER, 25, 1),
        new Commodity(null, CommodityType.BIOMATERIALS, 55, 1),
        new Commodity(null, CommodityType.COMMON_METALS, 60, 1),
        new Commodity(null, CommodityType.RARE_METALS, 110, 1),
        new Commodity(null, CommodityType.INDUSTRIAL_MINERALS, 45, 1),
        new Commodity(null, CommodityType.HYDROCARBONS, 50, 1),
        new Commodity(null, CommodityType.INDUSTRIAL_CHEMICALS, 75, 1),
        new Commodity(null, CommodityType.RARE_ELEMENTS, 140, 1),

        /*
         * ===== Tier 2 =====
         */
        new Commodity(null, CommodityType.REFINED_METALS, 170, 2),
        new Commodity(null, CommodityType.PETROCHEMICALS, 160, 2),
        new Commodity(null, CommodityType.ADVANCED_MATERIALS, 240, 2),
        new Commodity(null, CommodityType.MANUFACTURED_PARTS, 220, 2),
        new Commodity(null, CommodityType.ELECTRONIC_COMPONENTS, 290, 2),
        new Commodity(null, CommodityType.PHARMACEUTICALS, 250, 2),
        new Commodity(null, CommodityType.FUEL, 150, 2),

        /*
         * ===== Tier 3 =====
         */
        new Commodity(null, CommodityType.ELECTRONICS, 550, 3),
        new Commodity(null, CommodityType.MEDICAL_SUPPLIES, 600, 3),
        new Commodity(null, CommodityType.INDUSTRIAL_MACHINERY, 700, 3),
        new Commodity(null, CommodityType.CAPITAL_GOODS, 800, 3),
        new Commodity(null, CommodityType.CONSUMER_GOODS, 450, 3),

        /*
         * ===== Unique =====
         */
        new Commodity(null, CommodityType.LUXURY_GOODS, 1000, 4)
        ));

        Map<CommodityType, Commodity> commodityMap =
                commodities.stream()
                        .collect(Collectors.toMap(
                                Commodity::getType,
                                commodity -> commodity
                        ));

        createProductionRecipes(commodityMap);
        calculateTier1GoodTotals(commodityMap);
    }

    /**
     * Creates the production recipes for all manufactured commodities.
     *
     * @param commodities Map of commodity types to their database entities.
     */
    private void createProductionRecipes(
            Map<CommodityType, Commodity> commodities) {

        commodities.get(CommodityType.REFINED_METALS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                4.0,
                                Map.of(
                                        commodities.get(CommodityType.COMMON_METALS), 6.0,
                                        commodities.get(CommodityType.RARE_METALS), 1.0,
                                        commodities.get(CommodityType.INDUSTRIAL_MINERALS), 2.0
                                )
                        )
                );

        commodities.get(CommodityType.PETROCHEMICALS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                4.0,
                                Map.of(
                                        commodities.get(CommodityType.HYDROCARBONS), 5.0,
                                        commodities.get(CommodityType.INDUSTRIAL_CHEMICALS), 2.0
                                )
                        )
                );

        commodities.get(CommodityType.ADVANCED_MATERIALS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                4.0,
                                Map.of(
                                        commodities.get(CommodityType.REFINED_METALS), 2.0,
                                        commodities.get(CommodityType.INDUSTRIAL_CHEMICALS), 1.0,
                                        commodities.get(CommodityType.BIOMATERIALS), 1.0,
                                        commodities.get(CommodityType.RARE_ELEMENTS), 1.0,
                                        commodities.get(CommodityType.RARE_METALS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.MANUFACTURED_PARTS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                5.0,
                                Map.of(
                                        commodities.get(CommodityType.REFINED_METALS), 2.0,
                                        commodities.get(CommodityType.INDUSTRIAL_MINERALS), 2.0,
                                        commodities.get(CommodityType.RARE_METALS), 1.0,
                                        commodities.get(CommodityType.PETROCHEMICALS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.ELECTRONIC_COMPONENTS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                4.0,
                                Map.of(
                                        commodities.get(CommodityType.ADVANCED_MATERIALS), 1.0,
                                        commodities.get(CommodityType.RARE_ELEMENTS), 2.0,
                                        commodities.get(CommodityType.REFINED_METALS), 1.0,
                                        commodities.get(CommodityType.INDUSTRIAL_CHEMICALS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.PHARMACEUTICALS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                3.0,
                                Map.of(
                                        commodities.get(CommodityType.BIOMATERIALS), 4.0,
                                        commodities.get(CommodityType.WATER), 2.0,
                                        commodities.get(CommodityType.INDUSTRIAL_CHEMICALS), 2.0,
                                        commodities.get(CommodityType.PETROCHEMICALS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.FUEL)
                .setProductionRecipe(
                        new ProductionRecipe(
                                5.0,
                                Map.of(
                                        commodities.get(CommodityType.HYDROCARBONS), 6.0,
                                        commodities.get(CommodityType.WATER), 4.0
                                )
                        )
                );

        commodities.get(CommodityType.CONSUMER_GOODS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                5.0,
                                Map.of(
                                        commodities.get(CommodityType.FOOD), 4.0,
                                        commodities.get(CommodityType.WATER), 2.0,
                                        commodities.get(CommodityType.PHARMACEUTICALS), 1.0,
                                        commodities.get(CommodityType.REFINED_METALS), 1.0,
                                        commodities.get(CommodityType.ADVANCED_MATERIALS), 1.0,
                                        commodities.get(CommodityType.PETROCHEMICALS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.MEDICAL_SUPPLIES)
                .setProductionRecipe(
                        new ProductionRecipe(
                                5.0,
                                Map.of(
                                        commodities.get(CommodityType.PHARMACEUTICALS), 3.0,
                                        commodities.get(CommodityType.ELECTRONIC_COMPONENTS), 1.0,
                                        commodities.get(CommodityType.ADVANCED_MATERIALS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.INDUSTRIAL_MACHINERY)
                .setProductionRecipe(
                        new ProductionRecipe(
                                3.0,
                                Map.of(
                                        commodities.get(CommodityType.MANUFACTURED_PARTS), 3.0,
                                        commodities.get(CommodityType.ADVANCED_MATERIALS), 1.0,
                                        commodities.get(CommodityType.ELECTRONIC_COMPONENTS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.ELECTRONICS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                3.0,
                                Map.of(
                                        commodities.get(CommodityType.ELECTRONIC_COMPONENTS), 3.0,
                                        commodities.get(CommodityType.MANUFACTURED_PARTS), 1.0,
                                        commodities.get(CommodityType.PETROCHEMICALS), 1.0
                                )
                        )
                );

        commodities.get(CommodityType.CAPITAL_GOODS)
                .setProductionRecipe(
                        new ProductionRecipe(
                                3.0,
                                Map.of(
                                        commodities.get(CommodityType.INDUSTRIAL_MACHINERY), 1.0,
                                        commodities.get(CommodityType.MANUFACTURED_PARTS), 2.0,
                                        commodities.get(CommodityType.FUEL), 1.0,
                                        commodities.get(CommodityType.ADVANCED_MATERIALS), 1.0
                                )
                        )
                );
    }

    /**
     * Calculates and stores the total Tier 1 requirements for each recipe.
     *
     * @param commodities Map of commodity types to their database entities.
     */
    private void calculateTier1GoodTotals(
            Map<CommodityType, Commodity> commodities) {

        for (Commodity commodity : commodities.values()) {

            ProductionRecipe recipe =
                    commodity.getProductionRecipe();

            if (recipe == null) {
                continue;
            }

            Map<Commodity, Double> totals =
                    calculateTier1Requirements(commodity);

            recipe.setTier1GoodTotals(totals);
        }
    }

    /**
     * Recursively resolves a commodity's production inputs into Tier 1 goods.
     *
     * @param commodity Commodity whose Tier 1 requirements are being calculated.
     * @return Map containing the total Tier 1 input required.
     */
    private Map<Commodity, Double> calculateTier1Requirements(
            Commodity commodity) {

        Map<Commodity, Double> totals =
                new HashMap<>();

        ProductionRecipe recipe =
                commodity.getProductionRecipe();

        if (recipe == null) {
            totals.put(commodity, 1.0);
            return totals;
        }

        for (Map.Entry<Commodity, Double> entry :
                recipe.getInputs().entrySet()) {

            Commodity input = entry.getKey();
            double quantity = entry.getValue();

            Map<Commodity, Double> inputTotals =
                    calculateTier1Requirements(input);

            inputTotals.forEach((tier1Commodity, amount) ->
                    totals.merge(
                            tier1Commodity,
                            amount * quantity,
                            Double::sum
                    )
            );
        }

        return totals;
    }
}