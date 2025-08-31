import React, { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  Container,
  Typography,
  Grid,
  Card,
  CardMedia,
  CardContent,
  CircularProgress,
  Pagination,
  Stack,
} from "@mui/material";
import { favoriteService } from "../services/favoriteService";
import { Link as RouterLink } from "react-router-dom";
import type { Series } from "../services/seriesService";

export default function LibraryPage() {
  const [page, setPage] = useState(1);
  const { data, isLoading, error } = useQuery({
    queryKey: ["favorites", page],
    queryFn: () => favoriteService.list(page - 1, 12),
  });

  if (isLoading) return <CircularProgress />;
  if (error) return <Typography color="error">Failed to load library</Typography>;

  return (
    <Container sx={{ mt: 6 }}>
      <Typography variant="h4" gutterBottom fontWeight={700}>
        My Library
      </Typography>
      <Grid container spacing={3}>
        {data?.content?.map((s: Series) => (
          <Grid item xs={12} sm={6} md={3} key={s.id}>
            <Card
              component={RouterLink}
              to={`/series/${s.id}`}
              sx={{ textDecoration: "none", color: "inherit" }}
            >
              <CardMedia
                component="img"
                height="180"
                image={s.coverImageUrl}
                alt={s.title}
              />
              <CardContent>
                <Typography variant="h6" noWrap>
                  {s.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" noWrap>
                  {s.authors.join(", ")}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      {data?.totalPages > 1 && (
        <Stack mt={4} alignItems="center">
          <Pagination
            count={data.totalPages}
            page={page}
            onChange={(_, val) => setPage(val)}
          />
        </Stack>
      )}
    </Container>
  );
}
