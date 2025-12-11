// src/components/Navbar.tsx

import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
// import { isTokenValid, logout } from '../services/authService'; // <-- Comment out or remove these
// import { getUserRole } from '../utils/jwtUtils'; // <-- Comment out or remove these

// =========================================================
// !!! TEMPORARY MOCK FUNCTIONS TO BYPASS AUTH LOGIC !!!
// =========================================================

// This will always return false so the Navbar shows the "Sign In" button
const isTokenValid = () => false; 
const getUserRole = () => null; 
const logout = () => { console.log("Logout function mocked: Token not cleared."); };

// NOTE: You still need to manually remove the unused imports in a real TS file
// or your linter might complain if you don't comment out the original imports above.

// =========================================================
// !!! END MOCK !!!
// =========================================================

const Navbar: React.FC = () => {
  const navigate = useNavigate();
  
  // NOTE: loggedIn will now always be false
  const loggedIn = isTokenValid(); 
  
  // NOTE: userRole will now always be null
  const userRole = getUserRole(); 

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Tailwind classes for a modern, dark navy header
  return (
    <nav className="bg-gray-800 p-4 shadow-lg sticky top-0 z-10">
      <div className="container mx-auto flex justify-between items-center">
        {/* Logo/Home Link */}
        <Link to="/" className="text-white text-2xl font-bold hover:text-green-400 transition duration-300">
          JavaCafe
        </Link>

        {/* Navigation Links */}
        <div className="flex space-x-6 items-center">
          {/* Public Link */}
          <Link to="/menu" className="text-gray-300 hover:text-white transition duration-300">
            Menu
          </Link>

          {/* Conditional Links based on Authentication Status */}
          {loggedIn ? (
            <>
              {/* These links will NOT show since loggedIn is false */}
              {userRole === 'ADMIN' && (
                <Link to="/admin" className="text-yellow-400 hover:text-yellow-300 transition duration-300 font-medium">
                  Admin Portal
                </Link>
              )}
              
              {/* ... other links ... */}

              {/* Logout Button */}
              <button
                onClick={handleLogout}
                className="bg-red-500 hover:bg-red-600 text-white text-sm font-medium py-1 px-3 rounded-full transition duration-300"
              >
                Logout
              </button>
            </>
          ) : (
            // Not Logged In Links - THIS WILL ALWAYS SHOW
            <Link to="/login" className="bg-green-500 hover:bg-green-600 text-white text-sm font-medium py-1 px-3 rounded-full transition duration-300">
              Sign In
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;