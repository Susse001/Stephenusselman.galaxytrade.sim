import { useEffect, useState } from "react";
import type { StarSystem } from "../types/starSystem";
import SystemPanel from "./SystemPanel";
import type { Market } from "../types/market";
import type {Trader} from "../types/trader";
import { getMarketsForSystem } from "../api/marketApi";
import {getTraders} from "../api/traderApi";
import TraderPanel from "./TraderPanel";

interface GalaxyMapProps {
    systems: StarSystem[];
}

const regionColors: Record<string, string> = {
    CORE: "#60a5fa",
    INNER_RIM: "#34d399",
    OUTER_RIM: "#f59e0b"
};

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

            const marketData =
                await getMarketsForSystem(system.id);

            setMarkets(marketData);

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

const traderDockedOffsets = [
    // North
    { x: 0, y: -2.5 },
    { x: -1, y: -2.5 },
    { x: 1, y: -2.5 },

    // South
    { x: 0, y: 2.5 },
    { x: -1, y: 2.5 },
    { x: 1, y: 2.5 },

    // East
    { x: 2.5, y: 0 },
    { x: 2.5, y: -1 },
    { x: 2.5, y: 1 },

    // West
    { x: -2.5, y: 0 },
    { x: -2.5, y: -1 },
    { x: -2.5, y: 1 }
];

const traderTravelOffsets = [

    { x: 0, y: 0 },

    { x: 0.5, y: 0.5 },
    { x: -0.5, y: -0.5 },

    { x: 0.5, y: -0.5 },
    { x: -0.5, y: 0.5 },

    { x: 1, y: 0 },
    { x: -1, y: 0 }
];

function getTraderPosition(
    trader: Trader,
    systems: StarSystem[]) {

        const currentSystem =
            systems.find(
                s =>
                    s.id ===
                    trader.currentSystemId
            );

        if (!currentSystem) {
            return null;
        }

        let startSystem: StarSystem | undefined;
        let endSystem: StarSystem | undefined;

        if (
            trader.status ===
            "TRAVELING_TO_BUY"
        ) {
            startSystem = currentSystem;

            endSystem =
                systems.find(
                    s =>
                        s.id ===
                        trader.currentTrade?.buySystemId
                );
        }

        if (
            trader.status ===
            "TRAVELING_TO_SELL"
        ) {
            startSystem =
                systems.find(
                    s =>
                        s.id ===
                        trader.currentTrade?.buySystemId
                );

            endSystem =
                systems.find(
                    s =>
                        s.id ===
                        trader.currentTrade?.sellSystemId
                );
        }

        if (!startSystem ||
            !endSystem ||
            trader.travelTicksRemaining == null ||
            trader.totalTravelTicks == null) {

            return {
                x: currentSystem.xCoordinate,
                y: currentSystem.yCoordinate,
                docked: true
            };
        }

        const progress =
            1 -
            (
                trader.travelTicksRemaining /
                trader.totalTravelTicks
            );
        

        const ticksTravelled =
        trader.totalTravelTicks -
        trader.travelTicksRemaining;

        const isLeavingSystem =
            ticksTravelled <= 1;

        const isArrivingSystem =
            trader.travelTicksRemaining <= 1;

        if (isLeavingSystem) {
            return {
                x: startSystem.xCoordinate,
                y: startSystem.yCoordinate,
                docked: true
            };
        }

        if (isArrivingSystem) {
            return {
                x: endSystem.xCoordinate,
                y: endSystem.yCoordinate,
                docked: true
            };
        }

        return {
            x:
                startSystem.xCoordinate +
                (
                    endSystem.xCoordinate
                    -
                    startSystem.xCoordinate
                ) * progress,

            y:
                startSystem.yCoordinate +
                (
                    endSystem.yCoordinate
                    -
                    startSystem.yCoordinate
                ) * progress,
            
            docked: false
        };
}

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
                {systems.map(system => (
                    <g key={system.id}>
                        <circle
                            cx={system.xCoordinate}
                            cy={system.yCoordinate}
                            r={1}
                            fill={
                                regionColors[
                                    system.region
                                ] ?? "#9ca3af"
                            }
                            onClick={() =>
                                handleSystemClick(system)
                            }
                            style={{
                                cursor: "pointer"
                            }}
                        />
                    </g>
                ))}

                {traders.map((trader, index) => {

                    const position =
                        getTraderPosition(
                            trader,
                            systems
                        );

                    if (!position) {
                        return null;
                    }

                    let x = position.x;
                    let y = position.y;

                    if (
                        position.docked == false
                    ) {

                        const offset =
                            traderTravelOffsets[
                                index %
                                traderTravelOffsets.length
                            ];

                        x += offset.x;
                        y += offset.y;
                    }

                    else {

                        const offset =
                            traderDockedOffsets[
                                index %
                                traderDockedOffsets.length
                            ];

                        x += offset.x;
                        y += offset.y;
                    }

                    return (
                        <circle
                            key={trader.id}
                            cx={x}
                            cy={y}
                            r={0.5}
                            fill="white"
                            onClick={() =>
                                setSelectedTrader(trader)
                            }
                            style={{
                                cursor: "pointer"
                            }}
                        />
                    );
                })}

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
