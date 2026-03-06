import { apiClient } from './api';
import type { ListEntryDto, Page } from '../types';

export const favoriteService = {
  list(page = 0, size = 20) {
    return apiClient.get<Page<ListEntryDto>>(`/users/me/favorites?page=${page}&size=${size}`);
  },

  add(seriesId: string) {
    return apiClient.post<void>(`/users/me/favorites/${seriesId}`);
  },

  remove(seriesId: string) {
    return apiClient.delete<void>(`/users/me/favorites/${seriesId}`);
  },

  isFavorite(seriesId: string) {
    return apiClient.get<boolean>(`/users/me/favorites/${seriesId}/status`);
  },
};
