import { api } from "./api";

export interface SimulationStatus {
    tick: number;
}

/**
 * Advances the simulation by one tick.
 *
 * @returns The updated simulation status.
 */
export async function runTick(): Promise<SimulationStatus> {

    const response =
        await api.post<SimulationStatus>(
            "/simulation/tick"
        );

    return response.data;
}

/**
 * Retrieves the current simulation status.
 *
 * @returns The current simulation status.
 */
export async function getSimulationStatus(): Promise<SimulationStatus> {

    const response =
        await api.get<SimulationStatus>(
            "/simulation/status"
        );

    return response.data;
}