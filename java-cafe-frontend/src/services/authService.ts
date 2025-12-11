// src/services/authService.ts

import apiClient from './api';

// Define the expected structure of the login response from your Spring Boot backend
interface LoginResponse {
  token: string;
  role: string;
}

// Define the required structure for the login request body
interface LoginRequest {
    email: string;
    password: string;
}

const login = async (credentials: LoginRequest): Promise<LoginResponse> => {
  try {
    const response = await apiClient.post<LoginResponse>('/auth/login', credentials);
    
    const { token, role } = response.data;
    
    if (token) {
      localStorage.setItem('token', token); 
      localStorage.setItem('role', role);
    }
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const logout = (): void => {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
};

const AuthService = {
  login,
  logout
};

export default AuthService;