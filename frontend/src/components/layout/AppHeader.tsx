import { useEffect, useMemo, useState } from 'react';
import {
  AppBar,
  Avatar,
  Box,
  Button,
  IconButton,
  InputBase,
  Menu,
  MenuItem,
  Toolbar,
  Typography,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import SearchIcon from '@mui/icons-material/Search';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function AppHeader() {
  const { user, logout } = useAuth();
  const [mobileMenuEl, setMobileMenuEl] = useState<null | HTMLElement>(null);
  const [userMenuEl, setUserMenuEl] = useState<null | HTMLElement>(null);
  const navigate = useNavigate();
  const location = useLocation();

  const initialSearchValue = useMemo(() => {
    const params = new URLSearchParams(location.search);
    return params.get('q') || '';
  }, [location.search]);

  const [searchValue, setSearchValue] = useState(initialSearchValue);

  useEffect(() => {
    setSearchValue(initialSearchValue);
  }, [initialSearchValue]);

  const submitSearch = () => {
    const params = new URLSearchParams();
    if (searchValue.trim()) {
      params.set('q', searchValue.trim());
    }
    navigate(`/series${params.toString() ? `?${params.toString()}` : ''}`);
    setMobileMenuEl(null);
  };

  const handleLogout = () => {
    logout();
    setUserMenuEl(null);
    navigate('/series');
  };

  return (
    <AppBar position="sticky" color="inherit" elevation={1}>
      <Toolbar sx={{ display: 'flex', gap: 2, justifyContent: 'space-between' }}>
        <Typography
          component={RouterLink}
          to="/series"
          sx={{
            textDecoration: 'none',
            fontSize: '1.2rem',
            fontWeight: 700,
            color: 'text.primary',
            whiteSpace: 'nowrap',
          }}
        >
          Webtoon Catalog
        </Typography>

        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            border: '1px solid',
            borderColor: 'divider',
            borderRadius: 999,
            px: 1,
            py: 0.25,
            width: '100%',
            maxWidth: 460,
            backgroundColor: 'background.paper',
          }}
        >
          <InputBase
            placeholder="Search title or synopsis"
            value={searchValue}
            onChange={(event) => setSearchValue(event.target.value)}
            onKeyDown={(event) => {
              if (event.key === 'Enter') {
                submitSearch();
              }
            }}
            sx={{ ml: 1, flex: 1 }}
          />
          <IconButton onClick={submitSearch} size="small" aria-label="search">
            <SearchIcon fontSize="small" />
          </IconButton>
        </Box>

        <Box sx={{ display: { xs: 'none', md: 'flex' }, alignItems: 'center', gap: 1 }}>
          <Button component={RouterLink} to="/series" color="inherit">Series</Button>
          {user && <Button component={RouterLink} to="/library" color="inherit">My List</Button>}
          {user && (
            <Button component={RouterLink} to={`/users/${user.username}`} color="inherit">
              Profile
            </Button>
          )}
          {!user && (
            <>
              <Button component={RouterLink} to="/login" variant="outlined">Sign In</Button>
              <Button component={RouterLink} to="/register" variant="contained">Sign Up</Button>
            </>
          )}
          {user && (
            <Button
              onClick={(event) => setUserMenuEl(event.currentTarget)}
              startIcon={<Avatar sx={{ width: 28, height: 28 }}>{user.username[0]?.toUpperCase()}</Avatar>}
            >
              {user.username}
            </Button>
          )}
        </Box>

        <IconButton sx={{ display: { xs: 'flex', md: 'none' } }} onClick={(event) => setMobileMenuEl(event.currentTarget)}>
          <MenuIcon />
        </IconButton>

        <Menu anchorEl={mobileMenuEl} open={Boolean(mobileMenuEl)} onClose={() => setMobileMenuEl(null)}>
          <MenuItem component={RouterLink} to="/series" onClick={() => setMobileMenuEl(null)}>Series</MenuItem>
          {user && <MenuItem component={RouterLink} to="/library" onClick={() => setMobileMenuEl(null)}>My List</MenuItem>}
          {user && (
            <MenuItem component={RouterLink} to={`/users/${user.username}`} onClick={() => setMobileMenuEl(null)}>
              Profile
            </MenuItem>
          )}
          {!user && <MenuItem component={RouterLink} to="/login" onClick={() => setMobileMenuEl(null)}>Sign In</MenuItem>}
          {!user && <MenuItem component={RouterLink} to="/register" onClick={() => setMobileMenuEl(null)}>Sign Up</MenuItem>}
        </Menu>

        <Menu anchorEl={userMenuEl} open={Boolean(userMenuEl)} onClose={() => setUserMenuEl(null)}>
          <MenuItem component={RouterLink} to={`/users/${user?.username}`} onClick={() => setUserMenuEl(null)}>
            {user?.email}
          </MenuItem>
          <MenuItem onClick={handleLogout}>Sign Out</MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
}
