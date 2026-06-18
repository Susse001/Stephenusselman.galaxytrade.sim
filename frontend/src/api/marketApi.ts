import axios from "axios";
import type { Market } from "../types/market";

const API_URL =
    "http://localhost:8080/api/markets";

export async function getMarketsForSystem(
    systemId: number
) {
    const response =
        await axios.get<Market[]>(
            `${API_URL}/system/${systemId}`
        );

    return response.data;
}