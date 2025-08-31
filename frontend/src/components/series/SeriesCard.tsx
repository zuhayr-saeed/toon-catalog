import React from 'react';
import { Card, CardContent, CardMedia, Typography, Chip, Stack } from '@mui/material';
import type { Series } from '../../services/seriesService';
import { Link as RouterLink } from 'react-router-dom';

export default function SeriesCard({ item }: { item: Series }) {
  const cover = item.coverImageUrl || 'https://via.placeholder.com/400x225?text=No+Cover';
  return (
    <Card
      component={RouterLink}
      to={`/series/${item.id}`}
      sx={{ textDecoration: 'none' }}
      elevation={2}
    >
      <CardMedia component="img" height="180" image={cover} alt={item.title} />
      <CardContent>
        <Typography variant="h6" fontWeight={700} gutterBottom noWrap>
          {item.title}
        </Typography>
        <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap', gap: 1 }}>
          {item.genres?.slice(0, 3).map((g) => (
            <Chip key={g} size="small" label={g} />
          ))}
        </Stack>
      </CardContent>
    </Card>
  );
}
