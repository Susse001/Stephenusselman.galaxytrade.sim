import { useEffect, useRef, useState } from "react";

import type { StarSystem } from "../types/starSystem";
import type { StarSystemDetailed } from "../types/starSystemDetailed";
import type { Planet } from "../types/planet";
import type { Market } from "../types/market";
import type { Trader } from "../types/trader";

import { getSystem } from "../api/systemApi";
import { getMarketsForSystem } from "../api/marketApi";
import { getPlanet } from "../api/planetApi";
import { getTraders } from "../api/traderApi";
import { getSimulationStatus } from "../api/simulationApi";

import GalaxySystems from "./GalaxySystems";
import GalaxyTraders from "./GalaxyTraders";
import TradeRouteOverlay from "./TradeRouteOverlay";
import SystemPanel from "./SystemPanel";
import PlanetPanel from "./PlanetPanel";
import TraderPanel from "./TraderPanel";

interface GalaxyMapProps {
    systems: StarSystem[];
}

/**
 * Renders the galaxy map and keeps displayed trader state synchronized
 * with the backend simulation.
 */
export default function GalaxyMap({
    systems
}: GalaxyMapProps) {

    const [selectedSystem, setSelectedSystem] =
        useState<StarSystem | null>(null);

    const [selectedSystemDetails, setSelectedSystemDetails] =
        useState<StarSystemDetailed | null>(null);

    const [selectedPlanet, setSelectedPlanet] =
        useState<Planet | null>(null);

    const [markets, setMarkets] =
        useState<Market[]>([]);

    const [loadingMarkets, setLoadingMarkets] =
        useState(false);

    const [traders, setTraders] =
        useState<Trader[]>([]);

    const [selectedTrader, setSelectedTrader] =
        useState<Trader | null>(null);

    const lastSimulationTick =
        useRef<number | null>(null);


    useEffect(() => {

        async function initializeSimulationState() {

            try {

                const [status, traderData] =
                    await Promise.all([
                        getSimulationStatus(),
                        getTraders()
                    ]);

                lastSimulationTick.current =
                    status.tick;

                setTraders(traderData);

            } catch (error) {

                console.error(
                    "Failed to initialize simulation state",
                    error
                );
            }
        }

        initializeSimulationState();

    }, []);

    useEffect(() => {

        const interval = setInterval(async () => {

            try {

                const status =
                    await getSimulationStatus();

                if (
                    lastSimulationTick.current === null
                ) {
                    lastSimulationTick.current =
                        status.tick;

                    return;
                }

                if (
                    status.tick !==
                    lastSimulationTick.current
                ) {

                    lastSimulationTick.current =
                        status.tick;

                    const updatedTraders =
                        await getTraders();

                    setTraders(updatedTraders);
                }

            } catch (error) {

                console.error(
                    "Failed to check simulation status",
                    error
                );
            }

        }, 5000);

        return () =>
            clearInterval(interval);

    }, []);

    async function handleSystemClick(
        system: StarSystem
    ) {

        setSelectedSystem(system);
        setSelectedPlanet(null);
        setLoadingMarkets(true);

        try {

            const [
                systemDetails,
                marketData
            ] = await Promise.all([
                getSystem(system.id),
                getMarketsForSystem(system.id)
            ]);

            setSelectedSystemDetails(
                systemDetails
            );

            setMarkets(marketData);

        } catch (error) {

            console.error(
                "Failed to load system data",
                error
            );

            setSelectedSystemDetails(null);
            setMarkets([]);

        } finally {

            setLoadingMarkets(false);
        }
    }

    async function handlePlanetClick(
        planetId: number
    ) {

        try {

            setSelectedPlanet(
                await getPlanet(planetId)
            );

        } catch (error) {

            console.error(
                "Failed to load planet data",
                error
            );

            setSelectedPlanet(null);
        }
    }

    const minX = Math.min(
        ...systems.map(
            system => system.xCoordinate
        )
    );

    const minY = Math.min(
        ...systems.map(
            system => system.yCoordinate
        )
    );

    const maxX = Math.max(
        ...systems.map(
            system => system.xCoordinate
        )
    );

    const maxY = Math.max(
        ...systems.map(
            system => system.yCoordinate
        )
    );

    const VIEWBOX_PADDING = 10;

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
                    maxX - minX +
                    VIEWBOX_PADDING * 2
                } ${
                    maxY - minY +
                    VIEWBOX_PADDING * 2
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

                <TradeRouteOverlay
                    trader={selectedTrader}
                    systems={systems}
                />

                <GalaxySystems
                    systems={systems}
                    selectedSystem={selectedSystem}
                    onSystemClick={handleSystemClick}
                />

                <GalaxyTraders
                    traders={traders}
                    systems={systems}
                    selectedTrader={selectedTrader}
                    onTraderClick={setSelectedTrader}
                />
            </svg>

            <div
                style={{
                    display: "flex",
                    flexWrap: "wrap",
                    gap: "1rem",
                    marginTop: "1rem"
                }}
            >
                <SystemPanel
                    system={selectedSystemDetails}
                    markets={markets}
                    loading={loadingMarkets}
                    onPlanetClick={handlePlanetClick}
                />

                <PlanetPanel
                    planet={selectedPlanet}
                />

                <TraderPanel
                    trader={selectedTrader}
                />
            </div>
        </>
    );
}
