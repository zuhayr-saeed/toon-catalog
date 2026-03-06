import { apiClient } from './api';
import type { FollowDto, UserProfileDto } from '../types';

export const userService = {
  getProfile(username: string): Promise<UserProfileDto> {
    return apiClient.get<UserProfileDto>(`/users/${encodeURIComponent(username)}`);
  },

  getFollowers(username: string): Promise<FollowDto[]> {
    return apiClient.get<FollowDto[]>(`/users/${encodeURIComponent(username)}/followers`);
  },

  getFollowing(username: string): Promise<FollowDto[]> {
    return apiClient.get<FollowDto[]>(`/users/${encodeURIComponent(username)}/following`);
  },

  follow(username: string): Promise<void> {
    return apiClient.post<void>(`/users/${encodeURIComponent(username)}/follow`);
  },

  unfollow(username: string): Promise<void> {
    return apiClient.delete<void>(`/users/${encodeURIComponent(username)}/follow`);
  },

  isFollowing(username: string): Promise<boolean> {
    return apiClient.get<boolean>(`/users/${encodeURIComponent(username)}/follow/status`);
  },
};
