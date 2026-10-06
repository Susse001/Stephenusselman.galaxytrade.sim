import { api } from "./api";
import type { Planet } from "../types/planet";

/**
 * Retrieves the planet data for the given planet
 *
 * @param systemId The identifier of the requested planet.
 * @returns The important information about the given planet.
 */
export async function getPlanet(
    planetId: number
): Promise<Planet> {
    const response =
        await api.get<Planet>(
            `/planets/${planetId}`
        );

    return response.data;
}