// src/services/authService.ts
import { apiClient } from "./api";

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
}

// --- Local storage management helpers ---
export const saveAuth = (data: AuthResponse) => {
  localStorage.setItem("auth", JSON.stringify(data));
};

export const clearAuth = () => {
  localStorage.removeItem("auth");
};

export const getAuth = (): AuthResponse | null => {
  const json = localStorage.getItem("auth");
  return json ? JSON.parse(json) : null;
};

// --- API calls (direct named exports) ---
export async function login(username: string, password: string) {
  return apiClient.post<AuthResponse>("/v1/auth/login", { username, password });
}

export async function register(username: string, email: string, password: string) {
  return apiClient.post<AuthResponse>("/v1/auth/register", {
    username,
    email,
    password,
  });
}
