package com.stephenu.gts.starsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the database with an initial set of star systems.
 *
 * The loader executes during application startup and only populates the
 * database when no star systems are present.
 */
@Component
@RequiredArgsConstructor
@Order(2)
public class StarSystemDataLoader implements CommandLineRunner {

    private final StarSystemRepository systemRepository;

    private static final int MAP_SIZE = 100;
    private static final int MIN_DISTANCE = 5;
    private static final int SYSTEM_COUNT = 30;
    private static final int GALAXY_CENTER = MAP_SIZE / 2;
    private static final int CORE_RADIUS = 25;
    private static final int INNER_RIM_RADIUS = 40;

    private static final List<String> SYSTEM_NAMES = List.of(
            "Aquila",
            "Cygnus",
            "Draco",
            "Orion",
            "Pegasus",
            "Lyra",
            "Vega",
            "Altair",
            "Sirius",
            "Deneb"
    );

    /**
     * Populates the database with procedurally generated star systems if none
     * currently exist.
     *
     * @param args Command-line arguments supplied during application startup.
     */
    @Override
    public void run(String... args) {

        if (systemRepository.count() > 0) {
            return;
        }

        Random random = new Random();

        List<StarSystem> systems = new ArrayList<>();

        for (int i = 0; i < SYSTEM_COUNT; i++) {

            String name =
                    SYSTEM_NAMES.get(i % SYSTEM_NAMES.size())
                    + "-"
                    + (i + 1);

            int x;
            int y;

            do {

                x = random.nextInt(MAP_SIZE);
                y = random.nextInt(MAP_SIZE);

            } while (
                isTooClose(
                    x,
                    y,
                    systems
                )
            );

            Region region = determineRegion(x, y);

            systems.add(
                    new StarSystem(
                            null,
                            name,
                            x,
                            y,
                            region
                    )
            );
        }

        systemRepository.saveAll(systems);
    }

    
    private boolean isTooClose(
        int x,
        int y,
        List<StarSystem> systems) {


        return systems.stream()
                .anyMatch(system -> {

                    double dx =
                            x - system.getXCoordinate();

                    double dy =
                            y - system.getYCoordinate();

                    double distance =
                            Math.sqrt(
                                    dx * dx
                                    +
                                    dy * dy
                            );

                    return distance
                            < MIN_DISTANCE;
                });
    }

    private Region determineRegion(int x, int y) {
        double dx = x - GALAXY_CENTER;
        double dy = y - GALAXY_CENTER;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < CORE_RADIUS) {
            return Region.CORE;
        }

        if (distance < INNER_RIM_RADIUS) {
            return Region.INNER_RIM;
        }

        return Region.OUTER_RIM;
    }
}