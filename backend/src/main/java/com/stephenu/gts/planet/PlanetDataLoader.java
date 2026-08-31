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
    PlanetResourceGenerator planetResourceGenerator = new PlanetResourceGenerator();
    PlanetConsumptionProfile planetConsumptionProfile = new PlanetConsumptionProfile();
    PlanetProductionProfile planetProductionProfile = new PlanetProductionProfile();

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

        int planetCount =
                Planet.generatePlanetCount(random);

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

            system.addPlanet(planet);
            planets.add(planet);
        }

        return planets;
    }
}