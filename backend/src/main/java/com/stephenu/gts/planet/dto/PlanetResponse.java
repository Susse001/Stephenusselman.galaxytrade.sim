package com.stephenu.gts.planet.dto;

import java.util.Set;

import com.stephenu.gts.planet.DevelopmentLevel;
import com.stephenu.gts.planet.InfrastructureLevel;
import com.stephenu.gts.planet.OrbitZone;
import com.stephenu.gts.planet.Planet;
import com.stephenu.gts.planet.PlanetFeature;
import com.stephenu.gts.planet.PlanetType;
import com.stephenu.gts.planet.PopulationLevel;

/**
 * Represents the API response for a planet.
 *
 * @param id The unique identifier of the planet.
 * @param name The display name of the planet.
 * @param orbitalOrder The planet's orbital position within its star system.
 * @param orbitZone The planet's orbital zone.
 * @param planetType The dominant planetary environment.
 * @param population The approximate population level.
 * @param development The planet's technological and industrial development.
 * @param infrastructure The planet's infrastructure level.
 * @param features The unique planetary features.
 */
public record PlanetResponse(
        Long id,
        String name,
        Integer orbitalOrder,
        OrbitZone orbitZone,
        PlanetType planetType,
        PopulationLevel population,
        DevelopmentLevel development,
        InfrastructureLevel infrastructure,
        Set<PlanetFeature> features
) {
    /**
     * Creates a response from a planet entity.
     *
     * @param planet The planet entity.
     * @return The API response representing the planet.
     */
    public static PlanetResponse from(Planet planet) {
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
