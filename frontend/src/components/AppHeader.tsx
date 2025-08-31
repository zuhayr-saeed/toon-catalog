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
    navigate("/");
  };

  return (
    <AppBar
      position="sticky"
      elevation={4}
      sx={{
        background: (theme) =>
          `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
        color: "white",
      }}
    >
      <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
        {/* Logo */}
        <Typography
          component={RouterLink}
          to="/"
          sx={{
            textDecoration: "none",
            fontWeight: 700,
            fontSize: "1.25rem",
            color: "white",
          }}
        >
          ToonCatalog
        </Typography>

        {/* Desktop Nav */}
        <Box sx={{ display: { xs: "none", md: "flex" }, gap: 3 }}>
          <Button component={RouterLink} to="/" sx={{ color: "white" }}>
            Home
          </Button>
          <Button component={RouterLink} to="/series" sx={{ color: "white" }}>
            Series
          </Button>
        </Box>

        {/* User Section */}
        {user ? (
          <>
            <Button
              onClick={(e) => setAnchorEl(e.currentTarget)}
              sx={{ color: "white" }}
              startIcon={
                <Avatar sx={{ bgcolor: "rgba(255,255,255,0.3)" }}>
                  {user.username.charAt(0).toUpperCase()}
                </Avatar>
              }
            >
              {user.username}
            </Button>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={() => setAnchorEl(null)}
              PaperProps={{
                sx: { mt: 1, borderRadius: 2 },
              }}
            >
              <MenuItem disabled>{user.email}</MenuItem>
              <MenuItem sx={{ color: "error.main" }} onClick={handleLogout}>
                Sign Out
              </MenuItem>
            </Menu>
          </>
        ) : (
          <Box sx={{ display: { xs: "none", md: "flex" }, gap: 2 }}>
            <Button component={RouterLink} to="/login" variant="outlined" sx={{ color: "white", borderColor: "white" }}>
              Sign In
            </Button>
            <Button component={RouterLink} to="/register" variant="contained" color="secondary">
              Sign Up
            </Button>
          </Box>
        )}

        {/* Mobile Menu */}
        <IconButton
          edge="end"
          sx={{ display: { xs: "flex", md: "none" }, color: "white" }}
          onClick={(e) => setMobileMenu(e.currentTarget)}
        >
          <MenuIcon />
        </IconButton>
        <Menu
          anchorEl={mobileMenu}
          open={Boolean(mobileMenu)}
          onClose={() => setMobileMenu(null)}
          PaperProps={{ sx: { mt: 1, borderRadius: 2 } }}
        >
          <MenuItem component={RouterLink} to="/">Home</MenuItem>
          <MenuItem component={RouterLink} to="/series">Series</MenuItem>
          {!user && (
            <>
              <MenuItem component={RouterLink} to="/login">Sign In</MenuItem>
              <MenuItem component={RouterLink} to="/register">Sign Up</MenuItem>
            </>
          )}
          {user && <MenuItem onClick={handleLogout} sx={{ color: "error.main" }}>Sign Out</MenuItem>}
        </Menu>
      </Toolbar>
    </AppBar>
  );
}
