package com.stephenu.gts.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    private static final List<CommodityType> TIER_1 = List.of(
        CommodityType.FOOD,
        CommodityType.WATER,
        CommodityType.ORE,
        CommodityType.RARE_METALS,
        CommodityType.ORGANICS
    );

    private static final List<CommodityType> TIER_2 = List.of(
            CommodityType.CHEMICAL_COMPOUNDS,
            CommodityType.REFINED_METALS,
            CommodityType.ADVANCED_COMPONENTS
    );

    private static final List<CommodityType> TIER_3 = List.of(
            CommodityType.MEDICINE,
            CommodityType.INDUSTRIAL_PARTS,
            CommodityType.ELECTRONICS
    );

    private final Random random = new Random();

    @Override
    public void run(String... args) {

        if (marketRepository.count() > 0) {
            return;
        }

        List<Market> markets = new ArrayList<>();

        List<Commodity> allCommodities =
                commodityRepository.findAll();


        for (StarSystem system : systemRepository.findAll()) {


             List<Commodity> exportPool =
            buildExportPool(
                    system.getRegion()
            );

            Set<Commodity> exports =
                    drawCommodities(
                            exportPool,
                            3
                    );

            Set<Commodity> imports =
                    generateImports(
                            exports,
                            allCommodities
            );

            exports.forEach(c ->
                    markets.add(
                            createExportMarket(
                                    system,
                                    c
                            )
                    )
            );

            imports.forEach(c ->
                    markets.add(
                            createImportMarket(
                                    system,
                                    c
                            )
                    )
            );
        }

        marketRepository.saveAll(markets);
    }

    private List<Commodity> buildExportPool(
        Region region) {

        List<Commodity> pool = new ArrayList<>();

        switch (region) {

            case CORE -> {
                addCopies(pool, TIER_1, 2);
                addCopies(pool, TIER_2, 4);
                addCopies(pool, TIER_3, 6);

                addCommodity(
                        pool,
                        CommodityType.LUXURY_GOODS,
                        2
                );
            }

            case INNER_RIM -> {
                addCopies(pool, TIER_1, 4);
                addCopies(pool, TIER_2, 4);
                addCopies(pool, TIER_3, 2);

                addCommodity(
                        pool,
                        CommodityType.LUXURY_GOODS,
                        1
                );
            }

            case OUTER_RIM -> {
                addCopies(pool, TIER_1, 6);
                addCopies(pool, TIER_2, 3);
                addCopies(pool, TIER_3, 1);

                addCommodity(
                        pool,
                        CommodityType.LUXURY_GOODS,
                        1
                );
            }
        }

        Collections.shuffle(pool);

        return pool;
    }

    private Set<Commodity> generateImports(
        Set<Commodity> exports,
        List<Commodity> allCommodities) {

        List<Commodity> candidates =
            new ArrayList<>(
                    allCommodities.stream()
                            .filter(c ->
                                    !exports.contains(c))
                            .toList()
            );

        Collections.shuffle(candidates);

        return new HashSet<>(
                candidates.subList(0, 3)
        );
    }

    private Market createExportMarket(
        StarSystem system,
        Commodity commodity) {

        double modifier =
                0.70 + random.nextDouble() * 0.20;

        return new Market(
                system,
                commodity,
                (int)(commodity.getBasePrice() * modifier),
                1000,
                200
        );
    }

    private Market createImportMarket(
        StarSystem system,
        Commodity commodity) {

        double modifier =
                1.10 + random.nextDouble() * 0.30;

        return new Market(
                system,
                commodity,
                (int)(commodity.getBasePrice() * modifier),
                200,
                1000
        );
    }

    private void addCopies(
        List<Commodity> pool,
        List<CommodityType> types,
        int copies) {

        for (CommodityType type : types) {

            Commodity commodity =
                    commodityRepository
                            .findByType(type)
                            .orElseThrow();

            for (int i = 0; i < copies; i++) {
                pool.add(commodity);
            }
        }
    }

    private void addCommodity(
        List<Commodity> pool,
        CommodityType type,
        int copies) {

        Commodity commodity =
                commodityRepository
                        .findByType(type)
                        .orElseThrow();

        for (int i = 0; i < copies; i++) {
            pool.add(commodity);
        }
    }

    private Set<Commodity> drawCommodities(
        List<Commodity> pool,
        int count) {

        Set<Commodity> result =
                new HashSet<>();

        int index = 0;

        while (result.size() < count) {

            if (index >= pool.size()) {
                Collections.shuffle(pool);
                index = 0;
            }

            result.add(pool.get(index++));
        }

        return result;
    }
}
