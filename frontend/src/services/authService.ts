import { apiClient } from './api';
import type { AuthResponse } from '../types';

const AUTH_STORAGE_KEY = 'auth';

export function saveAuth(data: AuthResponse) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(data));
}

export function clearAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}

export function getAuth(): AuthResponse | null {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  return raw ? (JSON.parse(raw) as AuthResponse) : null;
}

export async function login(username: string, password: string): Promise<AuthResponse> {
  const auth = await apiClient.post<AuthResponse>('/auth/login', { username, password });
  saveAuth(auth);
  return auth;
}

export async function register(username: string, email: string, password: string): Promise<AuthResponse> {
  const auth = await apiClient.post<AuthResponse>('/auth/register', { username, email, password });
  saveAuth(auth);
  return auth;
}
