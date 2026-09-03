package com.stephenu.gts.planet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;

import lombok.RequiredArgsConstructor;

@Component
@Order(3)
@RequiredArgsConstructor
public class PlanetDataLoader implements CommandLineRunner {

    private final PlanetRepository planetRepository;
    private final StarSystemRepository starSystemRepository;
    private final PlanetResourceGenerator planetResourceGenerator;
    private final PlanetConsumptionProfile planetConsumptionProfile;
    private final PlanetProductionProfile planetProductionProfile;

    private final Random random = new Random();

    @Override
    public void run(String... args) {

        if (planetRepository.count() > 0) {
            return;
        }

        List<Planet> planets = new ArrayList<>();

        for (StarSystem system : starSystemRepository.findAll()) {
            planets.addAll(generatePlanets(system));
        }

        planetRepository.saveAll(planets);
    }

    private List<Planet> generatePlanets(StarSystem system) {

        final List<Integer> PLANET_COUNTS = List.of(
        1,
        2, 2,
        3, 3, 3, 3,
        4, 4, 4, 4, 4,
        5, 5,
        6, 6,
        7
    );

    int planetCount = Planet.chooseWeighted(PLANET_COUNTS, random);

        List<Planet> planets =
                new ArrayList<>();

        for (int i = 1; i <= planetCount; i++) {

            Planet planet = new Planet();

            planet.generate(
                    system,
                    i,
                    planetCount,
                    random
            );

            planetResourceGenerator.generateAndAttachResources(
                planet,
                random
            );

            planet.setConsumptionProfile(
                planetConsumptionProfile.generateConsumption(planet)
            );

            planet.setProductionProfile(
                planetProductionProfile.generateProfile(planet)
            );

            system.addPlanet(planet);
            planets.add(planet);
        }

        return planets;
    }
}
