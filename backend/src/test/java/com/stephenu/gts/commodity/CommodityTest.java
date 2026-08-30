package com.stephenu.gts.commodity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class CommodityTest {

    @Test
    void tierOneCommodityShouldHaveNoProductionRecipe() {

        Commodity food =
                new Commodity(
                        null,
                        CommodityType.FOOD,
                        20,
                        1
                );

        assertNull(food.getProductionRecipe());
    }

    @Test
    void calculateTier1GoodTotalsShouldDoNothingForTierOneCommodity() {

        Commodity food =
                new Commodity(
                        null,
                        CommodityType.FOOD,
                        20,
                        1
                );

        food.calculateTier1GoodTotals();

        assertNull(food.getProductionRecipe());
    }

    @Test
    void calculateTier1GoodTotalsShouldResolveDirectTierOneInputs() {

        Commodity commonMetals =
                new Commodity(
                        null,
                        CommodityType.COMMON_METALS,
                        60,
                        1
                );

        Commodity rareMetals =
                new Commodity(
                        null,
                        CommodityType.RARE_METALS,
                        110,
                        1
                );

        Commodity refinedMetals =
                new Commodity(
                        null,
                        CommodityType.REFINED_METALS,
                        170,
                        2
                );

        refinedMetals.setProductionRecipe(
                new ProductionRecipe(
                        4.0,
                        Map.of(
                                commonMetals, 6.0,
                                rareMetals, 1.0
                        )
                )
        );

        refinedMetals.calculateTier1GoodTotals();

        Map<Commodity, Double> totals =
                refinedMetals.getProductionRecipe()
                        .getTier1GoodTotals();

        assertEquals(2, totals.size());
        assertEquals(6.0, totals.get(commonMetals));
        assertEquals(1.0, totals.get(rareMetals));
    }

    @Test
    void calculateTier1GoodTotalsShouldResolveNestedProductionChain() {

        Commodity commonMetals =
                new Commodity(
                        null,
                        CommodityType.COMMON_METALS,
                        60,
                        1
                );

        Commodity rareMetals =
                new Commodity(
                        null,
                        CommodityType.RARE_METALS,
                        110,
                        1
                );

        Commodity industrialMinerals =
                new Commodity(
                        null,
                        CommodityType.INDUSTRIAL_MINERALS,
                        45,
                        1
                );

        Commodity refinedMetals =
                new Commodity(
                        null,
                        CommodityType.REFINED_METALS,
                        170,
                        2
                );

        refinedMetals.setProductionRecipe(
                new ProductionRecipe(
                        4.0,
                        Map.of(
                                commonMetals, 6.0,
                                rareMetals, 1.0,
                                industrialMinerals, 2.0
                        )
                )
        );

        Commodity advancedMaterials =
                new Commodity(
                        null,
                        CommodityType.ADVANCED_MATERIALS,
                        240,
                        2
                );

        advancedMaterials.setProductionRecipe(
                new ProductionRecipe(
                        4.0,
                        Map.of(
                                refinedMetals, 2.0,
                                rareMetals, 1.0
                        )
                )
        );

        advancedMaterials.calculateTier1GoodTotals();

        Map<Commodity, Double> totals =
                advancedMaterials.getProductionRecipe()
                        .getTier1GoodTotals();

        assertEquals(3, totals.size());

        assertEquals(12.0, totals.get(commonMetals));
        assertEquals(3.0, totals.get(rareMetals));
        assertEquals(4.0, totals.get(industrialMinerals));
    }

    @Test
    void calculateTier1GoodTotalsShouldCombineDuplicateTierOneInputs() {

        Commodity commonMetals =
                new Commodity(
                        null,
                        CommodityType.COMMON_METALS,
                        60,
                        1
                );

        Commodity refinedMetals =
                new Commodity(
                        null,
                        CommodityType.REFINED_METALS,
                        170,
                        2
                );

        refinedMetals.setProductionRecipe(
                new ProductionRecipe(
                        4.0,
                        Map.of(
                                commonMetals, 6.0
                        )
                )
        );

        Commodity advancedMaterials =
                new Commodity(
                        null,
                        CommodityType.ADVANCED_MATERIALS,
                        240,
                        2
                );

        advancedMaterials.setProductionRecipe(
                new ProductionRecipe(
                        4.0,
                        Map.of(
                                refinedMetals, 2.0,
                                commonMetals, 3.0
                        )
                )
        );

        advancedMaterials.calculateTier1GoodTotals();

        Map<Commodity, Double> totals =
                advancedMaterials.getProductionRecipe()
                        .getTier1GoodTotals();

        assertEquals(1, totals.size());
        assertEquals(15.0, totals.get(commonMetals));
    }

    @Test
    void calculateTier1GoodTotalsShouldNotIncludeIntermediateGoods() {

        Commodity commonMetals =
                new Commodity(
                        null,
                        CommodityType.COMMON_METALS,
                        60,
                        1
                );

        Commodity refinedMetals =
                new Commodity(
                        null,
                        CommodityType.REFINED_METALS,
                        170,
                        2
                );

        refinedMetals.setProductionRecipe(
                new ProductionRecipe(
                        4.0,
                        Map.of(
                                commonMetals, 6.0
                        )
                )
        );

        refinedMetals.calculateTier1GoodTotals();

        Map<Commodity, Double> totals =
                refinedMetals.getProductionRecipe()
                        .getTier1GoodTotals();

        assertTrue(totals.containsKey(commonMetals));
        assertFalse(totals.containsKey(refinedMetals));
    }

}