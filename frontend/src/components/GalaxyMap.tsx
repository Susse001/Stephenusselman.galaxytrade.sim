import { useEffect, useState } from "react";

import type { StarSystem } from "../types/starSystem";
import SystemPanel from "./SystemPanel";
import GalaxySystems from "./GalaxySystems";

import type { Market } from "../types/market";
import { getMarketsForSystem } from "../api/marketApi";

import type {Trader} from "../types/trader";
import {getTraders} from "../api/traderApi";
import TraderPanel from "./TraderPanel";
import TradeRouteOverlay from "./TradeRouteOverlay";
import GalaxyTraders from "./GalaxyTraders";

interface GalaxyMapProps {
    systems: StarSystem[];
}

export default function GalaxyMap({
    systems
}: GalaxyMapProps) {

    const [selectedSystem, setSelectedSystem] =
        useState<StarSystem | null>(null);

    const [markets, setMarkets] =
        useState<Market[]>([]);

    const [loadingMarkets, setLoadingMarkets] =
        useState(false);

    const [traders, setTraders] =
        useState<Trader[]>([]);

    const [selectedTrader, setSelectedTrader] =
        useState<Trader | null>(null);

    useEffect(() => {

        const interval = setInterval(() => {

            getTraders()
                .then(setTraders)
                .catch(error =>
                    console.error(error)
                );

        }, 1000);

        return () =>
            clearInterval(interval);

    }, []);

    async function handleSystemClick(
        system: StarSystem
    ) {

        setSelectedSystem(system);
        setLoadingMarkets(true);

        try {
            setMarkets(await getMarketsForSystem(system.id)
        );

        } catch (error) {
            console.error(
                "Failed to load market data",
                error
            );
            
            setMarkets([]);

        } finally {

            setLoadingMarkets(false);

        }
    }

    const minX = Math.min(
        ...systems.map(system => system.xCoordinate)
    );

    const minY = Math.min(
        ...systems.map(system => system.yCoordinate)
    );

    const maxX = Math.max(
        ...systems.map(system => system.xCoordinate)
    );

    const maxY = Math.max(
        ...systems.map(system => system.yCoordinate)
    );

    const VIEWBOX_PADDING = 10;

    return (
        <>
            <svg
                width={800}
                height={600}
                viewBox={`${
                    minX - VIEWBOX_PADDING
                } ${
                    minY - VIEWBOX_PADDING
                } ${
                    maxX - minX + VIEWBOX_PADDING * 2
                } ${
                    maxY - minY + VIEWBOX_PADDING * 2
                }`}
                preserveAspectRatio="xMidYMid meet"
                style={{
                    backgroundColor: "#111827",
                    borderRadius: "8px",
                    border: "1px solid #374151"
                }}
            >
                <circle
                    cx={50}
                    cy={50}
                    r={25}
                    fill="none"
                    stroke="#374151"
                    strokeDasharray="2,2"
                />

                <circle
                    cx={50}
                    cy={50}
                    r={40}
                    fill="none"
                    stroke="#374151"
                    strokeDasharray="2,2"
                />

                <TradeRouteOverlay
                trader={selectedTrader}
                systems={systems}
                />

                <GalaxySystems
                    systems={systems}
                    selectedSystem={selectedSystem}
                    onSystemClick={handleSystemClick}
                />

                <GalaxyTraders
                    traders={traders}
                    systems={systems}
                    selectedTrader={selectedTrader}
                    onTraderClick={setSelectedTrader}
                />

            </svg>

            <div
                style={{
                    display: "flex",
                    gap: "1rem",
                    marginTop: "1rem"
                }}
            >
                <SystemPanel
                    system={selectedSystem}
                    markets={markets}
                    loading={loadingMarkets}
                />

                <TraderPanel
                    trader={selectedTrader}
                />
            </div>
        </>
    );
}
