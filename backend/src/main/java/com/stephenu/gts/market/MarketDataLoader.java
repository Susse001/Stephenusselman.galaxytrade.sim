package com.stephenu.gts.market;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityRepository;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MarketDataLoader implements CommandLineRunner {

    private final MarketRepository marketRepository;
    private final StarSystemRepository starSystemRepository;
    private final CommodityRepository commodityRepository;

    public MarketDataLoader(
            MarketRepository marketRepository,
            StarSystemRepository starSystemRepository,
            CommodityRepository commodityRepository
    ) {
        this.marketRepository = marketRepository;
        this.starSystemRepository = starSystemRepository;
        this.commodityRepository = commodityRepository;
    }

    @Override
    public void run(String... args) {

        if (marketRepository.count() > 0) {
            return;
        }

        StarSystem titanGate = starSystemRepository.findByName("Titan Gate")
                .orElseThrow();

        StarSystem novaPrime = starSystemRepository.findByName("Nova Prime")
                .orElseThrow();

        StarSystem heliosReach = starSystemRepository.findByName("Helios Reach")
                .orElseThrow();

        Commodity food = commodityRepository.findByType(CommodityType.FOOD)
                .orElseThrow();

        Commodity fuel = commodityRepository.findByType(CommodityType.FUEL)
                .orElseThrow();

        Commodity ore = commodityRepository.findByType(CommodityType.ORE)
                .orElseThrow();

        marketRepository.save(
                new Market(titanGate, food, 100, 500, 300)
        );

        marketRepository.save(
                new Market(titanGate, fuel, 120, 200, 350)
        );

        marketRepository.save(
                new Market(novaPrime, food, 130, 100, 450)
        );

        marketRepository.save(
                new Market(heliosReach, ore, 90, 700, 150)
        );
    }
}