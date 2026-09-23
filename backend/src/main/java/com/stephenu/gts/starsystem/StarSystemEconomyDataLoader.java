package com.stephenu.gts.starsystem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityRepository;
import com.stephenu.gts.commodity.CommodityType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(4)
public class StarSystemEconomyDataLoader implements CommandLineRunner {

    private final StarSystemRepository systemRepository;
    private final CommodityRepository commodityRepository;

    @Override
    public void run(String... args) {

        Map<CommodityType, Commodity> commodities =
                commodityRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                Commodity::getType,
                                commodity -> commodity
                        ));

        List<StarSystem> systems =
                systemRepository.findAllWithPlanets();

        for (StarSystem system : systems) {

            StarSystemEconomicProfile profile =
                    new StarSystemEconomicProfile()
                            .generateProfile(
                                    system,
                                    commodities
                            );

            system.setEconomicProfile(profile);
        }

        systemRepository.saveAll(systems);
    }
}