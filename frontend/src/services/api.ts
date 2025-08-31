const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

export const apiClient = {
  async get<T>(url: string): Promise<T> {
    const token = localStorage.getItem('token');
    const res = await fetch(`${API_URL}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
    });
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    return data as T;
  },

  async post<T>(url: string, body: any): Promise<T> {
    const token = localStorage.getItem('token');
    const res = await fetch(`${API_URL}${url}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(body),
    });
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    return data as T;
  },

  async put<T>(url: string, body: any): Promise<T> {
    const token = localStorage.getItem('token');
    const res = await fetch(`${API_URL}${url}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: JSON.stringify(body),
    });
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    return data as T;
  },

  async delete<T>(url: string): Promise<T> {
    const token = localStorage.getItem('token');
    const res = await fetch(`${API_URL}${url}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
    });
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    return data as T;
  },
};
