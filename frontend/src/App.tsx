import { Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import AppHeader from "./components/layout/AppHeader";

import SeriesListPage from "./pages/SeriesListPage";
import SeriesDetailPage from "./pages/SeriesDetailPage";
import ReaderPage from "./pages/ReaderPage";
import LibraryPage from "./pages/LibraryPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";

function App() {
  return (
    <AuthProvider>
      <AppHeader />
      <Routes>
        <Route path="/" element={<SeriesListPage />} />
        <Route path="/series" element={<SeriesListPage />} />
        <Route path="/series/:id" element={<SeriesDetailPage />} />
        <Route path="/series/:id/episodes/:episodeId" element={<ReaderPage />} />
        <Route path="/library" element={<LibraryPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
