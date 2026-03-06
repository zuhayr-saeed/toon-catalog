import { apiClient } from './api';
import type { ListEntryDto, ListEntryUpsertRequest, Page, ReadingStatus } from '../types';

export interface MyListQuery {
  page?: number;
  size?: number;
  status?: ReadingStatus;
  favorite?: boolean;
  sort?: string;
}

export const listService = {
  getMyList(params: MyListQuery = {}): Promise<Page<ListEntryDto>> {
    const search = new URLSearchParams();
    if (params.page !== undefined) search.set('page', String(params.page));
    if (params.size !== undefined) search.set('size', String(params.size));
    if (params.status) search.set('status', params.status);
    if (params.favorite !== undefined) search.set('favorite', String(params.favorite));
    search.set('sort', params.sort || 'lastUpdated,desc');
    return apiClient.get<Page<ListEntryDto>>(`/users/me/list?${search.toString()}`);
  },

  getMyListEntry(seriesId: string): Promise<ListEntryDto> {
    return apiClient.get<ListEntryDto>(`/users/me/list/${seriesId}`);
  },

  upsert(seriesId: string, payload: ListEntryUpsertRequest): Promise<ListEntryDto> {
    return apiClient.put<ListEntryDto>(`/users/me/list/${seriesId}`, payload);
  },

  remove(seriesId: string): Promise<void> {
    return apiClient.delete<void>(`/users/me/list/${seriesId}`);
  },

  getPublicList(username: string, params: MyListQuery = {}): Promise<Page<ListEntryDto>> {
    const search = new URLSearchParams();
    if (params.page !== undefined) search.set('page', String(params.page));
    if (params.size !== undefined) search.set('size', String(params.size));
    if (params.status) search.set('status', params.status);
    if (params.favorite !== undefined) search.set('favorite', String(params.favorite));
    search.set('sort', params.sort || 'lastUpdated,desc');
    return apiClient.get<Page<ListEntryDto>>(`/users/${encodeURIComponent(username)}/list?${search.toString()}`);
  },
};
