// src/services/api.ts
import axios, { type AxiosInstance } from 'axios';

// 1. Define the base URL for your Spring Boot application
// Ensure this matches your Spring Boot server configuration
const BASE_URL = 'http://localhost:8080/api'; 

// 2. Create the Axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 3. Request Interceptor: Attach the JWT token to every request
apiClient.interceptors.request.use(
  (config) => {
    // Get the token from browser storage
    const token = localStorage.getItem('token'); 
    
    // If a token exists AND the URL is not the public login endpoint, attach it
    // NOTE: If you have many public endpoints, you might need more complex exclusion logic
    if (token && !config.url?.includes('/auth/login')) {
      // Set the standard Authorization header format
      config.headers.Authorization = `Bearer ${token}`; 
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Optional: Response Interceptor to handle 401/403 globally
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        // If we get a 401 and it's not the login attempt itself, log out the user
        if (error.response && error.response.status === 401 && error.config.url !== '/auth/login') {
            localStorage.removeItem('token');
            // Optionally redirect to login page here using react-router-dom
            // window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default apiClient;