// Base types matching your Spring Boot backend entities

export interface User {
  id: string;
  email: string;
  roles: string[];
  createdAt: string;
}

export interface Story {
  id: string;
  title: string;
  author: string;
  genre: string;
  description: string;
  coverImage: string;
  createdAt: string;
  averageRating?: number;
  totalRatings?: number;
}

export interface Rating {
  id: string;
  user: User;
  story: Story;
  rating: number; // 1-5
  review?: string;
  createdAt: string;
}

// API Response types
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // current page (0-based)
  first: boolean;
  last: boolean;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

// Authentication types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  confirmPassword: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface RefreshTokenResponse {
  accessToken: string;
}

// Rating/Review types
export interface CreateRatingRequest {
  storyId: string;
  rating: number;
  review?: string;
}

export interface UpdateRatingRequest {
  rating: number;
  review?: string;
}

// Query parameters
export interface StoriesQueryParams {
  page?: number;
  size?: number;
  genre?: string;
  sortBy?: 'title' | 'author' | 'createdAt' | 'averageRating';
  sortDirection?: 'asc' | 'desc';
}

export interface RatingsQueryParams {
  page?: number;
  size?: number;
  sortBy?: 'rating' | 'createdAt';
  sortDirection?: 'asc' | 'desc';
}

// Error types
export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
  path: string;
}
