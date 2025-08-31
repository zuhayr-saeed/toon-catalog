import { apiClient } from './api';

export interface Rating {
  id: string;
  storyId: string;
  rating: number;
  review?: string;
  createdAt: string;
}

export interface CreateRatingRequest {
  storyId: string;
  rating: number;
  review?: string;
}

export const ratingService = {
  // Get user's rating for a story
  getUserRating: async (storyId: string): Promise<Rating | null> => {
    try {
      return await apiClient.get(`/stories/${storyId}/my-rating`);
    } catch (error: any) {
      if (error.response?.status === 404) {
        return null; // no rating yet
      }
      throw error;
    }
  },

  // Create rating for a story
  create: async (data: CreateRatingRequest): Promise<Rating> => {
    return apiClient.post(`/stories/${data.storyId}/ratings`, data);
  },

  // Update existing rating
  update: async (ratingId: string, rating: number, review?: string): Promise<Rating> => {
    return apiClient.put(`/ratings/${ratingId}`, { rating, review });
  },

  // Delete rating
  delete: async (ratingId: string): Promise<void> => {
    return apiClient.delete(`/ratings/${ratingId}`);
  },
};
