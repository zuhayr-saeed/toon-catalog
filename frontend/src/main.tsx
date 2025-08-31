import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';

import { ThemeProvider, CssBaseline } from '@mui/material';
import { theme } from './theme';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context/AuthContext';
import { BrowserRouter } from 'react-router-dom';  // ✅ add this

const queryClient = new QueryClient();

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AuthProvider>
          <BrowserRouter>   {/* ✅ wrap here */}
            <App />
          </BrowserRouter>
        </AuthProvider>
      </ThemeProvider>
    </QueryClientProvider>
  </React.StrictMode>
);
