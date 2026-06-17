package com.stephenu.gts.market;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityRepository;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.Region;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;

import lombok.RequiredArgsConstructor;

@Component
@Order(3)
@RequiredArgsConstructor
public class MarketDataLoader implements CommandLineRunner {

    private final MarketRepository marketRepository;
    private final StarSystemRepository systemRepository;
    private final CommodityRepository commodityRepository;

    @Override
    public void run(String... args) {

        if (marketRepository.count() > 0) {
            return;
        }

        List<Market> markets = new ArrayList<>();

        Commodity food = commodityRepository.findByType(CommodityType.FOOD)
                .orElseThrow();

        Commodity fuel = commodityRepository.findByType(CommodityType.FUEL)
                .orElseThrow();

        Commodity ore = commodityRepository.findByType(CommodityType.ORE)
                .orElseThrow();

        Commodity medicine = commodityRepository.findByType(CommodityType.MEDICINE)
                .orElseThrow();

        Commodity technology = commodityRepository.findByType(CommodityType.TECHNOLOGY)
                .orElseThrow();

        Commodity luxuryGoods = commodityRepository.findByType(CommodityType.LUXURY_GOODS)
                .orElseThrow();

        for (StarSystem system : systemRepository.findAll()) {

            switch (system.getRegion()) {

                case CORE -> {
                    markets.add(createExportMarket(system, luxuryGoods));
                    markets.add(createImportMarket(system, ore));
                }

                case INNER_RIM -> {
                    markets.add(createExportMarket(system, food));
                    markets.add(createImportMarket(system, luxuryGoods));
                }

                case OUTER_RIM -> {
                    markets.add(createExportMarket(system, ore));
                    markets.add(createImportMarket(system, food));
                }
            }
        }

        marketRepository.saveAll(markets);
    }

    private Market createExportMarket(
            StarSystem system,
            Commodity commodity
    ) {
        return new Market(
                system,
                commodity,
                (int) (commodity.getBasePrice() * 0.8),
                1000,
                200
        );
    }

    private Market createImportMarket(
            StarSystem system,
            Commodity commodity
    ) {
        return new Market(
                system,
                commodity,
                (int) (commodity.getBasePrice() * 1.2),
                200,
                1000
        );
    }
}
