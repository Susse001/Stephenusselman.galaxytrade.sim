import type { StarSystem } from "../types/starSystem";
import type { Trader } from "../types/trader";

/**
 * Calculates the rendered position of a trader on the galaxy map.
 *
 * Traveling traders are interpolated between systems while docked
 * traders remain positioned at their current system.
 */
export function getTraderPosition(
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
                ticksTravelled < 1;

            const isArrivingSystem =
                trader.travelTicksRemaining < 1;

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