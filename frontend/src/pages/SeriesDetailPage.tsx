import { useEffect, useMemo, useState } from 'react';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Alert,
  Box,
  Button,
  Card,
  CardMedia,
  Chip,
  CircularProgress,
  Container,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import { seriesService } from '../services/seriesService';
import { listService } from '../services/listService';
import { favoriteService } from '../services/favoriteService';
import { chapterService } from '../services/chapterService';
import { ratingService } from '../services/ratingService';
import { ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import type { ListEntryUpsertRequest, ReadingStatus } from '../types';

const STATUSES: ReadingStatus[] = ['PLAN_TO_READ', 'READING', 'COMPLETED', 'ON_HOLD', 'DROPPED'];

export default function SeriesDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [episodesPage, setEpisodesPage] = useState(1);
  const [reviewsPage, setReviewsPage] = useState(1);

  const seriesQuery = useQuery({
    queryKey: ['series', id],
    queryFn: () => seriesService.get(id!),
    enabled: Boolean(id),
  });

  const listEntryQuery = useQuery({
    queryKey: ['my-list-entry', id],
    queryFn: async () => {
      try {
        return await listService.getMyListEntry(id!);
      } catch (error) {
        if (error instanceof ApiError && error.status === 404) {
          return null;
        }
        throw error;
      }
    },
    enabled: Boolean(id && user),
    retry: false,
  });

  const summaryQuery = useQuery({
    queryKey: ['rating-summary', id],
    queryFn: () => ratingService.getSummary(id!),
    enabled: Boolean(id),
  });

  const myRatingQuery = useQuery({
    queryKey: ['my-rating', id],
    queryFn: () => ratingService.getUserRating(id!),
    enabled: Boolean(id && user),
  });

  const reviewsQuery = useQuery({
    queryKey: ['reviews', id, reviewsPage],
    queryFn: () => seriesService.getReviews(id!, reviewsPage - 1, 5),
    enabled: Boolean(id),
  });

  const episodesQuery = useQuery({
    queryKey: ['episodes', id, episodesPage],
    queryFn: () => chapterService.list(id!, episodesPage - 1, 20),
    enabled: Boolean(id),
  });

  const [status, setStatus] = useState<ReadingStatus>('PLAN_TO_READ');
  const [progress, setProgress] = useState(0);
  const [favorite, setFavorite] = useState(false);

  useEffect(() => {
    if (listEntryQuery.data) {
      setStatus(listEntryQuery.data.status);
      setProgress(listEntryQuery.data.progress);
      setFavorite(listEntryQuery.data.favorite);
    } else {
      setStatus('PLAN_TO_READ');
      setProgress(0);
      setFavorite(false);
    }
  }, [listEntryQuery.data]);

  const [score, setScore] = useState(0);
  const [review, setReview] = useState('');

  useEffect(() => {
    if (myRatingQuery.data) {
      setScore(myRatingQuery.data.score);
      setReview(myRatingQuery.data.review || '');
    }
  }, [myRatingQuery.data]);

  const saveListMutation = useMutation({
    mutationFn: (payload: ListEntryUpsertRequest) => listService.upsert(id!, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-list-entry', id] });
      queryClient.invalidateQueries({ queryKey: ['my-library'] });
    },
  });

  const toggleFavoriteMutation = useMutation({
    mutationFn: async () => {
      if (favorite) {
        await favoriteService.remove(id!);
      } else {
        await favoriteService.add(id!);
      }
    },
    onSuccess: () => {
      setFavorite((prev) => !prev);
      queryClient.invalidateQueries({ queryKey: ['my-list-entry', id] });
      queryClient.invalidateQueries({ queryKey: ['my-library'] });
    },
  });

  const saveRatingMutation = useMutation({
    mutationFn: () => ratingService.save(id!, score, review),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-rating', id] });
      queryClient.invalidateQueries({ queryKey: ['rating-summary', id] });
      queryClient.invalidateQueries({ queryKey: ['reviews', id] });
    },
  });

  const deleteRatingMutation = useMutation({
    mutationFn: () => ratingService.delete(id!),
    onSuccess: () => {
      setScore(0);
      setReview('');
      queryClient.invalidateQueries({ queryKey: ['my-rating', id] });
      queryClient.invalidateQueries({ queryKey: ['rating-summary', id] });
      queryClient.invalidateQueries({ queryKey: ['reviews', id] });
    },
  });

  const series = seriesQuery.data;

  const infoChips = useMemo(() => (series?.genres || []).slice(0, 4), [series]);

  if (seriesQuery.isLoading) {
    return <Box sx={{ display: 'grid', placeItems: 'center', py: 8 }}><CircularProgress /></Box>;
  }

  if (seriesQuery.isError || !series) {
    return (
      <Container sx={{ py: 4 }}>
        <Alert severity="error">Failed to load series.</Alert>
      </Container>
    );
  }

  return (
    <Container sx={{ py: 4 }}>
      <Button component={RouterLink} to="/series" sx={{ mb: 2 }}>Back to Series</Button>

      <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '300px 1fr' }, gap: 3 }}>
        <Card>
          <CardMedia
            component="img"
            image={series.coverImageUrl || 'https://via.placeholder.com/400x600?text=No+Cover'}
            alt={series.title}
            sx={{ height: 420, objectFit: 'cover' }}
          />
        </Card>

        <Stack spacing={2}>
          <Typography variant="h4" fontWeight={700}>{series.title}</Typography>
          <Typography color="text.secondary">{(series.authors || []).join(', ') || 'Unknown author'}</Typography>
          <Typography>{series.synopsis || 'No synopsis yet.'}</Typography>

          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            {infoChips.map((chip) => <Chip key={chip} label={chip} />)}
          </Stack>

          <Typography>
            Overall: ⭐ {summaryQuery.data?.avg?.toFixed(1) || '0.0'} ({summaryQuery.data?.count || 0} ratings)
          </Typography>

          {user ? (
            <Stack spacing={2} sx={{ p: 2, border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
              <Typography variant="h6">My List</Typography>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <FormControl sx={{ minWidth: 180 }}>
                  <InputLabel id="status-label">Status</InputLabel>
                  <Select
                    labelId="status-label"
                    label="Status"
                    value={status}
                    onChange={(event) => setStatus(event.target.value as ReadingStatus)}
                  >
                    {STATUSES.map((item) => (
                      <MenuItem key={item} value={item}>{item}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
                <TextField
                  type="number"
                  label="Progress"
                  value={progress}
                  onChange={(event) => setProgress(Number(event.target.value || 0))}
                  inputProps={{ min: 0 }}
                />
                <Button
                  variant="contained"
                  onClick={() => saveListMutation.mutate({ status, progress, favorite })}
                >
                  Save List Entry
                </Button>
                <IconButton color={favorite ? 'error' : 'default'} onClick={() => toggleFavoriteMutation.mutate()}>
                  {favorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
                </IconButton>
              </Stack>
            </Stack>
          ) : (
            <Alert severity="info">Sign in to manage your list, favorites, and rating.</Alert>
          )}

          {user && (
            <Stack spacing={2} sx={{ p: 2, border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
              <Typography variant="h6">My Rating</Typography>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <TextField
                  type="number"
                  label="Score (1-10)"
                  value={score}
                  onChange={(event) => setScore(Number(event.target.value || 0))}
                  inputProps={{ min: 1, max: 10 }}
                  sx={{ maxWidth: 180 }}
                />
                <TextField
                  label="Review"
                  multiline
                  minRows={2}
                  value={review}
                  onChange={(event) => setReview(event.target.value)}
                  fullWidth
                />
              </Stack>
              <Stack direction="row" spacing={1}>
                <Button
                  variant="contained"
                  disabled={score < 1 || score > 10}
                  onClick={() => saveRatingMutation.mutate()}
                >
                  Save Rating
                </Button>
                <Button color="error" onClick={() => deleteRatingMutation.mutate()}>Delete</Button>
              </Stack>
            </Stack>
          )}
        </Stack>
      </Box>

      <Box sx={{ mt: 5 }}>
        <Typography variant="h5" fontWeight={600} sx={{ mb: 2 }}>Episodes</Typography>
        {episodesQuery.isLoading ? (
          <CircularProgress size={22} />
        ) : (
          <Stack spacing={1}>
            {(episodesQuery.data?.content || []).map((episode) => (
              <Button
                key={episode.id}
                component={RouterLink}
                to={`/series/${series.id}/episodes/${episode.id}`}
                variant="outlined"
                sx={{ justifyContent: 'space-between' }}
              >
                <span>Episode {episode.number}: {episode.title || 'Untitled'}</span>
                <span>{episode.releaseDate || 'TBA'}</span>
              </Button>
            ))}
          </Stack>
        )}
        {(episodesQuery.data?.totalPages || 0) > 1 && (
          <Pagination
            sx={{ mt: 2 }}
            page={episodesPage}
            count={episodesQuery.data?.totalPages || 1}
            onChange={(_, value) => setEpisodesPage(value)}
          />
        )}
      </Box>

      <Box sx={{ mt: 5 }}>
        <Typography variant="h5" fontWeight={600} sx={{ mb: 2 }}>Reviews</Typography>
        <Stack spacing={2}>
          {(reviewsQuery.data?.content || []).map((item) => (
            <Box key={item.id} sx={{ border: '1px solid', borderColor: 'divider', borderRadius: 2, p: 2 }}>
              <Typography fontWeight={600}>{item.username} · {new Date(item.createdAt).toLocaleDateString()}</Typography>
              <Typography variant="body2" sx={{ mb: 1 }}>Score: {item.score}/10</Typography>
              <Typography>{item.review}</Typography>
            </Box>
          ))}
          {!reviewsQuery.isLoading && (reviewsQuery.data?.content || []).length === 0 && (
            <Typography color="text.secondary">No written reviews yet.</Typography>
          )}
        </Stack>

        {(reviewsQuery.data?.totalPages || 0) > 1 && (
          <Pagination
            sx={{ mt: 2 }}
            page={reviewsPage}
            count={reviewsQuery.data?.totalPages || 1}
            onChange={(_, value) => setReviewsPage(value)}
          />
        )}
      </Box>
    </Container>
  );
}
