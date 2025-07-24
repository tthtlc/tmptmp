// src/pages/admin/Loans.jsx
import { useEffect, useState } from 'react';
import apiService from '../../services/api.js';
import { useAuth } from '../../context/AuthContext.jsx';

function AdminLoans() {
  const [loans, setLoans] = useState([]);
  const [searchName, setSearchName] = useState('');
  const [newLoan, setNewLoan] = useState({ memberId: '', isbn: '' });
  const [extendId, setExtendId] = useState('');
  const [deleteId, setDeleteId] = useState('');
  const [error, setError] = useState('');
  const { credentials } = useAuth();

  useEffect(() => {
    fetchLoans();
  }, []);

  const fetchLoans = async () => {
    try {
      const data = await apiRequest('GET', '/admin/loans', null, credentials);
      setLoans(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleSearch = async () => {
    try {
      const data = await apiRequest('GET', `/admin/loans/search?name=${searchName}`, null, credentials);
      setLoans(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleCreateLoan = async () => {
    try {
      await apiRequest('POST', '/admin/loans', newLoan, credentials);
      fetchLoans();
      setNewLoan({ memberId: '', isbn: '' });
    } catch (err) {
      setError(err.message);
    }
  };

  const handleExtend = async () => {
    try {
      await apiRequest('PUT', `/admin/loans/${extendId}/extend`, null, credentials);
      fetchLoans();
      setExtendId('');
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async () => {
    try {
      await apiRequest('DELETE', `/admin/loans/${deleteId}`, null, credentials);
      fetchLoans();
      setDeleteId('');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Manage Loans</h2>
      {error && <p>Error: {error}</p>}
      <div>
        <input value={searchName} onChange={(e) => setSearchName(e.target.value)} placeholder="Search by member name" />
        <button onClick={handleSearch}>Search</button>
      </div>
      <div>
        <input value={newLoan.memberId} onChange={(e) => setNewLoan({ ...newLoan, memberId: e.target.value })} placeholder="Member ID" />
        <input value={newLoan.isbn} onChange={(e) => setNewLoan({ ...newLoan, isbn: e.target.value })} placeholder="ISBN" />
        <button onClick={handleCreateLoan}>Create Loan</button>
      </div>
      <div>
        <input value={extendId} onChange={(e) => setExtendId(e.target.value)} placeholder="Loan ID to extend" />
        <button onClick={handleExtend}>Extend</button>
      </div>
      <div>
        <input value={deleteId} onChange={(e) => setDeleteId(e.target.value)} placeholder="Loan ID to delete" />
        <button onClick={handleDelete}>Delete</button>
      </div>
      <table>
        <thead><tr><th>ID</th><th>Member</th><th>Book</th><th>Borrow Date</th><th>Due Date</th><th>Return Date</th><th>Fine</th></tr></thead>
        <tbody>
          {loans.map(loan => (
            <tr key={loan.id}>
              <td>{loan.id}</td>
              <td>{loan.member.name}</td>
              <td>{loan.book.title}</td>
              <td>{loan.borrowDate}</td>
              <td>{loan.dueDate}</td>
              <td>{loan.returnDate}</td>
              <td>{loan.fine}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminLoans;
