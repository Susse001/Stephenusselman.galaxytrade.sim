/**
 * Represents the market data for a single commodity within a star system.
 */
export interface Market {
    id: number;
    systemId: number;
    systemName: string;
    region: string;
    commodityType: string;
    price: number;
    inventory: number;
    targetInventory: number;
}