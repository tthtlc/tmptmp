// src/pages/member/Dashboard.jsx
import { useEffect, useState } from 'react';
import apiService from '../../services/api.js';
import { useAuth } from '../../context/AuthContext.jsx';

function MemberDashboard() {
  const [data, setData] = useState({ member: {}, currentLoans: [], history: [] });
  const [error, setError] = useState('');
  const { credentials } = useAuth();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await apiService.getMemberDashboard();
        setData(response);
      } catch (err) {
        setError(err.message);
      }
    };
    fetchData();
  }, []);

  return (
    <div>
      <h2>Member Dashboard</h2>
      {error && <p>Error: {error}</p>}
      <h3>Profile</h3>
      <p>Name: {data.member.name}</p>
      <p>Email: {data.member.email}</p>
      <h3>Current Loans</h3>
      <table>
        <thead><tr><th>Book</th><th>Borrow Date</th><th>Due Date</th></tr></thead>
        <tbody>
          {data.currentLoans.map(loan => (
            <tr key={loan.id}>
              <td>{loan.book.title}</td>
              <td>{loan.borrowDate}</td>
              <td>{loan.dueDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <h3>Loan History</h3>
      <table>
        <thead><tr><th>Book</th><th>Borrow Date</th><th>Return Date</th><th>Fine</th></tr></thead>
        <tbody>
          {data.history.map(loan => (
            <tr key={loan.id}>
              <td>{loan.book.title}</td>
              <td>{loan.borrowDate}</td>
              <td>{loan.returnDate || 'Active'}</td>
              <td>{loan.fine}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default MemberDashboard;
