// src/services/__tests__/api.test.js
import apiService from '../api';

// Mock fetch
global.fetch = jest.fn();

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
global.localStorage = localStorageMock;

// Mock window.location
delete window.location;
window.location = { href: '' };

describe('ApiService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    fetch.mockClear();
    localStorageMock.getItem.mockClear();
    localStorageMock.setItem.mockClear();
    localStorageMock.removeItem.mockClear();
  });

  describe('Authentication', () => {
    test('login sets token on successful response', async () => {
      const mockResponse = {
        token: 'mock-jwt-token',
        userId: 1,
        name: 'John Doe',
        role: 'USER'
      };

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => mockResponse
      });

      const result = await apiService.login('johndoe', 'password123');

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username: 'johndoe', password: 'password123' })
      });

      expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'mock-jwt-token');
      expect(result).toEqual(mockResponse);
    });

    test('register sets token on successful response', async () => {
      const userData = {
        name: 'John Doe',
        username: 'johndoe',
        email: 'john.doe@example.com',
        password: 'password123',
        role: 'USER'
      };

      const mockResponse = {
        token: 'mock-jwt-token',
        userId: 2,
        name: 'John Doe',
        role: 'USER'
      };

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => mockResponse
      });

      const result = await apiService.register(userData);

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      });

      expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'mock-jwt-token');
      expect(result).toEqual(mockResponse);
    });

    test('logout removes token and redirects', () => {
      apiService.logout();

      expect(localStorageMock.removeItem).toHaveBeenCalledWith('token');
      expect(window.location.href).toBe('/login');
    });
  });

  describe('Authenticated Requests', () => {
    test('includes authorization header when token exists', async () => {
      localStorageMock.getItem.mockReturnValue('mock-jwt-token');

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => ({ data: 'test' })
      });

      await apiService.getMemberDashboard();

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/member/dashboard', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer mock-jwt-token'
        }
      });
    });

    test('handles 401 unauthorized response', async () => {
      localStorageMock.getItem.mockReturnValue('expired-token');

      fetch.mockResolvedValueOnce({
        ok: false,
        status: 401,
        text: async () => 'Unauthorized'
      });

      await expect(apiService.getMemberDashboard()).rejects.toThrow('Authentication failed');

      expect(localStorageMock.removeItem).toHaveBeenCalledWith('token');
      expect(window.location.href).toBe('/login');
    });
  });

  describe('Book Operations', () => {
    test('getAllBooks makes correct API call', async () => {
      const mockBooks = [
        { id: 1, title: 'Book 1', author: 'Author 1' },
        { id: 2, title: 'Book 2', author: 'Author 2' }
      ];

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => mockBooks
      });

      const result = await apiService.getAllBooks();

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/books', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      expect(result).toEqual(mockBooks);
    });

    test('searchBooks constructs correct query parameters', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => []
      });

      await apiService.searchBooks('Java', 'Bloch', '978-0134685991');

      expect(fetch).toHaveBeenCalledWith(
        'http://localhost:8484/api/books/search?title=Java&author=Bloch&isbn=978-0134685991',
        expect.any(Object)
      );
    });

    test('borrowBook makes authenticated request', async () => {
      localStorageMock.getItem.mockReturnValue('mock-jwt-token');

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => ({ message: 'Book borrowed successfully' })
      });

      await apiService.borrowBook(1);

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/member/borrow/1', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer mock-jwt-token'
        }
      });
    });
  });

  describe('Admin Operations', () => {
    test('getAllMembers makes authenticated admin request', async () => {
      localStorageMock.getItem.mockReturnValue('admin-token');

      const mockMembers = [
        { id: 1, name: 'John Doe', role: 'USER' },
        { id: 2, name: 'Jane Smith', role: 'ADMIN' }
      ];

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => mockMembers
      });

      const result = await apiService.getAllMembers();

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/admin/members', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer admin-token'
        }
      });

      expect(result).toEqual(mockMembers);
    });

    test('addMember sends correct data', async () => {
      localStorageMock.getItem.mockReturnValue('admin-token');

      const memberData = {
        name: 'New User',
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
        role: 'USER'
      };

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => ({ ...memberData, id: 3 })
      });

      await apiService.addMember(memberData);

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/admin/members', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer admin-token'
        },
        body: JSON.stringify(memberData)
      });
    });

    test('createLoan sends correct loan data', async () => {
      localStorageMock.getItem.mockReturnValue('admin-token');

      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'application/json'
        },
        json: async () => ({ message: 'Loan created successfully' })
      });

      await apiService.createLoan(1, '978-0134685991');

      expect(fetch).toHaveBeenCalledWith('http://localhost:8484/api/admin/loans', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer admin-token'
        },
        body: JSON.stringify({ memberId: 1, isbn: '978-0134685991' })
      });
    });
  });

  describe('Error Handling', () => {
    test('throws error for non-ok response', async () => {
      fetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        text: async () => 'Bad Request'
      });

      await expect(apiService.getAllBooks()).rejects.toThrow('Bad Request');
    });

    test('handles network errors', async () => {
      fetch.mockRejectedValueOnce(new Error('Network error'));

      await expect(apiService.getAllBooks()).rejects.toThrow('Network error');
    });

    test('handles non-JSON responses', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        headers: {
          get: () => 'text/plain'
        },
        text: async () => 'Plain text response'
      });

      const result = await apiService.getAllBooks();
      expect(result).toBe('Plain text response');
    });
  });
});

