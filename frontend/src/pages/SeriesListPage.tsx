import { useCallback, useEffect, useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Box,
  Card,
  CardContent,
  CardMedia,
  Chip,
  CircularProgress,
  Container,
  FormControl,
  InputLabel,
  MenuItem,
  Pagination,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import { seriesService } from '../services/seriesService';

const PAGE_SIZE = 12;

export default function SeriesListPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const page = Number(searchParams.get('page') || '1');
  const q = searchParams.get('q') || '';
  const genre = searchParams.get('genre') || '';
  const tag = searchParams.get('tag') || '';
  const sort = (searchParams.get('sort') || 'newest') as 'top_rated' | 'popular' | 'newest' | 'title';

  const query = useMemo(
    () => ({ page: page - 1, size: PAGE_SIZE, q, genre, tag, sort }),
    [page, q, genre, tag, sort],
  );

  const [searchInput, setSearchInput] = useState(q);

  useEffect(() => {
    setSearchInput(q);
  }, [q]);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['series', query],
    queryFn: () => seriesService.list(query),
  });

  const updateParam = useCallback((key: string, value: string) => {
    const next = new URLSearchParams(searchParams);
    if (!value) {
      next.delete(key);
    } else {
      next.set(key, value);
    }
    next.set('page', '1');
    setSearchParams(next);
  }, [searchParams, setSearchParams]);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      if (searchInput !== q) {
        updateParam('q', searchInput);
      }
    }, 350);

    return () => window.clearTimeout(timeout);
  }, [q, searchInput, updateParam]);

  const onPageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    const next = new URLSearchParams(searchParams);
    next.set('page', String(value));
    setSearchParams(next);
  };

  return (
    <Container sx={{ py: 4 }}>
      <Stack spacing={2} sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight={700}>Browse Series</Typography>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
          <TextField
            label="Search"
            value={searchInput}
            onChange={(event) => setSearchInput(event.target.value)}
            placeholder="Title or synopsis"
            fullWidth
          />
          <TextField
            label="Genre"
            value={genre}
            onChange={(event) => updateParam('genre', event.target.value)}
            placeholder="e.g. Romance"
            sx={{ minWidth: 180 }}
          />
          <TextField
            label="Tag"
            value={tag}
            onChange={(event) => updateParam('tag', event.target.value)}
            placeholder="e.g. Fantasy"
            sx={{ minWidth: 180 }}
          />
          <FormControl sx={{ minWidth: 180 }}>
            <InputLabel id="sort-label">Sort</InputLabel>
            <Select
              labelId="sort-label"
              label="Sort"
              value={sort}
              onChange={(event) => updateParam('sort', event.target.value)}
            >
              <MenuItem value="newest">Newest</MenuItem>
              <MenuItem value="top_rated">Top Rated</MenuItem>
              <MenuItem value="popular">Most Popular</MenuItem>
              <MenuItem value="title">Title A-Z</MenuItem>
            </Select>
          </FormControl>
        </Stack>
      </Stack>

      {isLoading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      )}

      {isError && (
        <Typography color="error">Failed to load series.</Typography>
      )}

      {!isLoading && data && (
        <>
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: {
                xs: '1fr',
                sm: 'repeat(2, minmax(0, 1fr))',
                md: 'repeat(3, minmax(0, 1fr))',
                lg: 'repeat(4, minmax(0, 1fr))',
              },
              gap: 2,
            }}
          >
            {data.content.map((series) => (
              <Card
                key={series.id}
                component={RouterLink}
                to={`/series/${series.id}`}
                sx={{ textDecoration: 'none', color: 'inherit', display: 'flex', flexDirection: 'column' }}
              >
                {series.coverImageUrl ? (
                  <CardMedia component="img" height="220" image={series.coverImageUrl} alt={series.title} />
                ) : (
                  <Box sx={{ height: 220, display: 'grid', placeItems: 'center', bgcolor: 'grey.200', fontSize: 28 }}>
                    {series.title[0]}
                  </Box>
                )}
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h6" noWrap>{series.title}</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }} noWrap>
                    {(series.authors || []).join(', ') || 'Unknown author'}
                  </Typography>
                  <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                    {(series.genres || []).slice(0, 2).map((g) => (
                      <Chip key={g} label={g} size="small" />
                    ))}
                  </Stack>
                  <Typography variant="body2" sx={{ mt: 1 }}>
                    ⭐ {series.avgRating?.toFixed(1) || '0.0'} ({series.ratingCount || 0})
                  </Typography>
                </CardContent>
              </Card>
            ))}
          </Box>

          {data.totalPages > 1 && (
            <Stack alignItems="center" sx={{ mt: 4 }}>
              <Pagination count={data.totalPages} page={page} onChange={onPageChange} color="primary" />
            </Stack>
          )}
        </>
      )}
    </Container>
  );
}
