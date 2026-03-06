import { apiClient } from './api';
import type { EpisodeDto, Page } from '../types';

export const chapterService = {
  list(seriesId: string, page = 0, size = 20): Promise<Page<EpisodeDto>> {
    return apiClient.get<Page<EpisodeDto>>(`/series/${seriesId}/episodes?page=${page}&size=${size}`);
  },

  get(seriesId: string, episodeId: string): Promise<EpisodeDto> {
    return apiClient.get<EpisodeDto>(`/series/${seriesId}/episodes/${episodeId}`);
  },
};
