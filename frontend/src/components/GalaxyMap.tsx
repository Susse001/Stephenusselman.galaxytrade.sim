import type { StarSystem } from "../types/system";
import { useState } from "react";

interface GalaxyMapProps {
    systems: StarSystem[];
}

export default function GalaxyMap({
    systems
}: GalaxyMapProps) {

    const [selectedSystem, setSelectedSystem] =
        useState<StarSystem | null>(null);
    return (
        <>
            <svg
                width={800}
                height={600}
                style={{
                    backgroundColor: "#111827",
                    borderRadius: "8px",
                    border: "1px solid #374151"
                }}
            >

            {systems.slice(0, -1).map((system, index) => {

            const nextSystem = systems[index + 1];

            return (
                <line
                    key={`route-${system.id}`}
                    x1={system.xCoordinate}
                    y1={system.yCoordinate}
                    x2={nextSystem.xCoordinate}
                    y2={nextSystem.yCoordinate}
                    stroke="#374151"
                    strokeWidth={2}
                />
            );
        })}
                {systems.map(system => (
                    <g key={system.id}>
                        <circle
                            cx={system.xCoordinate}
                            cy={system.yCoordinate}
                            r={8}
                            fill="#60a5fa"
                            onMouseEnter={() =>
                                setSelectedSystem(system)
                            }
                            style={{
                                cursor: "pointer"
                            }}
                        />

                        <text
                            x={system.xCoordinate + 12}
                            y={system.yCoordinate + 5}
                            fill="white"
                            fontSize="12"
                        >
                            {system.name}
                        </text>
                    </g>
                ))}
            </svg>

            {selectedSystem && (
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
                        ({selectedSystem.xCoordinate},
                        {" "}
                        {selectedSystem.yCoordinate})
                    </p>
                </div>
            )}
        </>
    );
}