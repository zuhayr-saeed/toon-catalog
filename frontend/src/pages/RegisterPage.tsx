// src/pages/RegisterPage.tsx
import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function RegisterPage() {
  const { register, isLoading, user } = useAuth();
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (user) navigate("/");
  }, [user, navigate]);

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.username.trim()) {
      newErrors.username = "Username is required";
    } else if (formData.username.length < 3) {
      newErrors.username = "Username must be at least 3 characters";
    }

    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Please enter a valid email";
    }

    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
    }

    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords don't match";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    try {
      await register(formData.username.trim(), formData.email.trim(), formData.password);
      navigate("/");
    } catch (err: any) {
      setErrors({ general: err.message || "Registration failed" });
    }
  };

  const handleChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: "" }));
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Create Account</h1>
          <p style={styles.subtitle}>Join our webtoon community</p>
        </div>

        <form onSubmit={handleSubmit} style={styles.form}>
          {errors.general && (
            <div style={styles.errorAlert}>
              <span style={styles.errorIcon}>⚠️</span>
              {errors.general}
            </div>
          )}

          <div style={styles.inputGroup}>
            <label style={styles.label}>Username</label>
            <input
              type="text"
              value={formData.username}
              onChange={(e) => handleChange("username", e.target.value)}
              style={{
                ...styles.input,
                ...(errors.username ? styles.inputError : {})
              }}
              placeholder="Choose a username"
              disabled={isLoading}
              autoComplete="username"
            />
            {errors.username && <span style={styles.fieldError}>{errors.username}</span>}
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Email</label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => handleChange("email", e.target.value)}
              style={{
                ...styles.input,
                ...(errors.email ? styles.inputError : {})
              }}
              placeholder="Enter your email"
              disabled={isLoading}
              autoComplete="email"
            />
            {errors.email && <span style={styles.fieldError}>{errors.email}</span>}
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Password</label>
            <div style={styles.passwordContainer}>
              <input
                type={showPassword ? "text" : "password"}
                value={formData.password}
                onChange={(e) => handleChange("password", e.target.value)}
                style={{
                  ...styles.passwordInput,
                  ...(errors.password ? styles.inputError : {})
                }}
                placeholder="Create a password"
                disabled={isLoading}
                autoComplete="new-password"
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
            {errors.password && <span style={styles.fieldError}>{errors.password}</span>}
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Confirm Password</label>
            <input
              type={showPassword ? "text" : "password"}
              value={formData.confirmPassword}
              onChange={(e) => handleChange("confirmPassword", e.target.value)}
              style={{
                ...styles.input,
                ...(errors.confirmPassword ? styles.inputError : {})
              }}
              placeholder="Confirm your password"
              disabled={isLoading}
              autoComplete="new-password"
            />
            {errors.confirmPassword && <span style={styles.fieldError}>{errors.confirmPassword}</span>}
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
                Creating Account...
              </>
            ) : (
              "Create Account"
            )}
          </button>
        </form>

        <div style={styles.footer}>
          <p style={styles.footerText}>
            Already have an account?{" "}
            <Link to="/login" style={styles.link}>
              Sign in
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
  },
  inputError: {
    borderColor: '#dc2626',
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
  fieldError: {
    color: '#dc2626',
    fontSize: '12px',
    marginTop: '4px',
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
  },
};
