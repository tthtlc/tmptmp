// src/components/__tests__/Login.test.jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../../context/AuthContext';
import Login from '../Login';
import apiService from '../../services/api';

// Mock the API service
jest.mock('../../services/api');

// Mock react-router-dom
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderLogin = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <Login />
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('Login Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders login form correctly', () => {
    renderLogin();
    
    expect(screen.getByText('Login to NTUC Library')).toBeInTheDocument();
    expect(screen.getByLabelText('Username')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Login' })).toBeInTheDocument();
    expect(screen.getByText("Don't have an account?")).toBeInTheDocument();
  });

  test('displays demo credentials', () => {
    renderLogin();
    
    expect(screen.getByText('Demo Credentials:')).toBeInTheDocument();
    expect(screen.getByText(/Admin.*username: admin, password: password/)).toBeInTheDocument();
  });

  test('handles successful admin login', async () => {
    const mockResponse = {
      token: 'mock-jwt-token',
      userId: 1,
      name: 'Admin User',
      role: 'ADMIN'
    };
    
    apiService.login.mockResolvedValue(mockResponse);
    
    renderLogin();
    
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'admin' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Login' }));
    
    await waitFor(() => {
      expect(apiService.login).toHaveBeenCalledWith('admin', 'password');
      expect(mockNavigate).toHaveBeenCalledWith('/admin/dashboard');
    });
  });

  test('handles successful user login', async () => {
    const mockResponse = {
      token: 'mock-jwt-token',
      userId: 2,
      name: 'John Doe',
      role: 'USER'
    };
    
    apiService.login.mockResolvedValue(mockResponse);
    
    renderLogin();
    
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'johndoe' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Login' }));
    
    await waitFor(() => {
      expect(apiService.login).toHaveBeenCalledWith('johndoe', 'password123');
      expect(mockNavigate).toHaveBeenCalledWith('/member/dashboard');
    });
  });

  test('handles login failure', async () => {
    apiService.login.mockRejectedValue(new Error('Invalid credentials'));
    
    renderLogin();
    
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'wronguser' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'wrongpass' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Login' }));
    
    await waitFor(() => {
      expect(screen.getByText('Invalid credentials')).toBeInTheDocument();
    });
  });

  test('shows loading state during login', async () => {
    apiService.login.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
    
    renderLogin();
    
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'admin' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Login' }));
    
    expect(screen.getByText('Logging in...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Logging in...' })).toBeDisabled();
  });

  test('validates required fields', () => {
    renderLogin();
    
    const usernameInput = screen.getByLabelText('Username');
    const passwordInput = screen.getByLabelText('Password');
    
    expect(usernameInput).toHaveAttribute('required');
    expect(passwordInput).toHaveAttribute('required');
  });
});

