import React, { useState } from "react";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Menu,
  MenuItem,
  Box,
  Avatar,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import { useAuth } from "../../context/AuthContext";

export default function AppHeader() {
  const { user, logout } = useAuth();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [mobileMenu, setMobileMenu] = useState<null | HTMLElement>(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    setAnchorEl(null);
    setMobileMenu(null);
    navigate("/");
  };

  return (
    <AppBar position="sticky" color="inherit" elevation={2}>
      <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
        {/* Logo */}
        <Typography
          component={RouterLink}
          to="/"
          sx={{
            textDecoration: "none",
            fontWeight: 700,
            fontSize: "1.25rem",
            background: (theme) =>
              `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
            WebkitBackgroundClip: "text",
            WebkitTextFillColor: "transparent",
          }}
        >
          ToonCatalog
        </Typography>

        {/* Desktop Nav */}
        <Box sx={{ display: { xs: "none", md: "flex" }, gap: 2 }}>
          <Button component={RouterLink} to="/" color="inherit">
            Home
          </Button>
          <Button component={RouterLink} to="/series" color="inherit">
            Series
          </Button>
          {user && (
            <Button component={RouterLink} to="/library" color="inherit">
              My Library
            </Button>
          )}
        </Box>

        {/* User Section */}
        {user ? (
          <>
            <Button
              onClick={(e) => setAnchorEl(e.currentTarget)}
              startIcon={
                <Avatar sx={{ bgcolor: "primary.main" }}>
                  {(user?.username?.charAt(0) ??
                    user?.email?.charAt(0) ??
                    "U").toUpperCase()}
                </Avatar>
              }
              sx={{ ml: 2 }}
            >
              {user?.username || user?.email}
            </Button>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={() => setAnchorEl(null)}
            >
              {user?.email && <MenuItem disabled>{user.email}</MenuItem>}
              <MenuItem onClick={handleLogout} sx={{ color: "error.main" }}>
                Sign Out
              </MenuItem>
            </Menu>
          </>
        ) : (
          <Box sx={{ display: { xs: "none", md: "flex" }, gap: 2 }}>
            <Button
              component={RouterLink}
              to="/login"
              color="primary"
              variant="outlined"
            >
              Sign In
            </Button>
            <Button component={RouterLink} to="/register" variant="contained">
              Sign Up
            </Button>
          </Box>
        )}

        {/* Mobile Menu */}
        <IconButton
          edge="end"
          sx={{ display: { xs: "flex", md: "none" } }}
          onClick={(e) => setMobileMenu(e.currentTarget)}
        >
          <MenuIcon />
        </IconButton>
        <Menu
          anchorEl={mobileMenu}
          open={Boolean(mobileMenu)}
          onClose={() => setMobileMenu(null)}
        >
          <MenuItem component={RouterLink} to="/">
            Home
          </MenuItem>
          <MenuItem component={RouterLink} to="/series">
            Series
          </MenuItem>
          {user && (
            <MenuItem component={RouterLink} to="/library">
              My Library
            </MenuItem>
          )}
          {!user &&
            [
              <MenuItem key="login" component={RouterLink} to="/login">
                Sign In
              </MenuItem>,
              <MenuItem key="register" component={RouterLink} to="/register">
                Sign Up
              </MenuItem>,
            ]}
          {user && (
            <MenuItem onClick={handleLogout} sx={{ color: "error.main" }}>
              Sign Out
            </MenuItem>
          )}
        </Menu>
      </Toolbar>
    </AppBar>
  );
}
