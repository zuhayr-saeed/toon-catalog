import React from 'react';
import { Container, Box, Typography, Button } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

export default function HomePage() {
  return (
    <Container sx={{ py: 6 }}>
      <Box sx={{ textAlign: 'center', mb: 4 }}>
        <Typography variant="h3" fontWeight={800} gutterBottom>
          Discover, rate, and review webtoons
        </Typography>

        <Typography color="text.secondary" sx={{ maxWidth: 800, mx: 'auto', mb: 3 }}>
          A modern catalog to explore series, track what you’re reading, and share ratings.
        </Typography>

        <Button component={RouterLink} to="/series" size="large" variant="contained" color="primary">
          Browse Series
        </Button>
      </Box>
    </Container>
  );
}
