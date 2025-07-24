// src/components/Navbar.jsx
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname.startsWith(path) ? 'nav-link active' : 'nav-link';
  };

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="nav-logo">
          NTUC Library Management System
        </Link>
        
        <div className="nav-menu">
          <Link to="/books" className={isActive('/books')}>
            Book Catalog
          </Link>
          
          {isAdmin() ? (
            <>
              <Link to="/admin/dashboard" className={isActive('/admin/dashboard')}>
                Dashboard
              </Link>
              <Link to="/admin/members" className={isActive('/admin/members')}>
                Members
              </Link>
              <Link to="/admin/books" className={isActive('/admin/books')}>
                Manage Books
              </Link>
              <Link to="/admin/loans" className={isActive('/admin/loans')}>
                Loans
              </Link>
            </>
          ) : (
            <>
              <Link to="/member/dashboard" className={isActive('/member/dashboard')}>
                Dashboard
              </Link>
              <Link to="/member/profile" className={isActive('/member/profile')}>
                Profile
              </Link>
              <Link to="/member/loans" className={isActive('/member/loans')}>
                My Loans
              </Link>
            </>
          )}
        </div>
        
        <div className="nav-user">
          <span className="user-info">
            Welcome, {user?.name || user?.username}
            {isAdmin() && <span className="admin-badge">Admin</span>}
          </span>
          <button onClick={logout} className="logout-btn">
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;

