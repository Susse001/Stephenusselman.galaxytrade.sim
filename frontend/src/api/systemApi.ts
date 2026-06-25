import { api } from "./api";
import type { StarSystem } from "../types/starSystem";

export async function getSystems(): Promise<StarSystem[]> {
    const response = await api.get<StarSystem[]>("/systems");
    return response.data;
}