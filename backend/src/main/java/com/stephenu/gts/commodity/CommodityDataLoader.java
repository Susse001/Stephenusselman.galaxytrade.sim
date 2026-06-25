package com.stephenu.gts.commodity;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(1)
public class CommodityDataLoader implements CommandLineRunner {

    private final CommodityRepository commodityRepository;

    @Override
    public void run(String... args) {

        if (commodityRepository.count() > 0) {
            return;
        }

        commodityRepository.saveAll(List.of(
        new Commodity(null, CommodityType.FOOD, 25),
        new Commodity(null, CommodityType.WATER, 15),
        new Commodity(null, CommodityType.ORE, 80),
        new Commodity(null, CommodityType.RARE_METALS, 160),
        new Commodity(null, CommodityType.ORGANICS, 60),

        new Commodity(null, CommodityType.CHEMICAL_COMPOUNDS, 180),
        new Commodity(null, CommodityType.REFINED_METALS, 260),
        new Commodity(null, CommodityType.ADVANCED_COMPONENTS, 375),

        new Commodity(null, CommodityType.MEDICINE, 500),
        new Commodity(null, CommodityType.INDUSTRIAL_PARTS, 720),
        new Commodity(null, CommodityType.ELECTRONICS, 945),

        new Commodity(null, CommodityType.LUXURY_GOODS, 1500)
        ));
    }
}
