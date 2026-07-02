import type { Trader } from "../types/trader";
import type { StarSystem } from "../types/starSystem";
import { getTraderPosition } from "../utils/traderPosition";

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

interface GalaxyTradersProps {
    traders: Trader[];
    systems: StarSystem[];
    selectedTrader: Trader | null;
    onTraderClick: (trader: Trader) => void;
}

/**
 * Renders every trader currently visible on the galaxy map.
 */
export default function GalaxyTraders({
    traders,
    systems,
    selectedTrader,
    onTraderClick
}: GalaxyTradersProps) {

    return (
        <>
            {traders.map((trader, index) => {

                const position =
                    getTraderPosition(trader, systems);

                if (!position) {
                    return null;
                }

                let x = position.x;
                let y = position.y;

                if (!position.docked) {

                    const offset =
                        traderTravelOffsets[
                            index %
                            traderTravelOffsets.length
                        ];

                    x += offset.x;
                    y += offset.y;

                } else {

                    const offset =
                        traderDockedOffsets[
                            index %
                            traderDockedOffsets.length
                        ];

                    x += offset.x;
                    y += offset.y;
                }

                return (
                    <g key={trader.id}>

                        {selectedTrader?.id === trader.id && (
                            <circle
                                cx={x}
                                cy={y}
                                r={1.1}
                                fill="none"
                                stroke="white"
                                strokeWidth={0.2}
                            />
                        )}

                        <circle
                            cx={x}
                            cy={y}
                            r={0.5}
                            fill="white"
                            onClick={() => onTraderClick(trader)}
                            style={{ cursor: "pointer" }}
                        />

                    </g>
                );
            })}
        </>
    );
}