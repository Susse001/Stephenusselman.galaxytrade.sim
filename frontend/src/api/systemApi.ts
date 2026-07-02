import { api } from "./api";
import type { StarSystem } from "../types/starSystem";

/**
 * Retrieves every star system in the galaxy.
 *
 * @returns All known star systems.
 */
export async function getSystems(): Promise<StarSystem[]> {

    const response =
        await api.get<StarSystem[]>("/systems");

    return response.data;
}