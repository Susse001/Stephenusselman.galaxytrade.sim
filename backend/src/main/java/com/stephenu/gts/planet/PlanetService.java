package com.stephenu.gts.planet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<Planet> getAllPlanets() {
        return planetRepository.findAll();
    }

    /**
     * Returns the requested planet.
     */
    public Planet getPlanet(Long id) {
        return planetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Planet not found: " + id));
    }
}