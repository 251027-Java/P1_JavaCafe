import React, { useState, useContext, createContext } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    
    // --- SAFE STATE INITIALIZATION: SWITCHED TO SESSIONSTORAGE ---

    // 1. Initialize token from sessionStorage
    const initialToken = sessionStorage.getItem('jwtToken'); // 👈 CHANGED
    const [token, setToken] = useState(initialToken);
    const [isAuthenticated, setIsAuthenticated] = useState(!!initialToken);

    // 2. Initialize user data from sessionStorage
    const [user, setUser] = useState(() => {
        const storedUser = sessionStorage.getItem('user'); 
        
        if (storedUser) {
            try {
                return JSON.parse(storedUser);
            } catch (e) {
                console.error("Auth context failed to parse 'user' data from sessionStorage:", e);
                // Clear bad data from sessionStorage
                sessionStorage.removeItem('user'); 
                return null;
            }
        }
        return null;
    }); 

    /**
     * Handles user login, storing the token and user data in sessionStorage.
     */
    const login = (newToken, userData) => {
        // Token storage
        sessionStorage.setItem('jwtToken', newToken); 
        setToken(newToken);
        setIsAuthenticated(true);
        
        // Store user data
        sessionStorage.setItem('user', JSON.stringify(userData)); 
        setUser(userData);
    };

    /**
     * Handles user logout, manually clearing sessionStorage.
     */
    const logout = () => {
        sessionStorage.removeItem('jwtToken'); 
        sessionStorage.removeItem('user'); 
        setToken(null);
        setIsAuthenticated(false);
        setUser(null);
    };
    
    // NEW FUNCTION: Explicitly returns the token state.
    const getToken = () => token;

    return (
        <AuthContext.Provider value={{ 
            token, 
            isAuthenticated, 
            login, 
            logout,
            getToken,
            user 
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};