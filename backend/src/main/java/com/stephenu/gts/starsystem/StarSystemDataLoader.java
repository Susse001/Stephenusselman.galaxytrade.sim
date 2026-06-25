package com.stephenu.gts.starsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(2)
public class StarSystemDataLoader implements CommandLineRunner {

    private final StarSystemRepository systemRepository;

    private static final int MAP_SIZE = 100;
    private static final int MIN_DISTANCE = 5;

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

    @Override
    public void run(String... args) {

        if (systemRepository.count() > 0) {
            return;
        }

        Random random = new Random();

        List<StarSystem> systems = new ArrayList<>();

        for (int i = 0; i < 30; i++) {

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

            Region region;

            double distance =
            Math.sqrt(
                Math.pow(x - 50, 2)
                +
                Math.pow(y - 50, 2)
            );

            if (distance < 25) {
                region = Region.CORE;
            }
            else if (distance < 40) {
                region = Region.INNER_RIM;
            }
            else {
                region = Region.OUTER_RIM;
            }

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
}