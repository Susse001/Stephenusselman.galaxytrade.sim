package com.stephenu.gts.starsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.planet.DevelopmentLevel;
import com.stephenu.gts.planet.InfrastructureLevel;
import com.stephenu.gts.planet.OrbitZone;
import com.stephenu.gts.planet.Planet;
import com.stephenu.gts.planet.PlanetResource;
import com.stephenu.gts.planet.PlanetType;
import com.stephenu.gts.planet.PopulationLevel;
import com.stephenu.gts.planet.ResourceLevel;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(5)
public class GalaxyGenerationMonitor implements CommandLineRunner {

    private final StarSystemRepository starSystemRepository;

    private static final Path LOG_FILE =
            Paths.get(
                    "logs",
                    "galaxy-generation-report.txt"
            );

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {

        List<StarSystem> systems =
                starSystemRepository.findAll();

        String report =
                generateReport(systems);

        try {

            Files.createDirectories(
                    LOG_FILE.getParent()
            );

            Files.writeString(
                    LOG_FILE,
                    report,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Failed to write galaxy generation report",
                    exception
            );
        }
    }

    private String generateReport(
            List<StarSystem> systems) {

        StringBuilder report =
                new StringBuilder();

        appendHeader(report);

        appendGalaxyOverview(
                report,
                systems
        );

        appendPlanetTypeDistribution(
                report,
                systems
        );

        appendOrbitDistribution(
                report,
                systems
        );

        appendPopulationDistribution(
                report,
                systems
        );

        appendDevelopmentDistribution(
                report,
                systems
        );

        appendInfrastructureDistribution(
                report,
                systems
        );

        appendResourceDistribution(
                report,
                systems
        );

        appendSpecializationDistribution(
                report,
                systems
        );

        appendManufacturingSummary(
                report,
                systems
        );

        appendSystemBreakdown(
                report,
                systems
        );

        report.append("\n\n");

        return report.toString();
    }

    private void appendHeader(
            StringBuilder report) {

        report.append("\n\n");
        report.append(
                "============================================================\n"
        );
        report.append(
                "                 GALAXY GENERATION REPORT\n"
        );
        report.append(
                "============================================================\n"
        );
        report.append(
                "Generated: "
        );
        report.append(
                LocalDateTime.now()
        );
        report.append("\n");
    }

    private void appendGalaxyOverview(
            StringBuilder report,
            List<StarSystem> systems) {

        int planetCount =
                systems.stream()
                        .mapToInt(
                                system ->
                                        system.getPlanets().size()
                        )
                        .sum();

        double averagePlanets =
                systems.isEmpty()
                        ? 0.0
                        : (double) planetCount
                                / systems.size();

        report.append("\n");
        report.append("GALAXY OVERVIEW\n");
        report.append("----------------\n");

        report.append(
                String.format(
                        "Star Systems:       %d%n",
                        systems.size()
                )
        );

        report.append(
                String.format(
                        "Planets:            %d%n",
                        planetCount
                )
        );

        report.append(
                String.format(
                        "Average Planets:    %.2f%n",
                        averagePlanets
                )
        );
    }

    private void appendPlanetTypeDistribution(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<PlanetType, Long> counts =
                countPlanets(
                        systems,
                        Planet::getPlanetType
                );

        report.append("\n");
        report.append("PLANET TYPES\n");
        report.append("------------\n");

        for (PlanetType type :
                PlanetType.values()) {

            report.append(
                    String.format(
                            "%-20s %5d%n",
                            type,
                            counts.getOrDefault(
                                    type,
                                    0L
                            )
                    )
            );
        }
    }

    private void appendOrbitDistribution(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<OrbitZone, Long> counts =
                countPlanets(
                        systems,
                        Planet::getOrbitZone
                );

        report.append("\n");
        report.append("ORBIT ZONES\n");
        report.append("-----------\n");

        for (OrbitZone zone :
                OrbitZone.values()) {

            report.append(
                    String.format(
                            "%-20s %5d%n",
                            zone,
                            counts.getOrDefault(
                                    zone,
                                    0L
                            )
                    )
            );
        }
    }

    private void appendPopulationDistribution(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<PopulationLevel, Long> counts =
                countPlanets(
                        systems,
                        Planet::getPopulation
                );

        report.append("\n");
        report.append("POPULATION LEVELS\n");
        report.append("-----------------\n");

        for (PopulationLevel level :
                PopulationLevel.values()) {

            report.append(
                    String.format(
                            "%-25s %5d%n",
                            level,
                            counts.getOrDefault(
                                    level,
                                    0L
                            )
                    )
            );
        }
    }

    private void appendDevelopmentDistribution(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<DevelopmentLevel, Long> counts =
                countPlanets(
                        systems,
                        Planet::getDevelopment
                );

        report.append("\n");
        report.append("DEVELOPMENT LEVELS\n");
        report.append("------------------\n");

        for (DevelopmentLevel level :
                DevelopmentLevel.values()) {

            report.append(
                    String.format(
                            "%-20s %5d%n",
                            level,
                            counts.getOrDefault(
                                    level,
                                    0L
                            )
                    )
            );
        }
    }

    private void appendInfrastructureDistribution(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<InfrastructureLevel, Long> counts =
                countPlanets(
                        systems,
                        Planet::getInfrastructure
                );

        report.append("\n");
        report.append("INFRASTRUCTURE LEVELS\n");
        report.append("---------------------\n");

        for (InfrastructureLevel level :
                InfrastructureLevel.values()) {

            report.append(
                    String.format(
                            "%-20s %5d%n",
                            level,
                            counts.getOrDefault(
                                    level,
                                    0L
                            )
                    )
            );
        }
    }

    private void appendResourceDistribution(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<CommodityType, Map<ResourceLevel, Integer>>
                distribution =
                        new EnumMap<>(
                                CommodityType.class
                        );

        for (CommodityType commodity :
                CommodityType.values()) {

            Map<ResourceLevel, Integer> levels =
                    new EnumMap<>(
                            ResourceLevel.class
                    );

            for (ResourceLevel level :
                    ResourceLevel.values()) {

                levels.put(level, 0);
            }

            distribution.put(
                    commodity,
                    levels
            );
        }

        for (StarSystem system : systems) {

            for (Planet planet :
                    system.getPlanets()) {

                for (PlanetResource resource :
                        planet.getResources()) {

                    CommodityType commodity =
                            resource.getCommodity()
                                    .getType();

                    ResourceLevel level =
                            resource.getAbundance();

                    distribution
                            .get(commodity)
                            .merge(
                                    level,
                                    1,
                                    Integer::sum
                            );
                }
            }
        }

        report.append("\n");
        report.append("RESOURCE LEVEL DISTRIBUTION\n");
        report.append("--------------------------\n");

        report.append(
                String.format(
                        "%-24s %7s %7s %8s %7s %9s%n",
                        "Commodity",
                        "NONE",
                        "SCARCE",
                        "AVERAGE",
                        "RICH",
                        "ABUNDANT"
                )
        );

        for (CommodityType commodity :
                CommodityType.values()) {

            Map<ResourceLevel, Integer> levels =
                    distribution.get(commodity);

            report.append(
                    String.format(
                            "%-24s %7d %7d %8d %7d %9d%n",
                            commodity,
                            levels.get(
                                    ResourceLevel.NONE
                            ),
                            levels.get(
                                    ResourceLevel.SCARCE
                            ),
                            levels.get(
                                    ResourceLevel.AVERAGE
                            ),
                            levels.get(
                                    ResourceLevel.RICH
                            ),
                            levels.get(
                                    ResourceLevel.ABUNDANT
                            )
                    )
            );
        }
    }

    private void appendSpecializationDistribution(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<CommodityType, Integer> specializationCounts =
                new EnumMap<>(
                        CommodityType.class
                );

        Map<CommodityType, Set<Long>> systemsByCommodity =
                new EnumMap<>(
                        CommodityType.class
                );

        for (CommodityType commodity :
                CommodityType.values()) {

            specializationCounts.put(
                    commodity,
                    0
            );

            systemsByCommodity.put(
                    commodity,
                    new HashSet<>()
            );
        }

        for (StarSystem system : systems) {

            Map<CommodityType, Integer> specializations =
                    system.getEconomicProfile()
                            .getSpecializations();

            for (Map.Entry<CommodityType, Integer> entry :
                    specializations.entrySet()) {

                CommodityType commodity =
                        entry.getKey();

                int count =
                        entry.getValue();

                if (count <= 0) {
                    continue;
                }

                specializationCounts.merge(
                        commodity,
                        count,
                        Integer::sum
                );

                systemsByCommodity
                        .get(commodity)
                        .add(system.getId());
            }
        }

        report.append("\n");
        report.append("MANUFACTURING SPECIALIZATIONS\n");
        report.append("-----------------------------\n");

        report.append(
                String.format(
                        "%-24s %15s %10s%n",
                        "Commodity",
                        "Specializations",
                        "Systems"
                )
        );

        for (CommodityType commodity :
                CommodityType.values()) {

            int specializations =
                    specializationCounts.get(
                            commodity
                    );

            int systemsCount =
                    systemsByCommodity
                            .get(commodity)
                            .size();

            report.append(
                    String.format(
                            "%-24s %15d %10d%n",
                            commodity,
                            specializations,
                            systemsCount
                    )
            );
        }
    }

    private void appendManufacturingSummary(
            StringBuilder report,
            List<StarSystem> systems) {

        Map<CommodityType, Double> potential =
                new EnumMap<>(
                        CommodityType.class
                );

        Map<CommodityType, Double> manufacturing =
                new EnumMap<>(
                        CommodityType.class
                );

        for (StarSystem system : systems) {

            StarSystemEconomicProfile profile =
                    system.getEconomicProfile();

            addToMap(
                    potential,
                    profile.getManufacturingPotential()
            );

            addToMap(
                    manufacturing,
                    profile.getManufacturing()
            );
        }

        report.append("\n");
        report.append("MANUFACTURING SUMMARY\n");
        report.append("---------------------\n");

        report.append(
                String.format(
                        "%-24s %12s %12s %12s%n",
                        "Commodity",
                        "Potential",
                        "Actual",
                        "Utilization"
                )
        );

        for (CommodityType commodity :
                CommodityType.values()) {

            double totalPotential =
                    potential.getOrDefault(
                            commodity,
                            0.0
                    );

            double actual =
                    manufacturing.getOrDefault(
                            commodity,
                            0.0
                    );

            double utilization =
                    totalPotential <= 0.0
                            ? 0.0
                            : actual
                                    / totalPotential
                                    * 100.0;

            report.append(
                    String.format(
                            "%-24s %12.1f %12.1f %11.1f%%%n",
                            commodity,
                            totalPotential,
                            actual,
                            utilization
                    )
            );
        }
    }

    private void appendSystemBreakdown(
            StringBuilder report,
            List<StarSystem> systems) {

        report.append("\n");
        report.append("SYSTEM BREAKDOWN\n");
        report.append("================\n");

        for (StarSystem system : systems) {

            report.append("\n");
            report.append(
                    "------------------------------------------------------------\n"
            );

            report.append(
                    String.format(
                            "SYSTEM: %s%n",
                            system.getName()
                    )
            );

            report.append(
                    String.format(
                            "Region: %s%n",
                            system.getRegion()
                    )
            );

            report.append(
                    String.format(
                            "Planets: %d%n",
                            system.getPlanets().size()
                    )
            );

            for (Planet planet :
                    system.getPlanets()) {

                appendPlanet(
                        report,
                        planet
                );
            }

            appendSystemSpecializations(
                    report,
                    system
            );
        }
    }

    private void appendPlanet(
            StringBuilder report,
            Planet planet) {

        report.append("\n");

        report.append(
                String.format(
                        "  %s%n",
                        planet.getName()
                )
        );

        report.append(
                String.format(
                        "    Type:           %s%n",
                        planet.getPlanetType()
                )
        );

        report.append(
                String.format(
                        "    Orbit:          %s (%d)%n",
                        planet.getOrbitZone(),
                        planet.getOrbitalOrder()
                )
        );

        report.append(
                String.format(
                        "    Population:     %s%n",
                        planet.getPopulation()
                )
        );

        report.append(
                String.format(
                        "    Development:    %s%n",
                        planet.getDevelopment()
                )
        );

        report.append(
                String.format(
                        "    Infrastructure: %s%n",
                        planet.getInfrastructure()
                )
        );

        report.append(
                String.format(
                        "    Features:       %s%n",
                        planet.getFeatures()
                )
        );

        appendPlanetResources(
                report,
                planet
        );

        appendPlanetSpecializations(
                report,
                planet
        );
    }

    private void appendPlanetResources(
            StringBuilder report,
            Planet planet) {

        report.append(
                "    Resources:\n"
        );

        for (PlanetResource resource :
                planet.getResources()) {

            report.append(
                    String.format(
                            "      %-24s %s%n",
                            resource.getCommodity()
                                    .getType(),
                            resource.getAbundance()
                    )
            );
        }
    }

    private void appendPlanetSpecializations(
            StringBuilder report,
            Planet planet) {

        Map<CommodityType, Integer> specializations =
                planet.getSpecializations();

        report.append(
                "    Specializations:\n"
        );

        if (specializations.isEmpty()) {

            report.append(
                    "      None\n"
            );

            return;
        }

        for (Map.Entry<CommodityType, Integer> entry :
                specializations.entrySet()) {

            report.append(
                    String.format(
                            "      %-24s %d%n",
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }
    }

    private void appendSystemSpecializations(
            StringBuilder report,
            StarSystem system) {

        report.append("\n");
        report.append(
                "  SYSTEM SPECIALIZATIONS\n"
        );

        Map<CommodityType, Integer> specializations =
                system.getEconomicProfile()
                        .getSpecializations();

        boolean hasSpecialization = false;

        for (Map.Entry<CommodityType, Integer> entry :
                specializations.entrySet()) {

            if (entry.getValue() <= 0) {
                continue;
            }

            hasSpecialization = true;

            report.append(
                    String.format(
                            "    %-24s %d%n",
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        if (!hasSpecialization) {

            report.append(
                    "    None\n"
            );
        }
    }

    private <T> Map<T, Long> countPlanets(
            List<StarSystem> systems,
            Function<Planet, T> classifier) {

        return systems.stream()
                .flatMap(
                        system ->
                                system.getPlanets()
                                        .stream()
                )
                .collect(
                        Collectors.groupingBy(
                                classifier,
                                () -> new HashMap<>(),
                                Collectors.counting()
                        )
                );
    }

    private void addToMap(
            Map<CommodityType, Double> target,
            Map<CommodityType, Double> source) {

        source.forEach(
                (commodity, value) ->
                        target.merge(
                                commodity,
                                value,
                                Double::sum
                        )
        );
    }
}
