import { Link as RouterLink, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Container,
  Stack,
  Typography,
} from '@mui/material';
import { chapterService } from '../services/chapterService';
import { listService } from '../services/listService';
import { ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function ReaderPage() {
  const { id, episodeId } = useParams<{ id: string; episodeId: string }>();
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const episodeQuery = useQuery({
    queryKey: ['episode', id, episodeId],
    queryFn: () => chapterService.get(id!, episodeId!),
    enabled: Boolean(id && episodeId),
  });

  const allEpisodesQuery = useQuery({
    queryKey: ['episodes-all', id],
    queryFn: () => chapterService.list(id!, 0, 500),
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

  const markReadMutation = useMutation({
    mutationFn: async () => {
      const episode = episodeQuery.data;
      const episodes = allEpisodesQuery.data?.content || [];
      if (!episode) return;

      const highestEpisodeNumber = episodes.length > 0
        ? Math.max(...episodes.map((item) => item.number))
        : episode.number;

      const status = episode.number >= highestEpisodeNumber ? 'COMPLETED' : 'READING';

      await listService.upsert(id!, {
        status,
        progress: episode.number,
        favorite: listEntryQuery.data?.favorite ?? false,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-list-entry', id] });
      queryClient.invalidateQueries({ queryKey: ['my-library'] });
    },
  });

  if (episodeQuery.isLoading) {
    return <Box sx={{ display: 'grid', placeItems: 'center', py: 8 }}><CircularProgress /></Box>;
  }

  if (episodeQuery.isError || !episodeQuery.data) {
    return (
      <Container sx={{ py: 4 }}>
        <Alert severity="error">Failed to load episode.</Alert>
      </Container>
    );
  }

  const currentEpisode = episodeQuery.data;
  const orderedEpisodes = (allEpisodesQuery.data?.content || []).slice().sort((a, b) => a.number - b.number);
  const currentIndex = orderedEpisodes.findIndex((item) => item.id === currentEpisode.id);
  const previousEpisode = currentIndex > 0 ? orderedEpisodes[currentIndex - 1] : null;
  const nextEpisode = currentIndex >= 0 && currentIndex < orderedEpisodes.length - 1 ? orderedEpisodes[currentIndex + 1] : null;

  return (
    <Container sx={{ py: 4 }}>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
        <Button component={RouterLink} to={`/series/${id}`} variant="outlined">Back to Series</Button>
        <Typography variant="h6">Episode {currentEpisode.number}: {currentEpisode.title || 'Untitled'}</Typography>
      </Stack>

      <Box sx={{ p: 3, border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
        <Typography variant="body1" sx={{ mb: 1 }}>Episode Number: {currentEpisode.number}</Typography>
        <Typography variant="body1" sx={{ mb: 1 }}>Title: {currentEpisode.title || 'Untitled'}</Typography>
        <Typography variant="body1" sx={{ mb: 2 }}>
          Release Date: {currentEpisode.releaseDate ? new Date(currentEpisode.releaseDate).toLocaleDateString() : 'TBA'}
        </Typography>

        {user ? (
          <Button variant="contained" onClick={() => markReadMutation.mutate()}>
            Mark This Episode as Read
          </Button>
        ) : (
          <Alert severity="info">Sign in to track reading progress.</Alert>
        )}
      </Box>

      <Stack direction="row" justifyContent="space-between" sx={{ mt: 4 }}>
        {previousEpisode ? (
          <Button component={RouterLink} to={`/series/${id}/episodes/${previousEpisode.id}`}>Previous</Button>
        ) : <span />}

        {nextEpisode ? (
          <Button component={RouterLink} to={`/series/${id}/episodes/${nextEpisode.id}`}>Next</Button>
        ) : (
          <Button disabled>Next</Button>
        )}
      </Stack>
    </Container>
  );
}
