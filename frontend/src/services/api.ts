import { getAuth } from './authService';

const API_URL = import.meta.env.VITE_API_URL || '/api/v1';

export class ApiError extends Error {
  status: number;
  body: unknown;

  constructor(status: number, message: string, body: unknown) {
    super(message);
    this.status = status;
    this.body = body;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const auth = getAuth();
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(auth?.token ? { Authorization: `Bearer ${auth.token}` } : {}),
    ...(init?.headers || {}),
  };

  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers,
  });

  if (response.status === 204) {
    return null as T;
  }

  const text = await response.text();
  const body = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message = (body && typeof body === 'object' && 'detail' in body)
      ? String((body as { detail: unknown }).detail)
      : response.statusText;
    throw new ApiError(response.status, message, body);
  }

  return body as T;
}

export const apiClient = {
  get: <T>(path: string) => request<T>(path, { method: 'GET' }),
  post: <T>(path: string, body?: unknown) => request<T>(path, {
    method: 'POST',
    body: body ? JSON.stringify(body) : undefined,
  }),
  put: <T>(path: string, body?: unknown) => request<T>(path, {
    method: 'PUT',
    body: body ? JSON.stringify(body) : undefined,
  }),
  delete: <T>(path: string) => request<T>(path, { method: 'DELETE' }),
};
