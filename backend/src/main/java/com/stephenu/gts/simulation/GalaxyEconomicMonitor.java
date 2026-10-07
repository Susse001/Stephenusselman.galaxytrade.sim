package com.stephenu.gts.simulation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;

import com.stephenu.gts.simulation.dto.CommodityEconomicSummary;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.market.Market;
import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;
import com.stephenu.gts.starsystem.StarSystemEconomicProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GalaxyEconomicMonitor {

    private final StarSystemRepository starSystemRepository;
    private final MarketRepository marketRepository;

    public List<CommodityEconomicSummary> getEconomicSummary() {

        Map<CommodityType, Double> production =
                new EnumMap<>(CommodityType.class);

        Map<CommodityType, Double> consumption =
                new EnumMap<>(CommodityType.class);

        Map<CommodityType, Double> inventory =
                new EnumMap<>(CommodityType.class);


        for (StarSystem system :
                starSystemRepository.findAll()) {

            StarSystemEconomicProfile profile =
                    system.getEconomicProfile();

            addToMap(
                    production,
                    profile.getExtractionCapacity()
            );

            addToMap(
                    production,
                    profile.getManufacturing()
            );

            addToMap(
                    consumption,
                    profile.getConsumption()
            );
        }

        for (Market market :
                marketRepository.findAll()) {

            CommodityType commodity =
                    market.getCommodity().getType();

            inventory.merge(
                    commodity,
                    market.getInventory().doubleValue(),
                    Double::sum
            );
        }


        return Arrays.stream(CommodityType.values())
                .map(type ->
                        new CommodityEconomicSummary(
                                type,
                                production.getOrDefault(
                                        type,
                                        0.0
                                ),
                                consumption.getOrDefault(
                                        type,
                                        0.0
                                ),
                                inventory.getOrDefault(
                                        type,
                                        0.0
                                )
                        )
                )
                .toList();
    }

    private void addToMap(
            Map<CommodityType, Double> target,
            Map<CommodityType, Double> source) {

        source.forEach(
                (commodity, amount) ->
                        target.merge(
                                commodity,
                                amount,
                                Double::sum
                        )
        );
    }

    public void outputEconomicSummary(long tick) {

        Path logFile =
                Paths.get(
                        "logs",
                        "galaxy-economic-summary.txt"
                );

        try {

                Files.createDirectories(
                        logFile.getParent()
                );

                try (BufferedWriter writer =
                        Files.newBufferedWriter(
                                logFile,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.APPEND
                        )) {

                writer.newLine();
                writer.write(
                        "========== GALAXY ECONOMIC SUMMARY =========="
                );
                writer.newLine();
                writer.write(
                        "Tick: " + tick
                );
                writer.newLine();

                for (CommodityEconomicSummary summary :
                        getEconomicSummary()) {

                        writer.write(
                                String.format(
                                        "%-24s Production: %8.1f | Consumption: %8.1f | Net: %8.1f | Inventory: %10.1f",
                                        summary.getCommodity(),
                                        summary.getProduction(),
                                        summary.getConsumption(),
                                        summary.getNetProduction(),
                                        summary.getInventory()
                                )
                        );

                        writer.newLine();
                }

                writer.write(
                        "=============================================="
                );

                writer.newLine();
                }

        } catch (IOException exception) {

                throw new RuntimeException(
                        "Failed to write economic summary",
                        exception
                );
        }
        }
}
