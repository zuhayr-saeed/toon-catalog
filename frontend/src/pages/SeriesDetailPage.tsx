import React, { useState, useEffect } from "react";
import { useParams, Link as RouterLink } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Container,
  Typography,
  CircularProgress,
  Card,
  CardMedia,
  Stack,
  Chip,
  Button,
  Grid,
  Rating,
  IconButton,
  Tooltip,
  Box,
  Pagination,
} from "@mui/material";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import FavoriteIcon from "@mui/icons-material/Favorite";
import { apiClient } from "../services/api";
import { ratingService } from "../services/ratingService";
import type { Rating as RatingType } from "../services/ratingService"; // ✅ type-only
import { favoriteService } from "../services/favoriteService";
import { chapterService } from "../services/chapterService";
import type { Episode } from "../services/chapterService"; // ✅ type-only

interface Series {
  id: string;
  title: string;
  synopsis: string;
  coverImageUrl?: string;
  genres: string[];
  tags: string[];
  authors: string[];
  createdAt: string;
}

export default function SeriesDetailPage() {
  const { id } = useParams<{ id: string }>();
  const queryClient = useQueryClient();

  // --- Series ---
  const seriesQuery = useQuery<Series>({
    queryKey: ["series", id],
    queryFn: () => apiClient.get<Series>(`/series/${id}`),
    enabled: !!id,
  });

  // --- Rating ---
  const ratingQuery = useQuery<RatingType | null>({
    queryKey: ["rating", id],
    queryFn: async () => {
      try {
        return await ratingService.getUserRating(id!);
      } catch {
        return null;
      }
    },
    enabled: !!id,
  });

  const [userRating, setUserRating] = useState<number | null>(null);
  useEffect(() => setUserRating(ratingQuery.data?.rating ?? null), [ratingQuery.data]);

  const createRating = useMutation({
    mutationFn: (rating: number) => ratingService.create(id!, rating),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["rating", id] }),
  });

  const deleteRating = useMutation({
    mutationFn: () => ratingService.delete(id!),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["rating", id] }),
  });

  const handleRatingChange = (newValue: number | null) => {
    if (!newValue) return;
    setUserRating(newValue);
    createRating.mutate(newValue); // ✅ Only rating (seriesId in path, rating in body)
  };

  const handleRemoveRating = () => {
    setUserRating(null);
    deleteRating.mutate();
  };

  // --- Favorites ---
  const favoriteQuery = useQuery({
    queryKey: ["favoriteStatus", id],
    queryFn: () => favoriteService.isFavorite(id!),
    enabled: !!id,
  });

  const addFavorite = useMutation({
    mutationFn: () => favoriteService.add(id!), // ✅ no-body POST
    onSuccess: () => queryClient.invalidateQueries(["favoriteStatus", id]),
  });

  const removeFavorite = useMutation({
    mutationFn: () => favoriteService.remove(id!),
    onSuccess: () => queryClient.invalidateQueries(["favoriteStatus", id]),
  });

  // --- Episodes ---
  const [page, setPage] = useState(1);
  const { data: episodesPage, isLoading: episodesLoading } = useQuery({
    queryKey: ["episodes", id, page],
    queryFn: () => chapterService.list(id!, page - 1, 10),
    enabled: !!id,
  });

  if (seriesQuery.isLoading) return <CircularProgress />;
  if (seriesQuery.error)
    return <Typography color="error">Failed to load series.</Typography>;

  return (
    <Container sx={{ mt: 6 }}>
      <Button component={RouterLink} to="/series" variant="text" sx={{ mb: 3 }}>
        ← Back to Series
      </Button>

      <Grid container spacing={5}>
        <Grid item xs={12} md={4}>
          <Card sx={{ borderRadius: 3, boxShadow: 5 }}>
            <CardMedia
              component="img"
              height="400"
              sx={{ objectFit: "cover" }}
              image={
                seriesQuery.data?.coverImageUrl ||
                "https://via.placeholder.com/400x600?text=No+Cover"
              }
              alt={seriesQuery.data?.title}
            />
          </Card>
        </Grid>

        <Grid item xs={12} md={8}>
          {/* Title + Authors */}
          <Typography variant="h4" fontWeight={700} gutterBottom color="primary">
            {seriesQuery.data?.title}
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" gutterBottom>
            {seriesQuery.data?.authors.join(", ")}
          </Typography>
          <Typography variant="body1" paragraph>
            {seriesQuery.data?.synopsis}
          </Typography>

          {/* Genres & Tags */}
          <Stack direction="row" spacing={1} mt={2} flexWrap="wrap">
            {seriesQuery.data?.genres.map((g) => (
              <Chip key={g} label={g} color="primary" variant="outlined" />
            ))}
          </Stack>
          <Stack direction="row" spacing={1} mt={1} flexWrap="wrap">
            {seriesQuery.data?.tags.map((t) => (
              <Chip key={t} label={t} size="small" color="secondary" />
            ))}
          </Stack>

          {/* Rating */}
          <Stack direction="row" alignItems="center" spacing={2} mt={4}>
            <Typography>Your Rating:</Typography>
            <Rating
              name="series-rating"
              value={userRating || 0}
              onChange={(_, newValue) => handleRatingChange(newValue)}
            />
            {userRating && <Typography>{userRating} / 5</Typography>}
            {ratingQuery.data && (
              <Button onClick={handleRemoveRating} color="error" size="small">
                Remove
              </Button>
            )}
          </Stack>

          {/* Favorites */}
          <Stack direction="row" alignItems="center" spacing={2} mt={3}>
            <Tooltip
              title={
                favoriteQuery.data ? "Remove from Favorites" : "Add to Favorites"
              }
            >
              <IconButton
                color={favoriteQuery.data ? "error" : "default"}
                onClick={() => {
                  if (favoriteQuery.data) removeFavorite.mutate();
                  else addFavorite.mutate();
                }}
              >
                {favoriteQuery.data ? <FavoriteIcon /> : <FavoriteBorderIcon />}
              </IconButton>
            </Tooltip>
            <Typography>
              {favoriteQuery.data ? "In Favorites" : "Mark as Favorite"}
            </Typography>
          </Stack>

          {/* Episodes */}
          <Box mt={5}>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              Episodes
            </Typography>
            {episodesLoading ? (
              <CircularProgress size={24} />
            ) : (
              <Stack spacing={1}>
                {episodesPage?.content?.map((ep: Episode) => (
                  <Button
                    key={ep.id}
                    component={RouterLink}
                    to={`/series/${id}/episodes/${ep.id}`}
                    variant="outlined"
                    sx={{ justifyContent: "flex-start" }}
                  >
                    Episode {ep.number}: {ep.title} ({ep.releaseDate})
                  </Button>
                ))}
              </Stack>
            )}
            {episodesPage?.totalPages > 1 && (
              <Pagination
                sx={{ mt: 2 }}
                count={episodesPage.totalPages}
                page={page}
                onChange={(_, val) => setPage(val)}
              />
            )}
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
}