import { useEffect, useState } from "react";
import type { StarSystem } from "../types/system";
import type { Market } from "../types/market";
import type {Trader} from "../types/trader";
import { getMarketsForSystem } from "../api/marketApi";
import {getTraders} from "../api/traderApi";

interface GalaxyMapProps {
    systems: StarSystem[];
}

const regionColors: Record<string, string> = {
    CORE: "#60a5fa",
    MID_RIM: "#34d399",
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

    useEffect(() => {

        getTraders()
            .then(setTraders)
            .catch(error =>
                console.error(
                    "Failed to load traders",
                    error
                )
            );

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

    function renderMarketPanel() {

        if (!selectedSystem) {
            return null;
        }

        return (
            <div
                style={{
                    marginTop: "1rem",
                    padding: "1rem",
                    backgroundColor: "#1f2937",
                    color: "white",
                    borderRadius: "8px",
                    maxWidth: "400px"
                }}
            >
                <h3>{selectedSystem.name}</h3>

                <p>
                    <strong>Region:</strong>{" "}
                    {selectedSystem.region}
                </p>

                <p>
                    <strong>Coordinates:</strong>{" "}
                    (
                    {selectedSystem.xCoordinate},
                    {" "}
                    {selectedSystem.yCoordinate}
                    )
                </p>

                <hr />

                <h4>Market Data</h4>

                {loadingMarkets && (
                    <p>Loading market data...</p>
                )}

                {!loadingMarkets &&
                    markets.length === 0 && (
                    <p>No market data available.</p>
                )}

                {markets.map(market => (
                    <div
                        key={market.id}
                        style={{
                            marginBottom: "1rem"
                        }}
                    >
                        <strong>
                            {market.commodityType}
                        </strong>

                        <div>
                            Price: {market.price}
                        </div>

                        <div>
                            Supply: {market.supply}
                        </div>

                        <div>
                            Demand: {market.demand}
                        </div>
                    </div>
                ))}
            </div>
        );
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
    { x: -2, y: -1.5 },
    { x: -2, y: 0 },
    { x: -2, y: 1.5 },

    { x: -3.5, y: -1.5},
    { x: -3.5, y: -0},
    { x: -3.5, y: 1.5},

    { x: -5, y: -1.5},
    { x: -5, y: -0},
    { x: -5, y: 1.5},
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
                            />
                        );
                    }
                )}

                        <text
                            x={
                                system.xCoordinate + 3
                            }
                            y={
                                system.yCoordinate + 1
                            }
                            fill="white"
                            fontSize="2"
                        >
                            {system.name}
                        </text>
                    </g>
                ))}

            </svg>

            {renderMarketPanel()}
        </>
    );
}
