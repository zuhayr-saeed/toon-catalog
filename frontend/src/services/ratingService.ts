import { apiClient } from "./api";

export interface Rating {
  id: string;
  storyId: string;
  rating: number;
  review?: string;
  createdAt: string;
}

export const ratingService = {
  getUserRating: async (seriesId: string): Promise<Rating | null> => {
    try {
      return await apiClient.get<Rating>(`/ratings/${seriesId}/me`);
    } catch (err: any) {
      if (err.message?.includes("204") || err.message?.includes("404")) return null;
      throw err;
    }
  },

  create: async (seriesId: string, rating: number, review?: string): Promise<Rating> => {
    return apiClient.post(`/ratings/${seriesId}`, { rating, review });
  },

  delete: async (seriesId: string): Promise<void> => {
    return apiClient.delete(`/ratings/${seriesId}`);
  },
};