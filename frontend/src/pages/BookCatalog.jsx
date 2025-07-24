// src/pages/BookCatalog.jsx
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import apiService from '../services/api';
import { useAuth } from '../context/AuthContext';

function BookCatalog() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchParams, setSearchParams] = useState({
    title: '',
    author: '',
    isbn: ''
  });
  const { isAuthenticated, isUser } = useAuth();

  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async () => {
    try {
      setLoading(true);
      const data = await apiService.getAllBooks();
      setBooks(data);
    } catch (err) {
      setError('Failed to load books: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const data = await apiService.searchBooks(
        searchParams.title,
        searchParams.author,
        searchParams.isbn
      );
      setBooks(data);
    } catch (err) {
      setError('Search failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleBorrow = async (bookId) => {
    try {
      await apiService.borrowBook(bookId);
      alert('Book borrowed successfully!');
      loadBooks(); // Refresh the list
    } catch (err) {
      alert('Failed to borrow book: ' + err.message);
    }
  };

  const handleSearchChange = (e) => {
    setSearchParams({
      ...searchParams,
      [e.target.name]: e.target.value
    });
  };

  const clearSearch = () => {
    setSearchParams({ title: '', author: '', isbn: '' });
    loadBooks();
  };

  if (loading) {
    return <div className="loading">Loading books...</div>;
  }

  return (
    <div className="book-catalog">
      <div className="catalog-header">
        <h1>Book Catalog</h1>
        {!isAuthenticated() && (
          <div className="auth-prompt">
            <Link to="/login" className="btn btn-primary">Login</Link>
            <Link to="/register" className="btn btn-secondary">Register</Link>
          </div>
        )}
      </div>

      <div className="search-section">
        <form onSubmit={handleSearch} className="search-form">
          <div className="search-fields">
            <input
              type="text"
              name="title"
              placeholder="Search by title"
              value={searchParams.title}
              onChange={handleSearchChange}
            />
            <input
              type="text"
              name="author"
              placeholder="Search by author"
              value={searchParams.author}
              onChange={handleSearchChange}
            />
            <input
              type="text"
              name="isbn"
              placeholder="Search by ISBN"
              value={searchParams.isbn}
              onChange={handleSearchChange}
            />
          </div>
          <div className="search-buttons">
            <button type="submit" className="btn btn-primary">Search</button>
            <button type="button" onClick={clearSearch} className="btn btn-secondary">
              Clear
            </button>
          </div>
        </form>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="books-grid">
        {books.length === 0 ? (
          <div className="no-books">No books found</div>
        ) : (
          books.map((book) => (
            <div key={book.id} className="book-card">
              <div className="book-info">
                <h3 className="book-title">{book.title}</h3>
                <p className="book-author">by {book.author}</p>
                <p className="book-isbn">ISBN: {book.isbn}</p>
                <div className={`availability ${book.available ? 'available' : 'unavailable'}`}>
                  {book.available ? 'Available' : 'Not Available'}
                </div>
              </div>
              
              {isAuthenticated() && isUser() && book.available && (
                <div className="book-actions">
                  <button
                    onClick={() => handleBorrow(book.id)}
                    className="btn btn-primary"
                  >
                    Borrow Book
                  </button>
                </div>
              )}
              
              {!isAuthenticated() && book.available && (
                <div className="book-actions">
                  <Link to="/login" className="btn btn-primary">
                    Login to Borrow
                  </Link>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default BookCatalog;

