package com.stephenu.gts.starsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.commodity.ProductionRecipe;
import com.stephenu.gts.planet.Planet;
import com.stephenu.gts.planet.PlanetProductionProfile;

public class StarSystemEconomicProfileTest {

    private StarSystemEconomicProfile profile;
    private Planet planet;

    @BeforeEach
    void setUp() {
        profile = new StarSystemEconomicProfile();

        planet = new Planet();
        planet.setProductionProfile(new PlanetProductionProfile());
    }

    @Test
    void generateProfileBuildsProfileFromSinglePlanet() {
        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        planet.getProductionProfile()
                .getExtractionCapacity()
                .put(
                        CommodityType.COMMON_METALS,
                        100.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        100.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        50.0
                );

        StarSystem system = new StarSystem();
        system.addPlanet(planet);

        StarSystemEconomicProfile result =
                profile.generateProfile(
                        system,
                        commodities
                );

        assertEquals(
                100.0,
                result.getExtractionCapacity()
                        .get(CommodityType.COMMON_METALS)
        );

        assertEquals(
                100.0,
                result.getManufacturingPotential()
                        .get(CommodityType.MANUFACTURED_PARTS)
        );

        assertEquals(
                50.0,
                result.getManufacturingPotential()
                        .get(CommodityType.ELECTRONIC_COMPONENTS)
        );

        assertFalse(result.getManufacturing().isEmpty());
    }

    @Test
    void generateProfileAggregatesMultiplePlanets() {
        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        Planet secondPlanet = new Planet();
        secondPlanet.setProductionProfile(
                new PlanetProductionProfile()
        );

        planet.getProductionProfile()
                .getExtractionCapacity()
                .put(
                        CommodityType.COMMON_METALS,
                        100.0
                );

        secondPlanet.getProductionProfile()
                .getExtractionCapacity()
                .put(
                        CommodityType.COMMON_METALS,
                        50.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        100.0
                );

        secondPlanet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        50.0
                );

        StarSystem system = new StarSystem();
        system.addPlanet(planet);
        system.addPlanet(secondPlanet);

        StarSystemEconomicProfile result =
                profile.generateProfile(
                        system,
                        commodities
                );

        assertEquals(
                150.0,
                result.getExtractionCapacity()
                        .get(CommodityType.COMMON_METALS)
        );

        assertEquals(
                150.0,
                result.getManufacturingPotential()
                        .get(CommodityType.MANUFACTURED_PARTS)
        );

        assertFalse(result.getManufacturing().isEmpty());
    }

    @Test
    void chooseSpecializationSelectsCommodityWithHigherPotential() {
        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(CommodityType.MANUFACTURED_PARTS, 100.0);

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(CommodityType.ELECTRONIC_COMPONENTS, 50.0);

        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        CommodityType result =
                profile.chooseSpecialization(
                        planet,
                        new EnumMap<>(CommodityType.class),
                        new EnumMap<>(CommodityType.class),
                        commodities,
                        new EnumMap<>(CommodityType.class)
                );

        assertEquals(
                CommodityType.MANUFACTURED_PARTS,
                result
        );
    }

    @Test
    void chooseSpecializationFavorsSelfSufficientCommodity() {
        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        100.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        100.0
                );

        profile.getExtractionCapacity().put(
                CommodityType.COMMON_METALS,
                100.0
        );

        profile.getExtractionCapacity().put(
                CommodityType.INDUSTRIAL_MINERALS,
                100.0
        );

        profile.getExtractionCapacity().put(
                CommodityType.RARE_ELEMENTS,
                0.0
        );

        profile.getExtractionCapacity().put(
                CommodityType.HYDROCARBONS,
                0.0
        );


        CommodityType result =
                profile.chooseSpecialization(
                        planet,
                        new EnumMap<>(CommodityType.class),
                        new EnumMap<>(CommodityType.class),
                        commodities,
                        new EnumMap<>(CommodityType.class)
                );

        assertEquals(
                CommodityType.MANUFACTURED_PARTS,
                result
        );
    }

    @Test
    void chooseSpecializationFavorsGreaterUnmetConsumption() {
        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        100.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        100.0
                );

        profile.getConsumption().put(
                CommodityType.MANUFACTURED_PARTS,
                100.0
        );

        profile.getConsumption().put(
                CommodityType.ELECTRONIC_COMPONENTS,
                20.0
        );

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);

        manufacturing.put(
                CommodityType.MANUFACTURED_PARTS,
                20.0
        );

        manufacturing.put(
                CommodityType.ELECTRONIC_COMPONENTS,
                20.0
        );

        CommodityType result =
                profile.chooseSpecialization(
                        planet,
                        new EnumMap<>(CommodityType.class),
                        new EnumMap<>(CommodityType.class),
                        commodities,
                        manufacturing
                );

        assertEquals(
                CommodityType.MANUFACTURED_PARTS,
                result
        );
    }

    @Test
    void chooseSpecializationFavorsLessSpecializedCommodity() {
        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        100.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        100.0
                );

        Map<CommodityType, Integer> systemSpecializations =
                new EnumMap<>(CommodityType.class);

        systemSpecializations.put(
                CommodityType.MANUFACTURED_PARTS,
                2
        );

        CommodityType result =
                profile.chooseSpecialization(
                        planet,
                        systemSpecializations,
                        new EnumMap<>(CommodityType.class),
                        commodities,
                        new EnumMap<>(CommodityType.class)
                );

        assertEquals(
                CommodityType.ELECTRONIC_COMPONENTS,
                result
        );
    }

    @Test
    void chooseSpecializationFavorsExistingSupplyChain() {
        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        100.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        100.0
                );

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);

        manufacturing.put(
                CommodityType.REFINED_METALS,
                50.0
        );

        CommodityType result =
                profile.chooseSpecialization(
                        planet,
                        new EnumMap<>(CommodityType.class),
                        new EnumMap<>(CommodityType.class),
                        commodities,
                        manufacturing
                );

        assertEquals(
                CommodityType.ELECTRONIC_COMPONENTS,
                result
        );
    }

    @Test
    void chooseSpecializationSkipsCommoditySpecializedTwice() {
        Map<CommodityType, Commodity> commodities =
                createTestCommodities();

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.MANUFACTURED_PARTS,
                        100.0
                );

        planet.getProductionProfile()
                .getManufacturingPotential()
                .put(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        50.0
                );

        Map<CommodityType, Integer> planetSpecializations =
                new EnumMap<>(CommodityType.class);

        planetSpecializations.put(
                CommodityType.MANUFACTURED_PARTS,
                2
        );

        CommodityType result =
                profile.chooseSpecialization(
                        planet,
                        new EnumMap<>(CommodityType.class),
                        planetSpecializations,
                        commodities,
                        new EnumMap<>(CommodityType.class)
                );

        assertEquals(
                CommodityType.ELECTRONIC_COMPONENTS,
                result
        );
    }

    @Test
    void calculateSelfSufficiencyModifierReturnsOneWithoutRecipe() {
        Map<CommodityType, Commodity> commodities =
                new EnumMap<>(CommodityType.class);

        double result =
                profile.calculateSelfSufficiencyModifier(
                        CommodityType.MANUFACTURED_PARTS,
                        commodities
                );

        assertEquals(1.0, result);
    }

    @Test
    void calculateSelfSufficiencyModifierReturnsOneWithoutInputs() {
        Commodity candidate = new Commodity();
        candidate.setType(CommodityType.MANUFACTURED_PARTS);

        ProductionRecipe recipe = new ProductionRecipe();
        recipe.setTier1GoodTotals(new HashMap<>());

        candidate.setProductionRecipe(recipe);

        Map<CommodityType, Commodity> commodities =
                new EnumMap<>(CommodityType.class);
        commodities.put(
                CommodityType.MANUFACTURED_PARTS,
                candidate
        );

        double result =
                profile.calculateSelfSufficiencyModifier(
                        CommodityType.MANUFACTURED_PARTS,
                        commodities
                );

        assertEquals(1.0, result);
    }

    @Test
    void calculateSelfSufficiencyModifierUsesAvailableResources() {
        Commodity input = new Commodity();
        input.setType(CommodityType.COMMON_METALS);

        Commodity candidate = new Commodity();
        candidate.setType(CommodityType.MANUFACTURED_PARTS);

        ProductionRecipe recipe = new ProductionRecipe();

        Map<Commodity, Double> tier1Totals =
                new HashMap<>();
        tier1Totals.put(input, 10.0);

        recipe.setTier1GoodTotals(tier1Totals);
        recipe.setOutputAmount(10.0);

        candidate.setProductionRecipe(recipe);

        Map<CommodityType, Commodity> commodities =
                new EnumMap<>(CommodityType.class);
        commodities.put(
                CommodityType.MANUFACTURED_PARTS,
                candidate
        );

        profile.getExtractionCapacity().put(
                CommodityType.COMMON_METALS,
                15.0
        );

        double result =
                profile.calculateSelfSufficiencyModifier(
                        CommodityType.MANUFACTURED_PARTS,
                        commodities
                );

        assertEquals(1.5, result);
    }

    @Test
    void calculateSelfSufficiencyModifierClampsLowResourceAvailability() {
        Commodity input = new Commodity();
        input.setType(CommodityType.COMMON_METALS);

        Commodity candidate = new Commodity();
        candidate.setType(CommodityType.MANUFACTURED_PARTS);

        ProductionRecipe recipe = new ProductionRecipe();

        Map<Commodity, Double> tier1Totals =
                new HashMap<>();
        tier1Totals.put(input, 10.0);

        recipe.setTier1GoodTotals(tier1Totals);
        recipe.setOutputAmount(10.0);

        candidate.setProductionRecipe(recipe);

        Map<CommodityType, Commodity> commodities =
                new EnumMap<>(CommodityType.class);
        commodities.put(
                CommodityType.MANUFACTURED_PARTS,
                candidate
        );

        profile.getExtractionCapacity().put(
                CommodityType.COMMON_METALS,
                0.1
        );

        double result =
                profile.calculateSelfSufficiencyModifier(
                        CommodityType.MANUFACTURED_PARTS,
                        commodities
                );

        assertEquals(0.5, result);
    }

    @Test
    void calculateDistributionConsumptionModifierFavorsUnmetConsumption() {
        profile.getConsumption().put(
                CommodityType.ELECTRONIC_COMPONENTS,
                100.0
        );

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);

        manufacturing.put(
                CommodityType.ELECTRONIC_COMPONENTS,
                50.0
        );

        Map<CommodityType, Integer> specializations =
                new EnumMap<>(CommodityType.class);

        double result =
                profile.calculateDistributionConsumptionModifier(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        manufacturing,
                        specializations
                );

        // Production ratio = 0.5
        // Consumption modifier = 1 + (1 - 0.5) * 0.5 = 1.25
        // No specialization = 1.20
        // Final = 1.50
        assertEquals(1.50, result);
    }

    @Test
    void calculateDistributionConsumptionModifierDoesNotRewardOversupply() {
        profile.getConsumption().put(
                CommodityType.ELECTRONIC_COMPONENTS,
                100.0
        );

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);

        manufacturing.put(
                CommodityType.ELECTRONIC_COMPONENTS,
                150.0
        );

        Map<CommodityType, Integer> specializations =
                new EnumMap<>(CommodityType.class);

        double result =
                profile.calculateDistributionConsumptionModifier(
                        CommodityType.ELECTRONIC_COMPONENTS,
                        manufacturing,
                        specializations
                );

        assertEquals(1.20, result);
    }

    @Test
    void calculateSupplyChainModifierReturnsOneWithoutRecipe() {
        Map<CommodityType, Commodity> commodities =
                new EnumMap<>(CommodityType.class);

        double result =
                profile.calculateSupplyChainModifier(
                        CommodityType.MANUFACTURED_PARTS,
                        new EnumMap<>(CommodityType.class),
                        commodities
                );

        assertEquals(1.0, result);
    }

    @Test
    void calculateSupplyChainModifierRewardsManufacturedInput() {
        Commodity input = new Commodity();
        input.setType(CommodityType.REFINED_METALS);

        Commodity candidate = new Commodity();
        candidate.setType(CommodityType.MANUFACTURED_PARTS);

        ProductionRecipe recipe = new ProductionRecipe();

        Map<Commodity, Double> inputs =
                new HashMap<>();
        inputs.put(input, 2.0);

        recipe.setInputs(inputs);
        candidate.setProductionRecipe(recipe);

        Map<CommodityType, Commodity> commodities =
                new EnumMap<>(CommodityType.class);
        commodities.put(
                CommodityType.MANUFACTURED_PARTS,
                candidate
        );

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);
        manufacturing.put(
                CommodityType.REFINED_METALS,
                20.0
        );

        double result =
                profile.calculateSupplyChainModifier(
                        CommodityType.MANUFACTURED_PARTS,
                        manufacturing,
                        commodities
                );

        assertEquals(1.05, result);
    }

    @Test
    void calculateSupplyChainModifierRewardsMultipleManufacturedInputs() {
        Commodity metals = new Commodity();
        metals.setType(CommodityType.REFINED_METALS);

        Commodity materials = new Commodity();
        materials.setType(CommodityType.ADVANCED_MATERIALS);

        Commodity candidate = new Commodity();
        candidate.setType(CommodityType.MANUFACTURED_PARTS);

        ProductionRecipe recipe = new ProductionRecipe();

        Map<Commodity, Double> inputs =
                new HashMap<>();
        inputs.put(metals, 2.0);
        inputs.put(materials, 3.0);

        recipe.setInputs(inputs);
        candidate.setProductionRecipe(recipe);

        Map<CommodityType, Commodity> commodities =
                new EnumMap<>(CommodityType.class);
        commodities.put(
                CommodityType.MANUFACTURED_PARTS,
                candidate
        );

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(CommodityType.class);
        manufacturing.put(
                CommodityType.REFINED_METALS,
                20.0
        );
        manufacturing.put(
                CommodityType.ADVANCED_MATERIALS,
                20.0
        );

        double result =
                profile.calculateSupplyChainModifier(
                        CommodityType.MANUFACTURED_PARTS,
                        manufacturing,
                        commodities
                );

        assertEquals(1.10, result);
    }

    private Map<CommodityType, Commodity> createTestCommodities() {

        Commodity commonMetals =
                new Commodity(
                        null,
                        CommodityType.COMMON_METALS,
                        60,
                        1
                );

        Commodity industrialMinerals =
                new Commodity(
                        null,
                        CommodityType.INDUSTRIAL_MINERALS,
                        45,
                        1
                );

        Commodity rareElements =
                new Commodity(
                        null,
                        CommodityType.RARE_ELEMENTS,
                        140,
                        1
                );

        Commodity hydrocarbons =
                new Commodity(
                        null,
                        CommodityType.HYDROCARBONS,
                        50,
                        1
                );

        Commodity refinedMetals =
                new Commodity(
                        null,
                        CommodityType.REFINED_METALS,
                        170,
                        2
                );

        Commodity manufacturedParts =
                new Commodity(
                        null,
                        CommodityType.MANUFACTURED_PARTS,
                        220,
                        2
                );

        Commodity electronicComponents =
                new Commodity(
                        null,
                        CommodityType.ELECTRONIC_COMPONENTS,
                        290,
                        2
                );

        commonMetals.setProductionRecipe(null);
        industrialMinerals.setProductionRecipe(null);
        rareElements.setProductionRecipe(null);
        hydrocarbons.setProductionRecipe(null);

        refinedMetals.setProductionRecipe(
                new ProductionRecipe(
                        2.0,
                        Map.of(
                                commonMetals, 2.0
                        )
                )
        );

        manufacturedParts.setProductionRecipe(
                new ProductionRecipe(
                        4.0,
                        Map.of(
                                commonMetals, 2.0,
                                industrialMinerals, 2.0
                        )
                )
        );

        electronicComponents.setProductionRecipe(
                new ProductionRecipe(
                        3.0,
                        Map.of(
                                refinedMetals, 1.0,
                                rareElements, 1.0,
                                hydrocarbons, 1.0
                        )
                )
        );

        for (Commodity commodity : List.of(
                refinedMetals,
                manufacturedParts,
                electronicComponents
        )) {
            commodity.calculateTier1GoodTotals();
        }

        return Map.of(
                CommodityType.COMMON_METALS, commonMetals,
                CommodityType.INDUSTRIAL_MINERALS, industrialMinerals,
                CommodityType.RARE_ELEMENTS, rareElements,
                CommodityType.HYDROCARBONS, hydrocarbons,
                CommodityType.REFINED_METALS, refinedMetals,
                CommodityType.MANUFACTURED_PARTS, manufacturedParts,
                CommodityType.ELECTRONIC_COMPONENTS, electronicComponents
        );
    }
}
