import type { Market } from "../types/market";
import type { StarSystemDetailed } from "../types/starSystemDetailed";

interface SystemPanelProps {
    system: StarSystemDetailed | null;
    markets: Market[];
    loading: boolean;
}

/**
 * Displays detailed information for the currently selected star system.
 */
export default function SystemPanel({
    system,
    markets,
    loading
}: SystemPanelProps) {

    if (!system) {
        return null;
    }

    return (
        <div
            style={{
                padding: "1rem",
                backgroundColor: "#1f2937",
                color: "white",
                borderRadius: "8px",
                width: "500px",
                maxHeight: "600px",
                overflowY: "auto"
            }}
        >
            <h3>{system.name}</h3>

            <div
                style={{
                    display: "flex",
                    gap: "1.5rem"
                }}
            >
                <div>
                    <strong>Region:</strong>{" "}
                    {system.region}
                </div>

                <div>
                    <strong>Coordinates:</strong>{" "}
                    ({system.xCoordinate}, {system.yCoordinate})
                </div>
            </div>

            <hr />

            <h4>Planets</h4>

            {system.planets.length === 0 && (
                <p>No planets found.</p>
            )}

            {system.planets.map(planet => (
                <div
                    key={planet.id}
                    style={{
                        padding: "0.75rem",
                        marginBottom: "0.5rem",
                        backgroundColor: "#374151",
                        borderRadius: "6px",
                        cursor: "pointer"
                    }}
                >
                    <strong>{planet.name}</strong>

                    <div
                        style={{
                            display: "flex",
                            gap: "1rem",
                            marginTop: "0.25rem",
                            fontSize: "0.9rem"
                        }}
                    >
                        <span>
                            {planet.planetType}
                        </span>

                        <span>
                            {planet.population}
                        </span>

                        <span>
                            {planet.development}
                        </span>
                    </div>
                </div>
            ))}

            <hr />

            <h4>Markets</h4>

            {loading && (
                <p>Loading market data...</p>
            )}

            {!loading && markets.length === 0 && (
                <p>No market data available.</p>
            )}

            {!loading && markets.length > 0 && (
                <div
                    style={{
                        display: "grid",
                        gridTemplateColumns: "1fr auto auto",
                        gap: "0.5rem 1rem",
                        fontSize: "0.9rem"
                    }}
                >
                    <strong>Commodity</strong>
                    <strong>Price</strong>
                    <strong>Inventory</strong>

                    {markets.map(market => (
                        <div
                            key={market.id}
                            style={{
                                display: "contents"
                            }}
                        >
                            <div>
                                {market.commodityType}
                            </div>

                            <div>
                                {market.price}
                            </div>

                            <div>
                                {market.inventory}
                                {" / "}
                                {market.targetInventory}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}