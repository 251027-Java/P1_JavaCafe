import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from './useAuth'; 

// === CONFIGURATION START ===
const backendUrl = 'http://localhost:8080';
const REGISTER_ENDPOINT = `${backendUrl}/api/auth/register`; 
const LOGIN_ENDPOINT = `${backendUrl}/api/auth/login`; 
// === CONFIGURATION END ===

function LoginPage() {
    // Get auth functions and state
    const { login, isAuthenticated } = useAuth(); 
    
    const navigate = useNavigate();
    const location = useLocation(); // To read state and query parameters

    // 1. Determine the return path from the state passed by PlaceOrderPage
    //    Default to the menu path if no 'from' state is provided.
    const redirectPath = location.state?.from || '/api/menu';

    // 2. Read the URL query parameter to check for registration intent
    const isRegisterModeInitial = new URLSearchParams(location.search).get('mode') === 'register';

    // State to control which form is currently visible (Login vs. Register)
    const [isRegistering, setIsRegistering] = useState(isRegisterModeInitial);
    
    // Form States
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    // New states for registration
    const [firstName, setFirstName] = useState(''); 
    const [lastName, setLastName] = useState(''); 
    
    // State for loading and errors
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // If the user is already authenticated, redirect them away
    if (isAuthenticated) {
        // Redirect to the path they were trying to go to (e.g., /api/cart)
        navigate(redirectPath, { replace: true });
        return null; 
    }

    // Effect to reset error message when switching modes
    useEffect(() => {
        setError(null);
    }, [isRegistering]);

    // Function to handle switching between Login and Register views
    const toggleMode = (mode) => {
        setIsRegistering(mode === 'register');
        setError(null);
        
        // Change URL to reflect the mode, retaining the 'from' state
        const basePath = location.pathname.split('?')[0];
        const newPath = mode === 'register' 
            ? `${basePath}?mode=register`
            : basePath;
            
        // Use replace: true so the browser back button works cleanly
        navigate(newPath, { replace: true, state: location.state });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            if (isRegistering) {
                // --- REGISTRATION LOGIC ---
                if (!firstName || !lastName) {
                    throw new Error("First name and last name are required for registration.");
                }

                const registerPayload = {
                    email: email,
                    password: password,
                    firstName: firstName,
                    lastName: lastName,
                };

                const response = await fetch(REGISTER_ENDPOINT, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(registerPayload),
                });
                
                if (!response.ok) {
                    const errorDetail = await response.text();
                    throw new Error(`Registration failed (Status ${response.status}): ${errorDetail.substring(0, 50)}...`);
                }

                const authResponse = await response.json();
                
                // Assuming successful registration also provides a token and logs the user in
                const { token, userId, email: registeredEmail, firstName: regFirstName, lastName: regLastName } = authResponse;
                
                // 🚀 Call the Context function to store the token and user data
                login(token, { userId, email: registeredEmail, firstName: regFirstName, lastName: regLastName });

            } else {
                // --- LOGIN LOGIC (Uses original logic) ---
                const loginPayload = {
                    email: email,
                    password: password,
                };
                
                const response = await fetch(LOGIN_ENDPOINT, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(loginPayload),
                });

                if (!response.ok) {
                    const errorDetail = await response.text(); 
                    if (response.status === 401) {
                        throw new Error("Login failed: Invalid email or password.");
                    }
                    throw new Error(`Login failed (Status ${response.status}): ${errorDetail.substring(0, 50)}...`);
                }

                const authResponse = await response.json();
                const { token, userId, email: loggedInEmail, firstName: logFirstName, lastName: logLastName } = authResponse;
                
                // 🚀 Call the Context function to store the token and user data
                login(token, { userId, email: loggedInEmail, firstName: logFirstName, lastName: logLastName });
            }

            // Redirect the user to the saved path (from the cart or default menu)
            navigate(redirectPath, { replace: true });

        } catch (e) {
            console.error("Auth API Error:", e);
            setError(e.message || `An unknown error occurred during ${isRegistering ? 'registration' : 'login'}.`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mx-auto px-4 py-12 max-w-sm">
            <h1 className="text-3xl font-bold text-center text-amber-900 mb-6">
                {isRegistering ? 'Create Your Account' : 'Member Login'}
            </h1>
            
            {/* The line below has been removed: 
            <p className="text-center text-sm text-gray-500 mb-4">
                You will be redirected to the Cart page (**{redirectPath}**) after success.
            </p> */}
            
            <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-xl space-y-4">
                
                {error && (
                    <div className="p-3 text-sm text-red-700 bg-red-100 rounded-lg">
                        {error}
                    </div>
                )}
                
                {/* Registration Specific Fields */}
                {isRegistering && (
                    <>
                        <input
                            type="text"
                            placeholder="First Name"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            required
                            disabled={loading}
                            className="w-full p-3 border border-gray-300 rounded-md focus:ring-amber-500 focus:border-amber-500"
                        />
                        <input
                            type="text"
                            placeholder="Last Name"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                            required
                            disabled={loading}
                            className="w-full p-3 border border-gray-300 rounded-md focus:ring-amber-500 focus:border-amber-500"
                        />
                    </>
                )}

                {/* Shared Fields */}
                <input
                    type="email"
                    placeholder="Email Address"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    disabled={loading}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-amber-500 focus:border-amber-500"
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    disabled={loading}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-amber-500 focus:border-amber-500"
                />
                
                <button
                    type="submit"
                    disabled={loading}
                    className={`w-full py-3 rounded-lg font-bold text-lg transition ${
                        loading 
                            ? 'bg-gray-400 text-gray-700 cursor-not-allowed' 
                            : 'bg-amber-600 text-white hover:bg-amber-700'
                    }`}
                >
                    {loading 
                        ? 'Processing...' 
                        : isRegistering 
                            ? 'Create Account' 
                            : 'Log In'}
                </button>
            </form>

            {/* Toggle Link to switch views */}
            <p className="text-center mt-6 text-gray-600">
                {isRegistering ? "Already a member?" : "New user?"}
                <button 
                    onClick={() => toggleMode(isRegistering ? 'login' : 'register')} 
                    className="ml-2 text-blue-600 hover:underline font-semibold"
                    disabled={loading}
                >
                    {isRegistering ? 'Log in here' : 'Create an account'}
                </button>
            </p>
        </div>
    );
}

export default LoginPage;