import type { Trader } from "../types/trader";
import { api } from "./api";

/**
 * Retrieves every trader in the simulation.
 *
 * @returns All simulated traders.
 */
export async function getTraders(): Promise<Trader[]> {

    const response =
        await api.get<Trader[]>("/traders");

    return response.data;
}