import axios from "axios";

/**
 * Shared Axios instance used for communicating with the backend API.
 */
export const api = axios.create({
    baseURL: "http://localhost:8080/api"
});
