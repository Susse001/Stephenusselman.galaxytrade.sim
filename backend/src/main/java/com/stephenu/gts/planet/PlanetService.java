package com.stephenu.gts.planet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.stephenu.gts.planet.dto.PlanetResponse;

import java.util.List;

/**
 * Provides services for retrieving planet data.
 */
@Service
@RequiredArgsConstructor
public class PlanetService {

    private final PlanetRepository planetRepository;

    /**
     * Returns every planet in the galaxy.
     */
     public List<PlanetResponse> getAllPlanets() {
        return planetRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns the requested planet.
     */
    public PlanetResponse getPlanet(Long id) {
        Planet planet =
                planetRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Planet not found: " + id
                        ));

        return toResponse(planet);
    }

    private PlanetResponse toResponse(Planet planet) {
        return new PlanetResponse(
                planet.getId(),
                planet.getName(),
                planet.getOrbitalOrder(),
                planet.getOrbitZone(),
                planet.getPlanetType(),
                planet.getPopulation(),
                planet.getDevelopment(),
                planet.getInfrastructure(),
                planet.getFeatures()
        );
    }
}