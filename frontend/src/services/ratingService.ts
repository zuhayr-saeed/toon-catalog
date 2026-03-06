import { ApiError, apiClient } from './api';
import type { RatingDto, RatingSummary } from '../types';

export const ratingService = {
  async getUserRating(seriesId: string): Promise<RatingDto | null> {
    try {
      const rating = await apiClient.get<RatingDto | null>(`/ratings/${seriesId}/me`);
      return rating;
    } catch (error) {
      if (error instanceof ApiError && (error.status === 204 || error.status === 404)) {
        return null;
      }
      throw error;
    }
  },

  async save(seriesId: string, score: number, review?: string): Promise<RatingDto> {
    return apiClient.post<RatingDto>(`/ratings/${seriesId}`, { score, review });
  },

  async delete(seriesId: string): Promise<void> {
    return apiClient.delete<void>(`/ratings/${seriesId}`);
  },

  async getSummary(seriesId: string): Promise<RatingSummary> {
    return apiClient.get<RatingSummary>(`/ratings/${seriesId}/summary`);
  },
};
