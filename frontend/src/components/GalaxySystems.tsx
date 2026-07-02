import type { StarSystem } from "../types/starSystem";

const regionColors: Record<string, string> = {
    CORE: "#60a5fa",
    INNER_RIM: "#34d399",
    OUTER_RIM: "#f59e0b"
};

interface GalaxySystemsProps {
    systems: StarSystem[];
    selectedSystem: StarSystem | null;
    onSystemClick: (system: StarSystem) => void;
}

/**
 * Renders every star system in the galaxy map.
 */
export default function GalaxySystems({
    systems,
    selectedSystem,
    onSystemClick
}: GalaxySystemsProps) {

    return (
        <>
            {systems.map(system => (
                <g key={system.id}>

                    {selectedSystem?.id === system.id && (
                        <circle
                            cx={system.xCoordinate}
                            cy={system.yCoordinate}
                            r={2}
                            fill="none"
                            stroke="white"
                            strokeWidth={0.2}
                        />
                    )}

                    <circle
                        cx={system.xCoordinate}
                        cy={system.yCoordinate}
                        r={1}
                        fill={
                            regionColors[system.region]
                            ?? "#9ca3af"
                        }
                        onClick={() => onSystemClick(system)}
                        style={{ cursor: "pointer" }}
                    />

                </g>
            ))}
        </>
    );
}