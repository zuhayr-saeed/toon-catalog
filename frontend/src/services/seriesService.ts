import { apiClient } from './api';

export interface Series {
  id: string;
  title: string;
  type: 'WEBTOON' | 'WEBNOVEL' | string;
  synopsis: string;
  coverImageUrl?: string;
  genres: string[];
  tags: string[];
  authors: string[];
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface SeriesQuery {
  page?: number;
  size?: number;
}

export const seriesService = {
  list: async (params: SeriesQuery = { page: 0, size: 12 }): Promise<Page<Series>> => {
    const search = new URLSearchParams();
    if (params.page !== undefined) search.set('page', String(params.page));
    if (params.size !== undefined) search.set('size', String(params.size));
    const qs = search.toString();
    const url = qs ? `/series?${qs}` : '/series';
    return apiClient.get<Page<Series>>(url);
  },
  get: async (id: string): Promise<Series> => apiClient.get<Series>(`/series/${id}`),
};
