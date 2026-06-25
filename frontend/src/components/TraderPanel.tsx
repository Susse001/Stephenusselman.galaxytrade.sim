import type { Trader } from "../types/trader";

interface TraderPanelProps {
    trader: Trader | null;
}

export default function TraderPanel({
    trader
}: TraderPanelProps) {

    if (!trader) {
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

            {trader.currentTrade && (
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
                            trader
                                .currentTrade
                                .commodity
                        }
                    </p>

                    <p>
                        <strong>
                            Buy:
                        </strong>{" "}
                        {
                            trader
                                .currentTrade
                                .buySystemName
                        }
                    </p>

                    <p>
                        <strong>
                            Sell:
                        </strong>{" "}
                        {
                            trader
                                .currentTrade
                                .sellSystemName
                        }
                    </p>

                    <p>
                        <strong>
                            Profit/Unit:
                        </strong>{" "}
                        {
                            trader
                                .currentTrade
                                .expectedProfitPerUnit
                        }
                    </p>

                    <p>
                        <strong>
                            Travel Ticks Remaining:
                        </strong>{" "}
                        {
                            trader
                                .travelTicksRemaining
                        }
                    </p>
                </>
            )}
        </div>
    );
}