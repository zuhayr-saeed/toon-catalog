import React from "react";
import { useParams, Link as RouterLink } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import {
  Box,
  Container,
  Typography,
  CircularProgress,
  Button,
  Stack,
} from "@mui/material";
import { chapterService } from "../services/chapterService";

export default function ReaderPage() {
  const { id, episodeId } = useParams<{ id: string; episodeId: string }>();

  const { data, isLoading, error } = useQuery({
    queryKey: ["episode", id, episodeId],
    queryFn: () => chapterService.get(id!, episodeId!),
    enabled: !!id && !!episodeId,
  });

  if (isLoading) {
    return <Box sx={{ display: "flex", justifyContent: "center", mt: 6 }}><CircularProgress /></Box>;
  }
  if (error) {
    return <Typography color="error">Failed to load episode.</Typography>;
  }

  const ep = data!;

  return (
    <Container sx={{ mt: 4 }}>
      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={2}>
        <Button component={RouterLink} to={`/series/${id}`} variant="outlined">← Back to Series</Button>
        <Typography variant="h6" fontWeight={600}>Episode {ep.number}: {ep.title}</Typography>
      </Stack>

      <Box>
        {ep.images.map((img, idx) => (
          <Box key={idx} sx={{ mb: 2 }}>
            <img
              src={img}
              alt={`Page ${idx + 1}`}
              style={{ width: "100%", borderRadius: 8 }}
            />
          </Box>
        ))}
      </Box>

      <Stack direction="row" justifyContent="space-between" mt={4}>
        <Button
          component={RouterLink}
          to={`/series/${id}/episodes/${Number(ep.number) - 1}`}
          disabled={ep.number <= 1}
        >
          ← Previous
        </Button>
        <Button
          component={RouterLink}
          to={`/series/${id}/episodes/${Number(ep.number) + 1}`}
        >
          Next →
        </Button>
      </Stack>
    </Container>
  );
}
