import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Container,
  Typography,
  Card,
  CardMedia,
  CardContent,
  Stack,
  Chip,
  Pagination,
  CircularProgress,
  Grid,
  Box
} from '@mui/material';
import { apiClient } from '../services/api';
import { Link as RouterLink } from 'react-router-dom';

interface Series {
  id: string;
  title: string;
  type: string;
  synopsis: string;
  coverImageUrl?: string;
  genres: string[];
  tags: string[];
  authors: string[];
  createdAt: string;
}

interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export default function SeriesListPage() {
  const [page, setPage] = useState(1);
  const size = 12;

  const { data, isLoading, error } = useQuery<Page<Series>>({
    queryKey: ['series', page],
    queryFn: () =>
      apiClient.get<Page<Series>>(`/series?page=${page - 1}&size=${size}`),
    keepPreviousData: true,
  });

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress color="primary" />
      </Box>
    );
  }

  if (error) {
    return (
      <Container sx={{ mt: 4 }}>
        <Typography color="error" align="center" variant="h6">
          Failed to load series
        </Typography>
      </Container>
    );
  }

  return (
    <Container sx={{ mt: 6 }}>
      <Typography
        variant="h4"
        gutterBottom
        sx={{
          fontWeight: 700,
          color: 'primary.main',
          textAlign: 'center',
          mb: 6,
        }}
      >
        Browse Series
      </Typography>

      <Grid container spacing={4}>
        {data?.content?.map((s) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={s.id}>
            <Card
              component={RouterLink}
              to={`/series/${s.id}`}
              sx={{
                textDecoration: 'none',
                color: 'inherit',
                borderRadius: 3,
                boxShadow: 4,
                height: '100%',
                transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-6px)',
                  boxShadow: 8,
                },
              }}
            >
              {s.coverImageUrl ? (
                <CardMedia
                  component="img"
                  height="200"
                  image={s.coverImageUrl}
                  alt={s.title}
                  sx={{ objectFit: 'cover' }}
                />
              ) : (
                <Box
                  sx={{
                    height: 200,
                    background: (theme) => `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'white',
                    fontSize: 28,
                    fontWeight: 700,
                  }}
                >
                  {s.title.charAt(0)}
                </Box>
              )}
              <CardContent>
                <Typography variant="h6" gutterBottom noWrap>
                  {s.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" noWrap>
                  {s.authors.join(', ')}
                </Typography>
                <Stack direction="row" spacing={1} mt={1} flexWrap="wrap">
                  {s.genres?.slice(0, 2).map((g) => (
                    <Chip key={g} label={g} color="secondary" size="small" />
                  ))}
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {data?.totalPages > 1 && (
        <Stack mt={6} alignItems="center">
          <Pagination
            count={data.totalPages}
            page={page}
            onChange={(_, val) => setPage(val)}
            color="primary"
            size="large"
          />
        </Stack>
      )}
    </Container>
  );
}
