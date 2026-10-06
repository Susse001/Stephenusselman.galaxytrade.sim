import type { Planet } from "./planet";

/**
 * Represents a star system with its corresponding planets displayed within the galaxy.
 */
export interface StarSystemDetailed {
    id: number;
    name: string;
    xCoordinate: number;
    yCoordinate: number;
    region: string;
    planets: Planet[];
}