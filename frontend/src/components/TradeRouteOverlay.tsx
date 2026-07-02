import type { Trader } from "../types/trader";
import type { StarSystem } from "../types/starSystem";

interface TradeRouteOverlayProps {
    trader: Trader | null;
    systems: StarSystem[];
}

/**
 * Renders the currently selected trader's trade route.
 *
 * The route highlights the assigned buy and sell systems and displays
 * the path connecting them.
 */
export default function TradeRouteOverlay({
    trader,
    systems
}: TradeRouteOverlayProps) {

    const trade = trader?.currentTrade;

    if (!trade) {
        return null;
    }

    const buySystem =
        systems.find(
            system =>
                system.id === trade.buySystemId
        );

    const sellSystem =
        systems.find(
            system =>
                system.id === trade.sellSystemId
        );

    if (!buySystem || !sellSystem) {
        return null;
    }

    return (
        <>
            <line
                x1={buySystem.xCoordinate}
                y1={buySystem.yCoordinate}
                x2={sellSystem.xCoordinate}
                y2={sellSystem.yCoordinate}
                stroke="white"
                strokeWidth={0.25}
                strokeDasharray="1,1"
                opacity={0.6}
            />

            <circle
                cx={buySystem.xCoordinate}
                cy={buySystem.yCoordinate}
                r={1.3}
                fill="none"
                stroke="white"
                strokeWidth={0.15}
            />

            <circle
                cx={sellSystem.xCoordinate}
                cy={sellSystem.yCoordinate}
                r={1.3}
                fill="white"
            />
        </>
    );
}