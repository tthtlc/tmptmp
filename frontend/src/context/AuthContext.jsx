// src/context/AuthContext.jsx
import { createContext, useState, useContext, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import apiService from '../services/api';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const token = apiService.getToken();
      if (token) {
        const isValid = await apiService.validateToken();
        if (isValid) {
          const decoded = jwtDecode(token);
          setUser({
            username: decoded.sub,
            role: decoded.role,
            token: token
          });
        } else {
          apiService.setToken(null);
        }
      }
    } catch (error) {
      console.error('Auth check failed:', error);
      apiService.setToken(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (username, password) => {
    try {
      const response = await apiService.login(username, password);
      const decoded = jwtDecode(response.token);
      
      const userData = {
        id: response.userId,
        name: response.name,
        username: decoded.sub,
        role: response.role,
        token: response.token
      };
      
      setUser(userData);
      return userData;
    } catch (error) {
      throw error;
    }
  };

  const register = async (userData) => {
    try {
      const response = await apiService.register(userData);
      const decoded = jwtDecode(response.token);
      
      const newUser = {
        id: response.userId,
        name: response.name,
        username: decoded.sub,
        role: response.role,
        token: response.token
      };
      
      setUser(newUser);
      return newUser;
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    apiService.logout();
    setUser(null);
  };

  const isAuthenticated = () => {
    return !!user && !!user.token;
  };

  const isAdmin = () => {
    return user && user.role === 'ADMIN';
  };

  const isUser = () => {
    return user && (user.role === 'USER' || user.role === 'ADMIN');
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    isAuthenticated,
    isAdmin,
    isUser,
    checkAuthStatus
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
