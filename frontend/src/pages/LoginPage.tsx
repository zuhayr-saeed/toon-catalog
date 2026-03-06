import { useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const onSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError('');
    try {
      await login(username.trim(), password);
      navigate('/series');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    }
  };

  return (
    <Container maxWidth="sm" sx={{ py: 6 }}>
      <Card>
        <CardContent>
          <Typography variant="h5" fontWeight={700} sx={{ mb: 2 }}>Sign In</Typography>
          <Box component="form" onSubmit={onSubmit}>
            <Stack spacing={2}>
              {error && <Alert severity="error">{error}</Alert>}
              <TextField
                label="Username"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                required
              />
              <TextField
                label="Password"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
              />
              <Button type="submit" variant="contained" disabled={isLoading}>
                {isLoading ? 'Signing In...' : 'Sign In'}
              </Button>
              <Typography variant="body2">
                No account yet? <RouterLink to="/register">Create one</RouterLink>
              </Typography>
            </Stack>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
}
