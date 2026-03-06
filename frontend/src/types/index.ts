export type ReadingStatus = 'READING' | 'COMPLETED' | 'ON_HOLD' | 'DROPPED' | 'PLAN_TO_READ';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
}

export interface SeriesDto {
  id: string;
  title: string;
  type: string;
  synopsis: string | null;
  coverImageUrl: string | null;
  genres: string[];
  tags: string[];
  authors: string[];
  createdAt: string;
  avgRating: number;
  ratingCount: number;
}

export interface ListEntryDto {
  id: string;
  userId: string;
  username: string;
  seriesId: string;
  seriesTitle: string;
  seriesCoverImageUrl: string | null;
  status: ReadingStatus;
  progress: number;
  favorite: boolean;
  userScore: number | null;
  lastUpdated: string;
}

export interface ListEntryUpsertRequest {
  status?: ReadingStatus;
  progress?: number;
  favorite?: boolean;
}

export interface RatingDto {
  id: string;
  seriesId: string;
  username: string;
  score: number;
  review: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface RatingSummary {
  seriesId: string;
  avg: number;
  count: number;
}

export interface EpisodeDto {
  id: string;
  seriesId: string;
  number: number;
  title: string;
  releaseDate: string | null;
}

export interface FollowDto {
  username: string;
  followedAt: string;
}

export interface UserProfileDto {
  username: string;
  joinedAt: string;
  followersCount: number;
  followingCount: number;
  favoritesCount: number;
  statusCounts: Record<ReadingStatus, number>;
  followedByMe: boolean;
}
