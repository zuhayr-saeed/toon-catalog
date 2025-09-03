// src/services/chapterService.ts
import { apiClient } from "./api";

// Type for an Episode (chapter in a series)
export interface Episode {
  id: string;
  number: number;
  title: string;
  releaseDate: string;
}

export interface EpisodePage {
  content: Episode[];
  totalPages: number;
}

// Service for interacting with episode endpoints
export const chapterService = {
  // List episodes for a series with pagination
  list: (seriesId: string, page = 0, size = 10): Promise<EpisodePage> =>
    apiClient.get(`/series/${seriesId}/episodes?page=${page}&size=${size}`),
};
