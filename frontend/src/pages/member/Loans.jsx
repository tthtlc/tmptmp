// src/pages/member/Loans.jsx
import { useState } from 'react';
import apiService from '../../services/api.js';
import { useAuth } from '../../context/AuthContext.jsx';

function MemberLoans() {
  const [bookId, setBookId] = useState('');
  const [loanId, setLoanId] = useState('');
  const [message, setMessage] = useState('');
  const { credentials } = useAuth();

  const handleBorrow = async () => {
    try {
      await apiRequest('POST', `/member/borrow/${bookId}`, null, credentials);
      setMessage('Book borrowed');
    } catch (err) {
      setMessage(err.message);
    }
  };

  const handleRenew = async () => {
    try {
      await apiRequest('POST', `/member/renew/${loanId}`, null, credentials);
      setMessage('Loan renewed');
    } catch (err) {
      setMessage(err.message);
    }
  };

  const handleReturn = async () => {
    try {
      await apiRequest('POST', `/member/return/${loanId}`, null, credentials);
      setMessage('Book returned');
    } catch (err) {
      setMessage(err.message);
    }
  };

  return (
    <div>
      <h2>Manage Loans</h2>
      <div>
        <input value={bookId} onChange={(e) => setBookId(e.target.value)} placeholder="Book ID to borrow" />
        <button onClick={handleBorrow}>Borrow</button>
      </div>
      <div>
        <input value={loanId} onChange={(e) => setLoanId(e.target.value)} placeholder="Loan ID" />
        <button onClick={handleRenew}>Renew</button>
        <button onClick={handleReturn}>Return</button>
      </div>
      <p>{message}</p>
    </div>
  );
}

export default MemberLoans;
