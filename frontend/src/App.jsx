// src/App.jsx
import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login.jsx';
import Register from './components/Register.jsx';
import MemberDashboard from './pages/member/Dashboard.jsx';
import MemberProfile from './pages/member/Profile.jsx';
import MemberLoans from './pages/member/Loans.jsx';
import AdminDashboard from './pages/admin/Dashboard.jsx';
import AdminMembers from './pages/admin/Members.jsx';
import AdminBooks from './pages/admin/Books.jsx';
import AdminLoans from './pages/admin/Loans.jsx';
import BookCatalog from './pages/BookCatalog.jsx';
import Navbar from './components/Navbar.jsx';
import { useAuth } from './context/AuthContext.jsx';
import './App.css';

function App() {
  const { user, loading, isAuthenticated, isAdmin } = useAuth();

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner">Loading...</div>
      </div>
    );
  }

  if (!isAuthenticated()) {
    return (
      <div className="app">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/books" element={<BookCatalog />} />
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </div>
    );
  }

  return (
    <div className="app">
      <Navbar />
      <main className="main-content">
        <Routes>
          {/* Public routes */}
          <Route path="/books" element={<BookCatalog />} />
          
          {/* Member routes */}
          <Route path="/member/dashboard" element={<MemberDashboard />} />
          <Route path="/member/profile" element={<MemberProfile />} />
          <Route path="/member/loans" element={<MemberLoans />} />
          
          {/* Admin routes */}
          {isAdmin() && (
            <>
              <Route path="/admin/dashboard" element={<AdminDashboard />} />
              <Route path="/admin/members" element={<AdminMembers />} />
              <Route path="/admin/books" element={<AdminBooks />} />
              <Route path="/admin/loans" element={<AdminLoans />} />
            </>
          )}
          
          {/* Default redirects */}
          <Route 
            path="/" 
            element={
              <Navigate to={isAdmin() ? "/admin/dashboard" : "/member/dashboard"} />
            } 
          />
          <Route 
            path="*" 
            element={
              <Navigate to={isAdmin() ? "/admin/dashboard" : "/member/dashboard"} />
            } 
          />
        </Routes>
      </main>
    </div>
  );
}

export default App;
