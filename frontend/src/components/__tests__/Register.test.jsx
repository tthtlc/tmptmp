// src/components/__tests__/Register.test.jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../../context/AuthContext';
import Register from '../Register';
import apiService from '../../services/api';

// Mock the API service
jest.mock('../../services/api');

// Mock react-router-dom
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockNavigate,
}));

const renderRegister = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <Register />
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('Register Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders registration form correctly', () => {
    renderRegister();
    
    expect(screen.getByText('Register for NTUC Library')).toBeInTheDocument();
    expect(screen.getByLabelText('Full Name')).toBeInTheDocument();
    expect(screen.getByLabelText('Username')).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toBeInTheDocument();
    expect(screen.getByLabelText('Password')).toBeInTheDocument();
    expect(screen.getByLabelText('Confirm Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Register' })).toBeInTheDocument();
  });

  test('handles successful registration', async () => {
    const mockResponse = {
      token: 'mock-jwt-token',
      userId: 2,
      name: 'John Doe',
      role: 'USER'
    };
    
    apiService.register.mockResolvedValue(mockResponse);
    
    renderRegister();
    
    fireEvent.change(screen.getByLabelText('Full Name'), {
      target: { value: 'John Doe' }
    });
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'johndoe' }
    });
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'john.doe@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' }
    });
    fireEvent.change(screen.getByLabelText('Confirm Password'), {
      target: { value: 'password123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));
    
    await waitFor(() => {
      expect(apiService.register).toHaveBeenCalledWith({
        name: 'John Doe',
        username: 'johndoe',
        email: 'john.doe@example.com',
        password: 'password123',
        role: 'USER'
      });
      expect(mockNavigate).toHaveBeenCalledWith('/member/dashboard');
    });
  });

  test('validates password confirmation', async () => {
    renderRegister();
    
    fireEvent.change(screen.getByLabelText('Full Name'), {
      target: { value: 'John Doe' }
    });
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'johndoe' }
    });
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'john.doe@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' }
    });
    fireEvent.change(screen.getByLabelText('Confirm Password'), {
      target: { value: 'differentpassword' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));
    
    await waitFor(() => {
      expect(screen.getByText('Passwords do not match')).toBeInTheDocument();
    });
    
    expect(apiService.register).not.toHaveBeenCalled();
  });

  test('validates password length', async () => {
    renderRegister();
    
    fireEvent.change(screen.getByLabelText('Full Name'), {
      target: { value: 'John Doe' }
    });
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'johndoe' }
    });
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'john.doe@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: '123' }
    });
    fireEvent.change(screen.getByLabelText('Confirm Password'), {
      target: { value: '123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));
    
    await waitFor(() => {
      expect(screen.getByText('Password must be at least 6 characters long')).toBeInTheDocument();
    });
    
    expect(apiService.register).not.toHaveBeenCalled();
  });

  test('handles registration failure', async () => {
    apiService.register.mockRejectedValue(new Error('Username already exists'));
    
    renderRegister();
    
    fireEvent.change(screen.getByLabelText('Full Name'), {
      target: { value: 'John Doe' }
    });
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'existinguser' }
    });
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'john.doe@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' }
    });
    fireEvent.change(screen.getByLabelText('Confirm Password'), {
      target: { value: 'password123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));
    
    await waitFor(() => {
      expect(screen.getByText('Username already exists')).toBeInTheDocument();
    });
  });

  test('shows loading state during registration', async () => {
    apiService.register.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
    
    renderRegister();
    
    fireEvent.change(screen.getByLabelText('Full Name'), {
      target: { value: 'John Doe' }
    });
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'johndoe' }
    });
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'john.doe@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' }
    });
    fireEvent.change(screen.getByLabelText('Confirm Password'), {
      target: { value: 'password123' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Register' }));
    
    expect(screen.getByText('Registering...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Registering...' })).toBeDisabled();
  });
});

