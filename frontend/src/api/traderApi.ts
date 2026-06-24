import axios from "axios";
import type { Trader } from "../types/trader";

const API_URL =
    "http://localhost:8080/api/traders";

export async function getTraders() {

    const response =
        await axios.get<Trader[]>(
            API_URL
        );

    return response.data;
}