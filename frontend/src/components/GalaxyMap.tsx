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

const tradersBySystem =
    traders.reduce(
        (
            acc,
            trader
        ) => {

            if (
                !acc[
                    trader.currentSystemId
                ]
            ) {

                acc[
                    trader.currentSystemId
                ] = [];
            }

            acc[
                trader.currentSystemId
            ].push(trader);

            return acc;

        },
        {} as Record<
            number,
            Trader[]
        >
    );

const traderOffsets = [
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

                        {(
                tradersBySystem[
                    system.id
                    ] ?? []
                ).map(
                    (
                        trader,
                        index
                    ) => {

                        const offset =
                            traderOffsets[
                                index %
                                traderOffsets.length
                            ];

                        return (
                            <circle
                                key={trader.id}
                                cx={
                                    system.xCoordinate
                                    + offset.x
                                }
                                cy={
                                    system.yCoordinate
                                    + offset.y
                                }
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
                    }
                )}

                    </g>
                ))}

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
