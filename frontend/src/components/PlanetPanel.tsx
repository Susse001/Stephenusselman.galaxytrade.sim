import type { Planet } from "../types/planet";

interface PlanetPanelProps {
    planet: Planet | null;
}

/**
 * Displays detailed information for the currently selected planet.
 */
export default function PlanetPanel({
    planet
}: PlanetPanelProps) {

    if (!planet) {
        return null;
    }

    return (
        <div
            style={{
                padding: "1rem",
                backgroundColor: "#1f2937",
                color: "white",
                borderRadius: "8px",
                width: "350px",
                maxHeight: "600px",
                overflowY: "auto"
            }}
        >
            <h3>{planet.name}</h3>

            <p>
                <strong>Type:</strong>{" "}
                {planet.planetType}
            </p>

            <p>
                <strong>Orbit:</strong>{" "}
                {planet.orbitZone}
            </p>

            <p>
                <strong>Orbital Position:</strong>{" "}
                {planet.orbitalOrder}
            </p>

            <hr />

            <h4>Population & Development</h4>

            <p>
                <strong>Population:</strong>{" "}
                {planet.population}
            </p>

            <p>
                <strong>Development:</strong>{" "}
                {planet.development}
            </p>

            <p>
                <strong>Infrastructure:</strong>{" "}
                {planet.infrastructure}
            </p>

            <hr />

            <h4>Features</h4>

            {planet.features.length === 0 ? (
                <p>No notable features.</p>
            ) : (
                <ul
                    style={{
                        marginTop: "0.5rem",
                        paddingLeft: "1.25rem"
                    }}
                >
                    {planet.features.map(feature => (
                        <li key={feature}>
                            {feature}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}