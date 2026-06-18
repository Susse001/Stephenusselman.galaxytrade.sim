import { useState } from "react";
import type { StarSystem } from "../types/system";
import type { Market } from "../types/market";
import { getMarketsForSystem } from "../api/marketApi";

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

const VIEWBOX_PADDING = 50;

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
                {systems.map(system => (
                    <g key={system.id}>
                        <circle
                            cx={system.xCoordinate}
                            cy={system.yCoordinate}
                            r={2}
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

                        <text
                            x={
                                system.xCoordinate + 5
                            }
                            y={
                                system.yCoordinate + 2
                            }
                            fill="white"
                            fontSize="4"
                        >
                            {system.name}
                        </text>
                    </g>
                ))}

                <circle
                cx={50}
                cy={50}
                r={20}
                fill="none"
                stroke="#374151"
                strokeDasharray="2,2"
                />

                <circle
                    cx={50}
                    cy={50}
                    r={35}
                    fill="none"
                    stroke="#374151"
                    strokeDasharray="2,2"
                />
            </svg>

            {renderMarketPanel()}
        </>
    );
}
