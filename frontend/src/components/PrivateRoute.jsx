import React from 'react';
import { Navigate } from 'react-router-dom';

// Wraps protected pages
// If no token → redirect to login
// If token exists → show the page
const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" />;
};

export default PrivateRoute;