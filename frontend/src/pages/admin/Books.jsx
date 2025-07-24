// src/pages/admin/Books.jsx
import { useEffect, useState } from 'react';
import apiService from '../../services/api.js';
import { useAuth } from '../../context/AuthContext.jsx';

function AdminBooks() {
  const [books, setBooks] = useState([]);
  const [formData, setFormData] = useState({ isbn: '', title: '', author: '' });
  const [error, setError] = useState('');
  const { credentials } = useAuth();

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      const data = await apiRequest('GET', '/admin/books', null, credentials);
      setBooks(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleAdd = async () => {
    try {
      await apiRequest('POST', '/admin/books', formData, credentials);
      fetchBooks();
      setFormData({ isbn: '', title: '', author: '' });
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Manage Books</h2>
      {error && <p>Error: {error}</p>}
      <form onSubmit={(e) => { e.preventDefault(); handleAdd(); }}>
        <input name="isbn" value={formData.isbn} onChange={handleChange} placeholder="ISBN" />
        <input name="title" value={formData.title} onChange={handleChange} placeholder="Title" />
        <input name="author" value={formData.author} onChange={handleChange} placeholder="Author" />
        <button type="submit">Add Book</button>
      </form>
      <table>
        <thead><tr><th>ID</th><th>ISBN</th><th>Title</th><th>Author</th></tr></thead>
        <tbody>
          {books.map(book => (
            <tr key={book.id}>
              <td>{book.id}</td>
              <td>{book.isbn}</td>
              <td>{book.title}</td>
              <td>{book.author}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminBooks;
