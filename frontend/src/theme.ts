// src/theme.ts
import { createTheme, responsiveFontSizes } from '@mui/material/styles';

const PURPLE = '#7c3aed';
const PURPLE_LIGHT = '#a78bfa';
const PURPLE_DARK = '#5b21b6';

const CYAN = '#06b6d4';
const CYAN_DARK = '#0e7490';

const ERROR = '#ef4444';
const WARNING = '#f59e0b';
const SUCCESS = '#10b981';
const INFO = '#3b82f6';

let theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: PURPLE, light: PURPLE_LIGHT, dark: PURPLE_DARK, contrastText: '#fff' },
    secondary: { main: CYAN, dark: CYAN_DARK, contrastText: '#fff' },
    error: { main: ERROR },
    warning: { main: WARNING },
    success: { main: SUCCESS },
    info: { main: INFO },
    background: { default: '#fafafa', paper: '#fff' },
    text: {
      primary: '#374151',
      secondary: '#6b7280',
    },
    divider: 'rgba(0,0,0,0.08)',
  },
  shape: { borderRadius: 12 },
  typography: {
    fontFamily: `'Inter', 'Roboto', 'Helvetica', 'Arial', sans-serif`,
    h1: { fontWeight: 800, fontSize: '3rem' },
    h2: { fontWeight: 700, fontSize: '2.25rem' },
    h3: { fontWeight: 700, fontSize: '1.75rem' },
    h4: { fontWeight: 600, fontSize: '1.5rem' },
    h5: { fontWeight: 600, fontSize: '1.25rem' },
    h6: { fontWeight: 600, fontSize: '1.125rem' },
    button: { textTransform: 'none', fontWeight: 600, letterSpacing: 0.2 },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          fontWeight: 600,
          padding: '8px 20px',
          transition: 'all 0.2s ease-in-out',
          '&:hover': {
            transform: 'translateY(-2px)',
            boxShadow: '0 6px 12px rgba(0,0,0,0.1)',
          },
        },
        containedPrimary: {
          // ✅ Apply gradient via background
          background: `linear-gradient(135deg, ${PURPLE} 0%, ${CYAN} 100%)`,
          color: '#fff',
          '&:hover': {
            background: `linear-gradient(135deg, ${PURPLE_DARK} 0%, ${CYAN_DARK} 100%)`,
          },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: 'rgba(255,255,255,0.9)',
          backdropFilter: 'blur(10px)',
          boxShadow: '0 4px 6px rgba(0,0,0,0.05)',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          transition: 'all 0.2s ease',
          boxShadow: '0 6px 12px rgba(0,0,0,0.06)',
          '&:hover': {
            transform: 'translateY(-4px)',
            boxShadow: '0 12px 24px rgba(0,0,0,0.12)',
          },
        },
      },
    },
  },
});

theme = responsiveFontSizes(theme);
export { theme };
