import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const name = localStorage.getItem('name');

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path ? 'active' : '';

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        💰 Finance Tracker
      </div>
      <div className="navbar-links">
        <Link to="/" className={isActive('/')}>Dashboard</Link>
        <Link to="/transactions" className={isActive('/transactions')}>Transactions</Link>
        <Link to="/budget" className={isActive('/budget')}>Budget</Link>
        <Link to="/notifications" className={isActive('/notifications')}>🔔 Alerts</Link>
      </div>
      <div className="navbar-user">
        <span>Hi, {name}</span>
        <button onClick={handleLogout} className="logout-btn">Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;