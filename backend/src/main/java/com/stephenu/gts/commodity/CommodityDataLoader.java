package com.stephenu.gts.commodity;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

//This seeder is nonfunctional pending the economy rework


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
     * Populates the database with the default commodity definitions.
     *
     * @param args Command-line arguments supplied during application startup.
     */
    @Override
    public void run(String... args) {

        if (commodityRepository.count() > 0) {
            return;
        }

        commodityRepository.saveAll(List.of(
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
        }
}
