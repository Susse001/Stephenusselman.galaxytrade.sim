package com.stephenu.gts.simulation;

/**
 * Represents the current simulation state.
 *
 * @param currentTick The current simulation tick.
 */
public record SimulationStatusResponse(
        long currentTick
) {}
