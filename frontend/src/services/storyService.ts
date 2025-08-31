import { apiClient } from './api';
import { 
  Story, 
  PaginatedResponse, 
  StoriesQueryParams 
} from '@/types';

export const storyService = {
  // Get paginated list of stories
  getStories: async (params: StoriesQueryParams = {}): Promise<PaginatedResponse<Story>> => {
    const queryParams = new URLSearchParams();
    
    if (params.page !== undefined) queryParams.append('page', params.page.toString());
    if (params.size !== undefined) queryParams.append('size', params.size.toString());
    if (params.genre) queryParams.append('genre', params.genre);
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params.sortDirection) queryParams.append('sortDirection', params.sortDirection);
    
    const queryString = queryParams.toString();
    const url = queryString ? `/stories?${queryString}` : '/stories';
    
    return apiClient.get<PaginatedResponse<Story>>(url);
  },

  // Get single story by ID
  getStoryById: async (id: string): Promise<Story> => {
    return apiClient.get<Story>(`/stories/${id}`);
  },

  // Get available genres (if your backend supports this)
  getGenres: async (): Promise<string[]> => {
    return apiClient.get<string[]>('/stories/genres');
  },
};
