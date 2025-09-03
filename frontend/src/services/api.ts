// src/services/api.ts
import { getAuth } from "./authService";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api/v1";

export const apiClient = {
  async get<T>(url: string): Promise<T> {
    const auth = getAuth();
    const res = await fetch(`${API_URL}${url}`, {
      headers: {
        "Content-Type": "application/json",
        ...(auth?.token ? { Authorization: `Bearer ${auth.token}` } : {}),
      },
    });
    if (!res.ok) throw new Error(res.status.toString());
    try {
      return (await res.json()) as T;
    } catch {
      return {} as T;
    }
  },

  async post<T>(url: string, body?: any): Promise<T> {
    const auth = getAuth();
    const res = await fetch(`${API_URL}${url}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(auth?.token ? { Authorization: `Bearer ${auth.token}` } : {}),
      },
      body: body ? JSON.stringify(body) : undefined, // ✅ allow no-body
    });
    if (!res.ok) throw new Error(res.status.toString());
    try {
      return (await res.json()) as T;
    } catch {
      return {} as T;
    }
  },

  async delete<T>(url: string): Promise<T> {
    const auth = getAuth();
    const res = await fetch(`${API_URL}${url}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        ...(auth?.token ? { Authorization: `Bearer ${auth.token}` } : {}),
      },
    });
    if (!res.ok) throw new Error(res.status.toString());
    try {
      return (await res.json()) as T;
    } catch {
      return {} as T;
    }
  },
};