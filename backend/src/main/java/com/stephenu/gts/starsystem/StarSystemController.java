package com.stephenu.gts.starsystem;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stephenu.gts.starsystem.dto.StarSystemResponse;

import lombok.RequiredArgsConstructor;

/**
 * Exposes REST endpoints for retrieving star system data.
 */
@RestController
@RequestMapping("/api/systems")
@RequiredArgsConstructor
public class StarSystemController {

    private final StarSystemService systemService;

    /**
     * Returns all star systems.
     *
     * @return A list of all star systems.
     */
    @GetMapping
    public List<StarSystemResponse> getSystems() {
        return systemService.getAllSystems();
    }

    /**
     * Returns the star system with the specified ID.
     *
     * @param id The identifier of the requested star system.
     * @return The requested star system.
     */
    @GetMapping("/{id}")
    public StarSystemResponse getSystem(
            @PathVariable Long id
    ) {
        return systemService.getSystem(id);
    }
}
