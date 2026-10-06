import { api } from "./api";
import type { StarSystem } from "../types/starSystem";
import type { StarSystemDetailed, } from "../types/starSystemDetailed";

/**
 * Retrieves every star system in the galaxy.
 *
 * @returns a list of all the star systems.
 */
export async function getSystems(): Promise<StarSystem[]> {
    const response =
        await api.get<StarSystem[]>("/systems");

    return response.data;
}

/**
 * Retrieves the star system and planet data for a star system.
 *
 * @param systemId The identifier of the requested star system.
 * @returns The important information and planets of the given system.
 */
export async function getSystem(
    systemId: number
): Promise<StarSystemDetailed> {
    const response =
        await api.get<StarSystemDetailed>(
            `/systems/${systemId}`
        );

    return response.data;
}