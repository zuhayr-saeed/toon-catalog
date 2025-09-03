// src/services/authService.ts
import { apiClient } from "./api";

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
}

// --- Local storage management helpers ---
export function saveAuth(data: AuthResponse) {
  localStorage.setItem("auth", JSON.stringify(data));
}

export function clearAuth() {
  localStorage.removeItem("auth");
}

export function getAuth(): AuthResponse | null {  
  const raw = localStorage.getItem("auth");  
  return raw ? JSON.parse(raw) : null;  
}

// --- API calls (direct named exports) ---
export async function login(username: string, password: string) {
  const res = await apiClient.post<AuthResponse>("/auth/login", { username, password });  
  localStorage.setItem("auth", JSON.stringify(res));  
  return res;
}

export async function register(username: string, email: string, password: string) {
  return apiClient.post<AuthResponse>("/auth/register", {
    username,
    email,
    password,
  });
}
