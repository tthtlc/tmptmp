// src/pages/admin/Dashboard.jsx
// Similar to MemberDashboard, but for admin overview; perhaps fetch all loans or members
import { useEffect, useState } from 'react';
import apiService from '../../services/api.js';
import { useAuth } from '../../context/AuthContext.jsx';

function AdminDashboard() {
  const [loans, setLoans] = useState([]);
  const [error, setError] = useState('');
  const { credentials } = useAuth();

  useEffect(() => {
    const fetchLoans = async () => {
      try {
        const data = await apiRequest('GET', '/admin/loans', null, credentials);
        setLoans(data);
      } catch (err) {
        setError(err.message);
      }
    };
    fetchLoans();
  }, []);

  return (
    <div>
      <h2>Admin Dashboard</h2>
      {error && <p>Error: {error}</p>}
      <h3>Current Loans</h3>
      <table>
        <thead><tr><th>Member</th><th>Book</th><th>Borrow Date</th><th>Due Date</th></tr></thead>
        <tbody>
          {loans.map(loan => (
            <tr key={loan.id}>
              <td>{loan.member.name}</td>
              <td>{loan.book.title}</td>
              <td>{loan.borrowDate}</td>
              <td>{loan.dueDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminDashboard;
