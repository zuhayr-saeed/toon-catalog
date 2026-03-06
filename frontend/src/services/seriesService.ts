import { apiClient } from './api';
import type { Page, SeriesDto, RatingDto } from '../types';

export interface SeriesQuery {
  page?: number;
  size?: number;
  q?: string;
  genre?: string;
  tag?: string;
  sort?: 'top_rated' | 'popular' | 'newest' | 'title';
}

export const seriesService = {
  async list(params: SeriesQuery = { page: 0, size: 12 }): Promise<Page<SeriesDto>> {
    const search = new URLSearchParams();
    if (params.page !== undefined) search.set('page', String(params.page));
    if (params.size !== undefined) search.set('size', String(params.size));
    if (params.q) search.set('q', params.q);
    if (params.genre) search.set('genre', params.genre);
    if (params.tag) search.set('tag', params.tag);
    if (params.sort) search.set('sort', params.sort);

    const query = search.toString();
    return apiClient.get<Page<SeriesDto>>(query ? `/series?${query}` : '/series');
  },

  async get(id: string): Promise<SeriesDto> {
    return apiClient.get<SeriesDto>(`/series/${id}`);
  },

  async getReviews(seriesId: string, page = 0, size = 10): Promise<Page<RatingDto>> {
    return apiClient.get<Page<RatingDto>>(`/series/${seriesId}/reviews?page=${page}&size=${size}&sort=createdAt,desc`);
  },
};
