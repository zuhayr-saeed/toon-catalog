// src/services/favoriteService.ts
import { apiClient } from "./api";
import type { Series } from "./seriesService";

export const favoriteService = {
  async list(page = 0, size = 20) {
    return apiClient.get<{ content: Series[]; totalPages: number }>(
      `/users/me/favorites?page=${page}&size=${size}`
    );
  },
  async add(seriesId: string) {
    return apiClient.post<void>(`/users/me/favorites/${seriesId}`, {});
  },
  async remove(seriesId: string) {
    return apiClient.delete<void>(`/users/me/favorites/${seriesId}`);
  },
  async isFavorite(seriesId: string) {
    return apiClient.get<boolean>(`/users/me/favorites/${seriesId}/status`);
  },
};
