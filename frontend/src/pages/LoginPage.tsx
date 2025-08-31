// src/pages/LoginPage.tsx
import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const { login, isLoading, user } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (user) navigate("/");
  }, [user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    
    if (!username.trim() || !password.trim()) {
      setError("Please fill in all fields");
      return;
    }

    try {
      await login(username.trim(), password);
      navigate("/");
    } catch (err: any) {
      setError(err.message || "Login failed");
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Welcome Back</h1>
          <p style={styles.subtitle}>Sign in to your account</p>
        </div>

        <form onSubmit={handleSubmit} style={styles.form}>
          {error && (
            <div style={styles.errorAlert}>
              <span style={styles.errorIcon}>⚠️</span>
              {error}
            </div>
          )}

          <div style={styles.inputGroup}>
            <label style={styles.label}>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              style={styles.input}
              placeholder="Enter your username"
              disabled={isLoading}
              autoComplete="username"
            />
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Password</label>
            <div style={styles.passwordContainer}>
              <input
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={styles.passwordInput}
                placeholder="Enter your password"
                disabled={isLoading}
                autoComplete="current-password"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={styles.passwordToggle}
                disabled={isLoading}
              >
                {showPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            style={{
              ...styles.submitButton,
              ...(isLoading ? styles.submitButtonLoading : {})
            }}
          >
            {isLoading ? (
              <>
                <span style={styles.spinner} />
                Signing in...
              </>
            ) : (
              "Sign In"
            )}
          </button>
        </form>

        <div style={styles.footer}>
          <p style={styles.footerText}>
            Don't have an account?{" "}
            <Link to="/register" style={styles.link}>
              Sign up
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    padding: '20px',
  },
  card: {
    background: 'white',
    borderRadius: '16px',
    padding: '40px',
    width: '100%',
    maxWidth: '400px',
    boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    animation: 'slideUp 0.5s ease-out',
  },
  header: {
    textAlign: 'center' as const,
    marginBottom: '32px',
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#1f2937',
    margin: '0 0 8px 0',
  },
  subtitle: {
    color: '#6b7280',
    margin: 0,
    fontSize: '16px',
  },
  form: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '20px',
  },
  errorAlert: {
    background: '#fef2f2',
    border: '1px solid #fecaca',
    color: '#dc2626',
    padding: '12px 16px',
    borderRadius: '8px',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    fontSize: '14px',
  },
  errorIcon: {
    fontSize: '16px',
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '6px',
  },
  label: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
  },
  input: {
    padding: '12px 16px',
    border: '2px solid #e5e7eb',
    borderRadius: '8px',
    fontSize: '16px',
    transition: 'all 0.2s ease',
    outline: 'none',
    ':focus': {
      borderColor: '#667eea',
      boxShadow: '0 0 0 3px rgba(102, 126, 234, 0.1)',
    },
  },
  passwordContainer: {
    position: 'relative' as const,
    display: 'flex',
    alignItems: 'center',
  },
  passwordInput: {
    padding: '12px 50px 12px 16px',
    border: '2px solid #e5e7eb',
    borderRadius: '8px',
    fontSize: '16px',
    width: '100%',
    transition: 'all 0.2s ease',
    outline: 'none',
  },
  passwordToggle: {
    position: 'absolute' as const,
    right: '12px',
    background: 'none',
    border: 'none',
    cursor: 'pointer',
    fontSize: '18px',
    padding: '4px',
  },
  submitButton: {
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    color: 'white',
    border: 'none',
    padding: '14px 24px',
    borderRadius: '8px',
    fontSize: '16px',
    fontWeight: '600',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    ':hover': {
      transform: 'translateY(-1px)',
      boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
    },
  },
  submitButtonLoading: {
    opacity: 0.8,
    cursor: 'not-allowed',
  },
  spinner: {
    width: '16px',
    height: '16px',
    border: '2px solid rgba(255,255,255,0.3)',
    borderTop: '2px solid white',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
  },
  footer: {
    textAlign: 'center' as const,
    marginTop: '24px',
    paddingTop: '24px',
    borderTop: '1px solid #e5e7eb',
  },
  footerText: {
    color: '#6b7280',
    margin: 0,
    fontSize: '14px',
  },
  link: {
    color: '#667eea',
    textDecoration: 'none',
    fontWeight: '500',
    ':hover': {
      textDecoration: 'underline',
    },
  },
};
