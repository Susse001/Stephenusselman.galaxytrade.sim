package com.stephenu.gts.simulation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping("/tick")
    public SimulationStatusResponse runTick() {

        long tick = simulationService.runTick();

        return new SimulationStatusResponse(tick);
    }

    @GetMapping("/status")
    public SimulationStatusResponse getStatus() {

        return new SimulationStatusResponse(
                simulationService.getCurrentTick()
        );
    }
}
