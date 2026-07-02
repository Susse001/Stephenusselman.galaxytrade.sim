package com.stephenu.gts.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes REST endpoints for controlling and monitoring the simulation.
 */
@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SimulationController {

    private final SimulationService simulationService;

    /**
     * Advances the simulation by one tick.
     *
     * @return The updated simulation status.
     */
    @PostMapping("/tick")
    public SimulationStatusResponse runTick() {

        long tick = simulationService.runTick();

        return new SimulationStatusResponse(tick);
    }

    /**
     * Returns the current simulation status.
     *
     * @return The current simulation status.
     */
    @GetMapping("/status")
    public SimulationStatusResponse getStatus() {

        return new SimulationStatusResponse(
                simulationService.getCurrentTick()
        );
    }
}
