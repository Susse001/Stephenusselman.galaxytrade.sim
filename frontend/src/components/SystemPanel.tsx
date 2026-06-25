import type { Market } from "../types/market";
import type { StarSystem } from "../types/starSystem";

interface SystemPanelProps {
    system: StarSystem | null;
    markets: Market[];
    loading: boolean;
}

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
                minWidth: "300px"
            }}
        >
            <h3>{system.name}</h3>

            <p>
                <strong>Region:</strong>{" "}
                {system.region}
            </p>

            <p>
                <strong>Coordinates:</strong>{" "}
                ({system.xCoordinate},
                {" "}
                {system.yCoordinate})
            </p>

            <hr />

            <h4>Markets</h4>

            {loading && (
                <p>Loading market data...</p>
            )}

            {!loading &&
                markets.length === 0 && (
                <p>No market data available.</p>
            )}

            {markets.map(market => (
                <div
                    key={market.id}
                    style={{
                        marginBottom: "0.75rem"
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