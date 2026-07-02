/**
 * Represents the current state of an autonomous trader.
 */
export interface Trader {
    id: number;
    name: string;
    currentSystemId: number;
    currentSystemName: string;
    credits: number;
    strategyProfile: string;
    status: string;
    travelTicksRemaining: number;
    totalTravelTicks: number;
    cargoCommodity: string | null;
    cargoAmount: number;
    cargoCapacity: number;

    currentTrade: TradeOpportunity | null;
}

/**
 * Represents the current state of an autonomous trader.
 */
export interface TradeOpportunity {
    id: number;
    commodity: string;
    buySystemId: number;
    buySystemName: string;
    sellSystemId: number;
    sellSystemName: string;
    expectedProfitPerUnit: number;
}