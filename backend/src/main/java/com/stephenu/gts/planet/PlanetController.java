package com.stephenu.gts.planet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.stephenu.gts.planet.dto.PlanetResponse;

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
    public List<PlanetResponse> getAllPlanets() {
        return planetService.getAllPlanets();
    }

    /**
     * Returns a planet by id.
     */
    @GetMapping("/{id}")
    public PlanetResponse getPlanet(
            @PathVariable Long id) {

        return planetService.getPlanet(id);
    }
}
