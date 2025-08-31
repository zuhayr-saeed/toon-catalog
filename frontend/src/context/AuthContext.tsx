// src/context/AuthContext.tsx
import React, { createContext, useContext, useEffect, useState } from "react";
import type { AuthResponse } from "../services/authService";
import {
  login as loginApi,
  register as registerApi,
  saveAuth,
  clearAuth,
  getAuth,
} from "../services/authService";

interface AuthContextType {
  user: AuthResponse | null;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // Load user from localStorage on mount
  useEffect(() => {
    const storedAuth = getAuth();
    if (storedAuth) {
      setUser(storedAuth);
    }
  }, []);

  // --- Login ---
  const login = async (username: string, password: string) => {
    setIsLoading(true);
    try {
      const res = await loginApi(username, password);
      saveAuth(res);
      setUser(res);
    } finally {
      setIsLoading(false);
    }
  };

  // --- Register ---
  const register = async (username: string, email: string, password: string) => {
    setIsLoading(true);
    try {
      const res = await registerApi(username, email, password);
      saveAuth(res);
      setUser(res);
    } finally {
      setIsLoading(false);
    }
  };

  // --- Logout ---
  const logout = () => {
    clearAuth();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};

// --- Hook for easier usage ---
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within an AuthProvider");
  return context;
};
