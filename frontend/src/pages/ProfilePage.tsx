import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Alert,
  Box,
  Button,
  Card,
  CardMedia,
  Chip,
  Container,
  Pagination,
  Stack,
  Tab,
  Tabs,
  Typography,
} from '@mui/material';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { listService } from '../services/listService';
import type { ReadingStatus } from '../types';

const STATUS_TABS: Array<ReadingStatus | 'ALL' | 'FAVORITES'> = ['ALL', 'READING', 'COMPLETED', 'ON_HOLD', 'DROPPED', 'PLAN_TO_READ', 'FAVORITES'];

export default function ProfilePage() {
  const { username } = useParams<{ username: string }>();
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [statusTab, setStatusTab] = useState<ReadingStatus | 'ALL' | 'FAVORITES'>('ALL');
  const [page, setPage] = useState(1);

  const profileQuery = useQuery({
    queryKey: ['profile', username],
    queryFn: () => userService.getProfile(username!),
    enabled: Boolean(username),
  });

  const listQuery = useQuery({
    queryKey: ['public-list', username, statusTab, page],
    queryFn: () => listService.getPublicList(username!, {
      page: page - 1,
      size: 20,
      status: statusTab === 'ALL' || statusTab === 'FAVORITES' ? undefined : statusTab,
      favorite: statusTab === 'FAVORITES' ? true : undefined,
    }),
    enabled: Boolean(username),
  });

  const followMutation = useMutation({
    mutationFn: async () => {
      const profile = profileQuery.data;
      if (!profile) return;
      if (profile.followedByMe) {
        await userService.unfollow(username!);
      } else {
        await userService.follow(username!);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['profile', username] });
    },
  });

  if (profileQuery.isLoading) {
    return (
      <Container sx={{ py: 4 }}>
        <Typography>Loading profile...</Typography>
      </Container>
    );
  }

  if (profileQuery.isError || !profileQuery.data) {
    return (
      <Container sx={{ py: 4 }}>
        <Alert severity="error">Profile not found.</Alert>
      </Container>
    );
  }

  const profile = profileQuery.data;
  const canFollow = Boolean(user && user.username !== profile.username);

  return (
    <Container sx={{ py: 4 }}>
      <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" spacing={2} sx={{ mb: 3 }}>
        <Box>
          <Typography variant="h4" fontWeight={700}>{profile.username}</Typography>
          <Typography color="text.secondary">Joined {new Date(profile.joinedAt).toLocaleDateString()}</Typography>
          <Stack direction="row" spacing={1} sx={{ mt: 1 }}>
            <Chip label={`${profile.followersCount} followers`} />
            <Chip label={`${profile.followingCount} following`} />
            <Chip label={`${profile.favoritesCount} favorites`} color="error" />
          </Stack>
        </Box>

        {canFollow && (
          <Button variant={profile.followedByMe ? 'outlined' : 'contained'} onClick={() => followMutation.mutate()}>
            {profile.followedByMe ? 'Unfollow' : 'Follow'}
          </Button>
        )}
      </Stack>

      <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap sx={{ mb: 2 }}>
        {Object.entries(profile.statusCounts).map(([key, count]) => (
          <Chip key={key} label={`${key.replace('_', ' ')}: ${count}`} />
        ))}
      </Stack>

      <Tabs
        value={statusTab}
        onChange={(_, value) => {
          setStatusTab(value);
          setPage(1);
        }}
        variant="scrollable"
        sx={{ mb: 2 }}
      >
        {STATUS_TABS.map((tab) => (
          <Tab key={tab} value={tab} label={tab === 'ALL' ? 'All' : tab.replace('_', ' ')} />
        ))}
      </Tabs>

      <Stack spacing={2}>
        {(listQuery.data?.content || []).map((entry) => (
          <Card key={entry.id} sx={{ p: 2 }}>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems={{ md: 'center' }}>
              <CardMedia
                component={RouterLink}
                to={`/series/${entry.seriesId}`}
                image={entry.seriesCoverImageUrl || 'https://via.placeholder.com/100x140?text=No+Cover'}
                sx={{ width: 90, height: 130, borderRadius: 1, flexShrink: 0 }}
              />
              <Box sx={{ flexGrow: 1 }}>
                <Typography component={RouterLink} to={`/series/${entry.seriesId}`} sx={{ textDecoration: 'none', color: 'text.primary' }}>
                  <strong>{entry.seriesTitle}</strong>
                </Typography>
                <Typography variant="body2" color="text.secondary">Status: {entry.status}</Typography>
                <Typography variant="body2" color="text.secondary">Progress: {entry.progress}</Typography>
                <Typography variant="body2" color="text.secondary">Score: {entry.userScore ?? 'N/A'}</Typography>
              </Box>
              {entry.favorite && <Chip color="error" label="Favorite" />}
            </Stack>
          </Card>
        ))}
      </Stack>

      {!listQuery.isLoading && (listQuery.data?.content || []).length === 0 && (
        <Typography color="text.secondary">No public entries for this filter.</Typography>
      )}

      {(listQuery.data?.totalPages || 0) > 1 && (
        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
          <Pagination
            page={page}
            count={listQuery.data?.totalPages || 1}
            onChange={(_, value) => setPage(value)}
          />
        </Box>
      )}
    </Container>
  );
}
