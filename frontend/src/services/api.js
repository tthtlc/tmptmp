// src/services/api.js
const BASE_URL = 'http://localhost:8484/api'; // Deployed backend URL

class ApiService {
  constructor() {
    this.token = localStorage.getItem('token');
  }

  setToken(token) {
    this.token = token;
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }

  getToken() {
    return this.token || localStorage.getItem('token');
  }

  async request(method, path, body = null) {
    const url = `${BASE_URL}${path}`;
    const headers = {
      'Content-Type': 'application/json',
    };

    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const options = {
      method,
      headers,
    };

    if (body) {
      options.body = JSON.stringify(body);
    }

    try {
      const response = await fetch(url, options);
      
      if (!response.ok) {
        if (response.status === 401) {
          // Token expired or invalid
          this.setToken(null);
          window.location.href = '/login';
          throw new Error('Authentication failed');
        }
        
        const errorText = await response.text();
        throw new Error(errorText || `HTTP error! status: ${response.status}`);
      }

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      } else {
        return await response.text();
      }
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  // Authentication methods
  async login(username, password) {
    const response = await this.request('POST', '/auth/login', { username, password });
    if (response.token) {
      this.setToken(response.token);
    }
    return response;
  }

  async register(userData) {
    const response = await this.request('POST', '/auth/register', userData);
    if (response.token) {
      this.setToken(response.token);
    }
    return response;
  }

  async validateToken() {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      await this.request('POST', '/auth/validate');
      return true;
    } catch (error) {
      this.setToken(null);
      return false;
    }
  }

  logout() {
    this.setToken(null);
    window.location.href = '/login';
  }

  // Member methods
  async getMemberDashboard() {
    return await this.request('GET', '/member/dashboard');
  }

  async getMemberProfile() {
    return await this.request('GET', '/member/profile');
  }

  async updateMemberProfile(profileData) {
    return await this.request('PUT', '/member/profile', profileData);
  }

  async getCurrentLoans() {
    return await this.request('GET', '/member/loans');
  }

  async getLoanHistory() {
    return await this.request('GET', '/member/loans/history');
  }

  async borrowBook(bookId) {
    return await this.request('POST', `/member/borrow/${bookId}`);
  }

  async renewLoan(loanId) {
    return await this.request('POST', `/member/renew/${loanId}`);
  }

  async returnBook(loanId) {
    return await this.request('POST', `/member/return/${loanId}`);
  }

  async getTotalFines() {
    return await this.request('GET', '/member/fines');
  }

  async checkBorrowEligibility() {
    return await this.request('GET', '/member/eligibility');
  }

  // Admin methods
  async getAdminDashboard() {
    return await this.request('GET', '/admin/dashboard');
  }

  async getAllMembers() {
    return await this.request('GET', '/admin/members');
  }

  async addMember(memberData) {
    return await this.request('POST', '/admin/members', memberData);
  }

  async updateMember(id, memberData) {
    return await this.request('PUT', `/admin/members/${id}`, memberData);
  }

  async deleteMember(id) {
    return await this.request('DELETE', `/admin/members/${id}`);
  }

  async renewMembership(id) {
    return await this.request('PUT', `/admin/members/${id}/renew`);
  }

  async searchMembers(name) {
    return await this.request('GET', `/admin/members/search?name=${encodeURIComponent(name)}`);
  }

  async getAllBooks() {
    return await this.request('GET', '/books');
  }

  async getAvailableBooks() {
    return await this.request('GET', '/books/available');
  }

  async addBook(bookData) {
    return await this.request('POST', '/admin/books', bookData);
  }

  async updateBook(id, bookData) {
    return await this.request('PUT', `/admin/books/${id}`, bookData);
  }

  async deleteBook(id) {
    return await this.request('DELETE', `/admin/books/${id}`);
  }

  async searchBooks(title, author, isbn) {
    const params = new URLSearchParams();
    if (title) params.append('title', title);
    if (author) params.append('author', author);
    if (isbn) params.append('isbn', isbn);
    
    return await this.request('GET', `/books/search?${params.toString()}`);
  }

  async getAllLoans() {
    return await this.request('GET', '/admin/loans');
  }

  async createLoan(memberId, isbn) {
    return await this.request('POST', '/admin/loans', { memberId, isbn });
  }

  async extendLoan(id) {
    return await this.request('PUT', `/admin/loans/${id}/extend`);
  }

  async deleteLoan(id) {
    return await this.request('DELETE', `/admin/loans/${id}`);
  }

  async searchLoansByMemberName(name) {
    return await this.request('GET', `/admin/loans/search?name=${encodeURIComponent(name)}`);
  }

  async getOverdueLoans() {
    return await this.request('GET', '/admin/loans/overdue');
  }

  async updateOverdueLoansStatus() {
    return await this.request('POST', '/admin/loans/update-overdue');
  }

  async getStatistics() {
    return await this.request('GET', '/admin/statistics');
  }
}

export default new ApiService();
