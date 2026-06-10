package com.stephenu.gts.commodity;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommodityDataLoader {

    private final CommodityRepository commodityRepository;

    @PostConstruct
    public void loadData() {

        if (commodityRepository.count() > 0) {
            return;
        }

        commodityRepository.saveAll(List.of(
            new Commodity(null, CommodityType.FOOD, 100),
            new Commodity(null, CommodityType.FUEL, 150),
            new Commodity(null, CommodityType.ORE, 80),
            new Commodity(null, CommodityType.MEDICINE, 250),
            new Commodity(null, CommodityType.TECHNOLOGY, 400),
            new Commodity(null, CommodityType.LUXURY_GOODS, 600)
        ));
    }
}
