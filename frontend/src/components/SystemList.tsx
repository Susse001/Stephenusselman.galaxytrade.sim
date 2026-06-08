import { useEffect, useState } from "react";
import { getSystems } from "../api/systemApi";
import type { StarSystem } from "../types/system";

export default function SystemList() {

    const [systems, setSystems] = useState<StarSystem[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        getSystems()
            .then(setSystems)
            .catch(() => setError("Failed to load systems"))
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return <p>Loading systems...</p>;
    }

    if (error) {
    return <p>{error}</p>;
    }

    return (
        <div>
            <h2>Star Systems</h2>

            <ul>
                {systems.map(system => (
                    <li key={system.id}>
                        {system.name} ({system.region})
                    </li>
                ))}
            </ul>
        </div>
    );
}
