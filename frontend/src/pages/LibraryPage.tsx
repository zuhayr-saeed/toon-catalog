import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Alert,
  Box,
  Card,
  CardMedia,
  Chip,
  Container,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  Stack,
  Tab,
  Tabs,
  TextField,
  Typography,
} from '@mui/material';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import { Link as RouterLink } from 'react-router-dom';
import { listService } from '../services/listService';
import { favoriteService } from '../services/favoriteService';
import { useAuth } from '../context/AuthContext';
import type { ListEntryDto, ReadingStatus } from '../types';

const STATUS_TABS: Array<ReadingStatus | 'ALL'> = ['ALL', 'READING', 'COMPLETED', 'ON_HOLD', 'DROPPED', 'PLAN_TO_READ'];

export default function LibraryPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [page, setPage] = useState(1);
  const [status, setStatus] = useState<ReadingStatus | 'ALL'>('ALL');
  const [favoriteOnly, setFavoriteOnly] = useState(false);

  const listQuery = useQuery({
    queryKey: ['my-library', page, status, favoriteOnly],
    queryFn: () => listService.getMyList({
      page: page - 1,
      size: 20,
      status: status === 'ALL' ? undefined : status,
      favorite: favoriteOnly ? true : undefined,
    }),
    enabled: Boolean(user),
  });

  const updateMutation = useMutation({
    mutationFn: ({ seriesId, payload }: { seriesId: string; payload: { status?: ReadingStatus; progress?: number; favorite?: boolean } }) =>
      listService.upsert(seriesId, payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['my-library'] }),
  });

  const toggleFavoriteMutation = useMutation({
    mutationFn: ({ seriesId, favorite }: { seriesId: string; favorite: boolean }) =>
      favorite ? favoriteService.remove(seriesId) : favoriteService.add(seriesId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['my-library'] }),
  });

  if (!user) {
    return (
      <Container sx={{ py: 4 }}>
        <Alert severity="info">
          Sign in to view your library.
        </Alert>
      </Container>
    );
  }

  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 2 }}>My List</Typography>

      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems={{ md: 'center' }} sx={{ mb: 2 }}>
        <Tabs
          value={status}
          onChange={(_, value) => {
            setStatus(value);
            setPage(1);
          }}
          variant="scrollable"
        >
          {STATUS_TABS.map((tab) => (
            <Tab key={tab} value={tab} label={tab === 'ALL' ? 'All' : tab.replace('_', ' ')} />
          ))}
        </Tabs>

        <Chip
          label={favoriteOnly ? 'Favorites only' : 'All entries'}
          color={favoriteOnly ? 'error' : 'default'}
          onClick={() => {
            setFavoriteOnly((prev) => !prev);
            setPage(1);
          }}
        />
      </Stack>

      <Stack spacing={2}>
        {(listQuery.data?.content || []).map((entry) => (
          <LibraryItem
            key={entry.id}
            entry={entry}
            onUpdate={(payload) => updateMutation.mutate({ seriesId: entry.seriesId, payload })}
            onToggleFavorite={() => toggleFavoriteMutation.mutate({ seriesId: entry.seriesId, favorite: entry.favorite })}
          />
        ))}
      </Stack>

      {!listQuery.isLoading && (listQuery.data?.content || []).length === 0 && (
        <Typography color="text.secondary" sx={{ mt: 2 }}>
          No items for this filter yet.
        </Typography>
      )}

      {(listQuery.data?.totalPages || 0) > 1 && (
        <Stack alignItems="center" sx={{ mt: 3 }}>
          <Pagination
            page={page}
            count={listQuery.data?.totalPages || 1}
            onChange={(_, value) => setPage(value)}
          />
        </Stack>
      )}
    </Container>
  );
}

function LibraryItem({
  entry,
  onUpdate,
  onToggleFavorite,
}: {
  entry: ListEntryDto;
  onUpdate: (payload: { status?: ReadingStatus; progress?: number; favorite?: boolean }) => void;
  onToggleFavorite: () => void;
}) {
  return (
    <Card sx={{ p: 2 }}>
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems={{ md: 'center' }}>
        <CardMedia
          component={RouterLink}
          to={`/series/${entry.seriesId}`}
          image={entry.seriesCoverImageUrl || 'https://via.placeholder.com/120x160?text=No+Cover'}
          sx={{ width: 100, height: 140, borderRadius: 1, flexShrink: 0 }}
        />

        <Box sx={{ flexGrow: 1 }}>
          <Typography component={RouterLink} to={`/series/${entry.seriesId}`} sx={{ textDecoration: 'none', color: 'text.primary' }}>
            <strong>{entry.seriesTitle}</strong>
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Last updated: {new Date(entry.lastUpdated).toLocaleString()}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Your score: {entry.userScore ?? 'N/A'}
          </Typography>
        </Box>

        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} alignItems={{ sm: 'center' }}>
          <FormControl sx={{ minWidth: 170 }}>
            <InputLabel id={`status-${entry.id}`}>Status</InputLabel>
            <Select
              labelId={`status-${entry.id}`}
              value={entry.status}
              label="Status"
              onChange={(event) => onUpdate({ status: event.target.value as ReadingStatus })}
            >
              <MenuItem value="PLAN_TO_READ">PLAN TO READ</MenuItem>
              <MenuItem value="READING">READING</MenuItem>
              <MenuItem value="COMPLETED">COMPLETED</MenuItem>
              <MenuItem value="ON_HOLD">ON HOLD</MenuItem>
              <MenuItem value="DROPPED">DROPPED</MenuItem>
            </Select>
          </FormControl>

          <TextField
            type="number"
            size="small"
            label="Progress"
            value={entry.progress}
            inputProps={{ min: 0 }}
            onChange={(event) => onUpdate({ progress: Number(event.target.value || 0) })}
            sx={{ width: 110 }}
          />

          <IconButton onClick={onToggleFavorite} color={entry.favorite ? 'error' : 'default'}>
            {entry.favorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
          </IconButton>
        </Stack>
      </Stack>
    </Card>
  );
}
