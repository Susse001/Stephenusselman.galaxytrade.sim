import type { Market } from "../types/market";
import { api } from "./api";

/**
 * Retrieves the market data for a star system.
 *
 * @param systemId The identifier of the requested star system.
 * @returns The markets available within the specified system.
 */
export async function getMarketsForSystem(
    systemId: number
): Promise<Market[]> {

    const response =
        await api.get<Market[]>(
            `/markets/system/${systemId}`
        );

    return response.data;
}