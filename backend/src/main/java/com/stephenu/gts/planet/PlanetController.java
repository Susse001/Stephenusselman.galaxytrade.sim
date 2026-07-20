package com.stephenu.gts.planet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes REST endpoints for retrieving planet data.
 */
@RestController
@RequestMapping("/api/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetService planetService;

    /**
     * Returns every planet.
     */
    @GetMapping
    public List<Planet> getAllPlanets() {
        return planetService.getAllPlanets();
    }

    /**
     * Returns a planet by id.
     */
    @GetMapping("/{id}")
    public Planet getPlanet(
            @PathVariable Long id) {

        return planetService.getPlanet(id);
    }
}
