import type { Trader } from "../types/trader";

interface TraderPanelProps {
    trader: Trader | null;
}

/**
 * Displays detailed information for the currently selected trader.
 */
export default function TraderPanel({
    trader
}: TraderPanelProps) {

    if (!trader) {
        return null;
    }

    const trade = trader.currentTrade;

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
            <h3>{trader.name}</h3>

            <p>
                <strong>Profile:</strong>{" "}
                {trader.strategyProfile}
            </p>

            <p>
                <strong>Status:</strong>{" "}
                {trader.status}
            </p>

            <p>
                <strong>Credits:</strong>{" "}
                {trader.credits}
            </p>

            <hr />

            <h4>Cargo</h4>

            <p>
                Commodity:
                {" "}
                {trader.cargoCommodity ??
                    "None"}
            </p>

            <p>
                Amount:
                {" "}
                {trader.cargoAmount}
                /
                {trader.cargoCapacity}
            </p>

            {trade && (
                <>
                    <hr />

                    <h4>
                        Current Trade
                    </h4>

                    <p>
                        <strong>
                            Commodity:
                        </strong>{" "}
                        {
                            trade.commodity
                        }
                    </p>

                    <p>
                        <strong>
                            Buy:
                        </strong>{" "}
                        {
                            trade.buySystemName
                        }
                    </p>

                    <p>
                        <strong>
                            Sell:
                        </strong>{" "}
                        {
                            trade.sellSystemName
                        }
                    </p>

                    <p>
                        <strong>
                            Profit/Unit:
                        </strong>{" "}
                        {
                            trade.expectedProfitPerUnit
                        }
                    </p>

                    <p>
                        <strong>
                            Travel Ticks Remaining:
                        </strong>{" "}
                        {
                            trader.travelTicksRemaining
                        }
                    </p>
                </>
            )}
        </div>
    );
}