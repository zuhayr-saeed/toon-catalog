import { Navigate, Route, Routes } from 'react-router-dom';
import { Box } from '@mui/material';
import AppHeader from './components/layout/AppHeader';
import SeriesListPage from './pages/SeriesListPage';
import SeriesDetailPage from './pages/SeriesDetailPage';
import ReaderPage from './pages/ReaderPage';
import LibraryPage from './pages/LibraryPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';

function App() {
  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: 'background.default' }}>
      <AppHeader />
      <Routes>
        <Route path="/" element={<Navigate to="/series" replace />} />
        <Route path="/series" element={<SeriesListPage />} />
        <Route path="/series/:id" element={<SeriesDetailPage />} />
        <Route path="/series/:id/episodes/:episodeId" element={<ReaderPage />} />
        <Route path="/library" element={<LibraryPage />} />
        <Route path="/users/:username" element={<ProfilePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </Box>
  );
}

export default App;
