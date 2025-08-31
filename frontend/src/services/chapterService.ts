// src/services/chapterService.ts
import { apiClient } from "./api";

export interface Episode {
  id: string;
  number: number;
  title: string;
  releaseDate: string;
}

export interface EpisodeDetail extends Episode {
  images: string[]; // webtoon panels or manga pages
}

export const chapterService = {
  async list(seriesId: string, page = 0, size = 20) {
    return apiClient.get<{ content: Episode[]; totalPages: number }>(
      `/series/${seriesId}/episodes?page=${page}&size=${size}`
    );
  },
  async get(seriesId: string, episodeId: string) {
    return apiClient.get<EpisodeDetail>(
      `/series/${seriesId}/episodes/${episodeId}`
    );
  },
};
