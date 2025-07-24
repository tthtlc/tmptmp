// src/context/__tests__/AuthContext.test.jsx
import { render, screen, waitFor, act } from '@testing-library/react';
import { AuthProvider, useAuth } from '../AuthContext';
import apiService from '../../services/api';

// Mock the API service
jest.mock('../../services/api');

// Mock jwt-decode
jest.mock('jwt-decode', () => ({
  jwtDecode: jest.fn(() => ({
    sub: 'testuser',
    role: 'USER',
    exp: Math.floor(Date.now() / 1000) + 3600 // 1 hour from now
  }))
}));

// Test component to access auth context
const TestComponent = () => {
  const { user, loading, login, logout, isAuthenticated, isAdmin, isUser } = useAuth();
  
  return (
    <div>
      <div data-testid="loading">{loading ? 'loading' : 'not-loading'}</div>
      <div data-testid="user">{user ? JSON.stringify(user) : 'no-user'}</div>
      <div data-testid="authenticated">{isAuthenticated() ? 'authenticated' : 'not-authenticated'}</div>
      <div data-testid="admin">{isAdmin() ? 'admin' : 'not-admin'}</div>
      <div data-testid="user-role">{isUser() ? 'user' : 'not-user'}</div>
      <button onClick={() => login('testuser', 'password')}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

const renderWithAuthProvider = () => {
  return render(
    <AuthProvider>
      <TestComponent />
    </AuthProvider>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Mock localStorage
    Storage.prototype.getItem = jest.fn();
    Storage.prototype.setItem = jest.fn();
    Storage.prototype.removeItem = jest.fn();
  });

  test('initializes with loading state', () => {
    apiService.getToken.mockReturnValue(null);
    apiService.validateToken.mockResolvedValue(false);
    
    renderWithAuthProvider();
    
    expect(screen.getByTestId('loading')).toHaveTextContent('loading');
    expect(screen.getByTestId('user')).toHaveTextContent('no-user');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('not-authenticated');
  });

  test('loads existing valid token on initialization', async () => {
    const mockToken = 'valid-jwt-token';
    apiService.getToken.mockReturnValue(mockToken);
    apiService.validateToken.mockResolvedValue(true);
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
      expect(screen.getByTestId('authenticated')).toHaveTextContent('authenticated');
      expect(screen.getByTestId('user-role')).toHaveTextContent('user');
      expect(screen.getByTestId('admin')).toHaveTextContent('not-admin');
    });
  });

  test('handles invalid token on initialization', async () => {
    const mockToken = 'invalid-jwt-token';
    apiService.getToken.mockReturnValue(mockToken);
    apiService.validateToken.mockResolvedValue(false);
    apiService.setToken.mockImplementation(() => {});
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
      expect(screen.getByTestId('authenticated')).toHaveTextContent('not-authenticated');
      expect(apiService.setToken).toHaveBeenCalledWith(null);
    });
  });

  test('handles successful login', async () => {
    apiService.getToken.mockReturnValue(null);
    apiService.validateToken.mockResolvedValue(false);
    
    const mockLoginResponse = {
      token: 'new-jwt-token',
      userId: 1,
      name: 'Test User',
      role: 'USER'
    };
    
    apiService.login.mockResolvedValue(mockLoginResponse);
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    });
    
    await act(async () => {
      screen.getByText('Login').click();
    });
    
    await waitFor(() => {
      expect(screen.getByTestId('authenticated')).toHaveTextContent('authenticated');
      expect(screen.getByTestId('user')).toContain('Test User');
      expect(screen.getByTestId('user-role')).toHaveTextContent('user');
    });
  });

  test('handles admin login', async () => {
    apiService.getToken.mockReturnValue(null);
    apiService.validateToken.mockResolvedValue(false);
    
    // Mock jwt-decode to return admin role
    const { jwtDecode } = require('jwt-decode');
    jwtDecode.mockReturnValue({
      sub: 'admin',
      role: 'ADMIN',
      exp: Math.floor(Date.now() / 1000) + 3600
    });
    
    const mockLoginResponse = {
      token: 'admin-jwt-token',
      userId: 1,
      name: 'Admin User',
      role: 'ADMIN'
    };
    
    apiService.login.mockResolvedValue(mockLoginResponse);
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    });
    
    await act(async () => {
      screen.getByText('Login').click();
    });
    
    await waitFor(() => {
      expect(screen.getByTestId('authenticated')).toHaveTextContent('authenticated');
      expect(screen.getByTestId('admin')).toHaveTextContent('admin');
      expect(screen.getByTestId('user-role')).toHaveTextContent('user'); // Admin is also a user
    });
  });

  test('handles login failure', async () => {
    apiService.getToken.mockReturnValue(null);
    apiService.validateToken.mockResolvedValue(false);
    apiService.login.mockRejectedValue(new Error('Invalid credentials'));
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    });
    
    await expect(async () => {
      await act(async () => {
        screen.getByText('Login').click();
      });
    }).rejects.toThrow('Invalid credentials');
    
    expect(screen.getByTestId('authenticated')).toHaveTextContent('not-authenticated');
  });

  test('handles logout', async () => {
    // First set up authenticated state
    const mockToken = 'valid-jwt-token';
    apiService.getToken.mockReturnValue(mockToken);
    apiService.validateToken.mockResolvedValue(true);
    apiService.logout.mockImplementation(() => {});
    
    renderWithAuthProvider();
    
    await waitFor(() => {
      expect(screen.getByTestId('authenticated')).toHaveTextContent('authenticated');
    });
    
    act(() => {
      screen.getByText('Logout').click();
    });
    
    expect(apiService.logout).toHaveBeenCalled();
    expect(screen.getByTestId('user')).toHaveTextContent('no-user');
    expect(screen.getByTestId('authenticated')).toHaveTextContent('not-authenticated');
  });

  test('handles registration', async () => {
    apiService.getToken.mockReturnValue(null);
    apiService.validateToken.mockResolvedValue(false);
    
    const mockRegisterResponse = {
      token: 'new-user-token',
      userId: 2,
      name: 'New User',
      role: 'USER'
    };
    
    apiService.register.mockResolvedValue(mockRegisterResponse);
    
    const TestRegisterComponent = () => {
      const { register } = useAuth();
      
      return (
        <button onClick={() => register({
          name: 'New User',
          username: 'newuser',
          email: 'new@example.com',
          password: 'password123',
          role: 'USER'
        })}>
          Register
        </button>
      );
    };
    
    render(
      <AuthProvider>
        <TestComponent />
        <TestRegisterComponent />
      </AuthProvider>
    );
    
    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('not-loading');
    });
    
    await act(async () => {
      screen.getByText('Register').click();
    });
    
    await waitFor(() => {
      expect(screen.getByTestId('authenticated')).toHaveTextContent('authenticated');
      expect(screen.getByTestId('user')).toContain('New User');
    });
  });

  test('throws error when useAuth is used outside AuthProvider', () => {
    // Suppress console.error for this test
    const originalError = console.error;
    console.error = jest.fn();
    
    expect(() => {
      render(<TestComponent />);
    }).toThrow('useAuth must be used within an AuthProvider');
    
    console.error = originalError;
  });
});

