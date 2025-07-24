// src/pages/__tests__/BookCatalog.test.jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../../context/AuthContext';
import BookCatalog from '../BookCatalog';
import apiService from '../../services/api';

// Mock the API service
jest.mock('../../services/api');

// Mock the auth context
const mockAuthContext = {
  isAuthenticated: jest.fn(),
  isUser: jest.fn(),
  user: null
};

jest.mock('../../context/AuthContext', () => ({
  ...jest.requireActual('../../context/AuthContext'),
  useAuth: () => mockAuthContext,
}));

const renderBookCatalog = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <BookCatalog />
      </AuthProvider>
    </BrowserRouter>
  );
};

const mockBooks = [
  {
    id: 1,
    isbn: '978-0134685991',
    title: 'Effective Java',
    author: 'Joshua Bloch',
    available: true
  },
  {
    id: 2,
    isbn: '978-0321356680',
    title: 'Effective C++',
    author: 'Scott Meyers',
    available: false
  }
];

describe('BookCatalog Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockAuthContext.isAuthenticated.mockReturnValue(false);
    mockAuthContext.isUser.mockReturnValue(false);
    mockAuthContext.user = null;
  });

  test('renders book catalog correctly', async () => {
    apiService.getAllBooks.mockResolvedValue(mockBooks);
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByText('Book Catalog')).toBeInTheDocument();
      expect(screen.getByText('Effective Java')).toBeInTheDocument();
      expect(screen.getByText('by Joshua Bloch')).toBeInTheDocument();
      expect(screen.getByText('ISBN: 978-0134685991')).toBeInTheDocument();
      expect(screen.getByText('Available')).toBeInTheDocument();
      
      expect(screen.getByText('Effective C++')).toBeInTheDocument();
      expect(screen.getByText('by Scott Meyers')).toBeInTheDocument();
      expect(screen.getByText('Not Available')).toBeInTheDocument();
    });
  });

  test('shows login and register buttons for unauthenticated users', async () => {
    apiService.getAllBooks.mockResolvedValue(mockBooks);
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByRole('link', { name: 'Login' })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: 'Register' })).toBeInTheDocument();
    });
  });

  test('shows borrow button for authenticated users on available books', async () => {
    mockAuthContext.isAuthenticated.mockReturnValue(true);
    mockAuthContext.isUser.mockReturnValue(true);
    mockAuthContext.user = { id: 1, name: 'John Doe', role: 'USER' };
    
    apiService.getAllBooks.mockResolvedValue(mockBooks);
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Borrow Book' })).toBeInTheDocument();
      expect(screen.queryByText('Login to Borrow')).not.toBeInTheDocument();
    });
  });

  test('handles book search functionality', async () => {
    const searchResults = [mockBooks[0]];
    apiService.getAllBooks.mockResolvedValue(mockBooks);
    apiService.searchBooks.mockResolvedValue(searchResults);
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByText('Effective Java')).toBeInTheDocument();
    });
    
    fireEvent.change(screen.getByPlaceholderText('Search by title'), {
      target: { value: 'Effective Java' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Search' }));
    
    await waitFor(() => {
      expect(apiService.searchBooks).toHaveBeenCalledWith('Effective Java', '', '');
    });
  });

  test('handles clear search functionality', async () => {
    apiService.getAllBooks.mockResolvedValue(mockBooks);
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByText('Effective Java')).toBeInTheDocument();
    });
    
    fireEvent.change(screen.getByPlaceholderText('Search by title'), {
      target: { value: 'Java' }
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Clear' }));
    
    await waitFor(() => {
      expect(apiService.getAllBooks).toHaveBeenCalledTimes(2); // Initial load + clear search
    });
  });

  test('handles book borrowing', async () => {
    mockAuthContext.isAuthenticated.mockReturnValue(true);
    mockAuthContext.isUser.mockReturnValue(true);
    mockAuthContext.user = { id: 1, name: 'John Doe', role: 'USER' };
    
    apiService.getAllBooks.mockResolvedValue(mockBooks);
    apiService.borrowBook.mockResolvedValue({ message: 'Book borrowed successfully' });
    
    // Mock window.alert
    window.alert = jest.fn();
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Borrow Book' })).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Borrow Book' }));
    
    await waitFor(() => {
      expect(apiService.borrowBook).toHaveBeenCalledWith(1);
      expect(window.alert).toHaveBeenCalledWith('Book borrowed successfully!');
    });
  });

  test('handles borrowing failure', async () => {
    mockAuthContext.isAuthenticated.mockReturnValue(true);
    mockAuthContext.isUser.mockReturnValue(true);
    mockAuthContext.user = { id: 1, name: 'John Doe', role: 'USER' };
    
    apiService.getAllBooks.mockResolvedValue(mockBooks);
    apiService.borrowBook.mockRejectedValue(new Error('Cannot borrow: limit exceeded'));
    
    // Mock window.alert
    window.alert = jest.fn();
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Borrow Book' })).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByRole('button', { name: 'Borrow Book' }));
    
    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Failed to borrow book: Cannot borrow: limit exceeded');
    });
  });

  test('displays no books message when catalog is empty', async () => {
    apiService.getAllBooks.mockResolvedValue([]);
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByText('No books found')).toBeInTheDocument();
    });
  });

  test('handles loading state', () => {
    apiService.getAllBooks.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));
    
    renderBookCatalog();
    
    expect(screen.getByText('Loading books...')).toBeInTheDocument();
  });

  test('handles API error', async () => {
    apiService.getAllBooks.mockRejectedValue(new Error('Failed to load books'));
    
    renderBookCatalog();
    
    await waitFor(() => {
      expect(screen.getByText('Failed to load books: Failed to load books')).toBeInTheDocument();
    });
  });
});

