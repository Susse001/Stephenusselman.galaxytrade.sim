/**
 * Represents the planet data for a single planet within a star system.
 */
export interface Planet {
    id: number;
    name: string;
    orbitalOrder: number;
    orbitZone: string;
    planetType: string;
    population: string;
    development: string;
    infrastructure: string;
    features: string[];
}