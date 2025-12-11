// src/pages/LoginPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/authService';;

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(''); // Clear previous errors

    try {
      const response = await AuthService.login({ email, password });
      
      // Get the user role to decide where to navigate
      const role = localStorage.getItem('role');

      // Navigate based on role after successful login
      if (role === 'ADMIN') {
        navigate('/admin', { replace: true });
      } else if (role === 'CUSTOMER') {
        navigate('/orders', { replace: true });
      } else {
         // Default if role is undefined or unexpected
         navigate('/', { replace: true });
      }

    } catch (err: any) {
      // Handle authentication failures (e.g., 401 Unauthorized)
      const errorMessage = err.response?.data?.message || 'Login failed. Check credentials and server status.';
      setError(errorMessage);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-xl shadow-2xl">
        <h2 className="text-2xl font-bold text-center text-gray-900">Sign in to JavaCafe</h2>
        
        <form className="space-y-6" onSubmit={handleLogin}>
          {/* Email Input */}
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email address</label>
            <input
              id="email"
              name="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          {/* Password Input */}
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">Password</label>
            <input
              id="password"
              name="password"
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          {/* Error Message */}
          {error && (
            <p className="text-sm font-medium text-red-600 text-center">{error}</p>
          )}

          {/* Submit Button (Uses Tailwind classes) */}
          <button
            type="submit"
            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Sign in
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;