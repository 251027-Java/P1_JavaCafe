import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, register } from '../services/AuthService';

function LoginPage() {
    const navigate = useNavigate();
    const [mode, setMode] = useState('login'); // 'login', 'register', or 'guest'
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const [loginEmail, setLoginEmail] = useState('');
    const [loginPassword, setLoginPassword] = useState('');

    const [registerEmail, setRegisterEmail] = useState('');
    const [registerPassword, setRegisterPassword] = useState('');
    const [registerConfirmPassword, setRegisterConfirmPassword] = useState('');
    const [registerFirstName, setRegisterFirstName] = useState('');
    const [registerLastName, setRegisterLastName] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            if (!loginEmail.trim() || !loginPassword.trim()) {
                throw new Error('Please enter both email and password');
            }

            const token = await login(loginEmail.trim(), loginPassword);
            
            // Store token in localStorage
            localStorage.setItem('token', token);

            window.dispatchEvent(new Event('userLoggedIn'));
            
            setSuccess('Login successful! Redirecting...');
            
            setTimeout(() => {
                navigate('/api/menu');
            }, 1000);
        } catch (err) {
            setError(err.message || 'Login failed. Please check your credentials.');
        } finally {
            setLoading(false);
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            // Validation
            if (!registerEmail.trim() || !registerPassword.trim() || !registerFirstName.trim() || !registerLastName.trim()) {
                throw new Error('Please fill in all fields');
            }

            if (registerPassword !== registerConfirmPassword) {
                throw new Error('Passwords do not match');
            }

            if (registerPassword.length < 6) {
                throw new Error('Password must be at least 6 characters long');
            }

            // Email validation
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(registerEmail.trim())) {
                throw new Error('Please enter a valid email address');
            }

            const token = await register(
                registerEmail.trim(),
                registerPassword,
                registerFirstName.trim(),
                registerLastName.trim()
            );
            
            // Store token in localStorage
            localStorage.setItem('token', token);
            
            // Dispatch event to update navbar
            window.dispatchEvent(new Event('userLoggedIn'));
            
            setSuccess('Registration successful! You are now logged in. Redirecting...');
            
            // Redirect to menu
            setTimeout(() => {
                navigate('/api/menu');
            }, 1500);
        } catch (err) {
            setError(err.message || 'Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleGuestCheckout = () => {
        navigate('/api/cart');
    };

    return (
        <div className="container mx-auto px-4 py-8 max-w-2xl">
            <div className="bg-white rounded-lg shadow-xl p-8">
                <h1 className="text-4xl font-bold text-amber-900 mb-2 text-center">Welcome to Java Cafe</h1>
                <p className="text-center text-gray-600 mb-8">Sign in to your account or continue as a guest</p>

                {/* Mode Tabs */}
                <div className="flex justify-center mb-6 border-b border-gray-200">
                    <button
                        onClick={() => {
                            setMode('login');
                            setError(null);
                            setSuccess(null);
                        }}
                        className={`px-6 py-3 font-semibold transition-colors ${
                            mode === 'login'
                                ? 'text-amber-600 border-b-2 border-amber-600'
                                : 'text-gray-600 hover:text-amber-600'
                        }`}
                    >
                        Login
                    </button>
                    <button
                        onClick={() => {
                            setMode('register');
                            setError(null);
                            setSuccess(null);
                        }}
                        className={`px-6 py-3 font-semibold transition-colors ${
                            mode === 'register'
                                ? 'text-amber-600 border-b-2 border-amber-600'
                                : 'text-gray-600 hover:text-amber-600'
                        }`}
                    >
                        Create Account
                    </button>
                </div>

                {/* Error/Success Messages */}
                {error && (
                    <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
                        {error}
                    </div>
                )}
                {success && (
                    <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
                        {success}
                    </div>
                )}

                {/* Login Form */}
                {mode === 'login' && (
                    <form onSubmit={handleLogin} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Email *
                            </label>
                            <input
                                type="email"
                                value={loginEmail}
                                onChange={(e) => setLoginEmail(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                placeholder="your.email@example.com"
                                required
                                disabled={loading}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Password *
                            </label>
                            <input
                                type="password"
                                value={loginPassword}
                                onChange={(e) => setLoginPassword(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                placeholder="Enter your password"
                                required
                                disabled={loading}
                            />
                        </div>
                        <button
                            type="submit"
                            disabled={loading}
                            className={`w-full py-3 rounded-lg font-semibold text-lg transition-colors ${
                                loading
                                    ? 'bg-gray-400 text-gray-700 cursor-not-allowed'
                                    : 'bg-amber-600 text-white hover:bg-amber-700'
                            }`}
                        >
                            {loading ? 'Logging in...' : 'Login'}
                        </button>
                    </form>
                )}

                {/* Register Form */}
                {mode === 'register' && (
                    <form onSubmit={handleRegister} className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    First Name *
                                </label>
                                <input
                                    type="text"
                                    value={registerFirstName}
                                    onChange={(e) => setRegisterFirstName(e.target.value)}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                    placeholder="John"
                                    required
                                    disabled={loading}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Last Name *
                                </label>
                                <input
                                    type="text"
                                    value={registerLastName}
                                    onChange={(e) => setRegisterLastName(e.target.value)}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                    placeholder="Doe"
                                    required
                                    disabled={loading}
                                />
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Email *
                            </label>
                            <input
                                type="email"
                                value={registerEmail}
                                onChange={(e) => setRegisterEmail(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                placeholder="your.email@example.com"
                                required
                                disabled={loading}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Password *
                            </label>
                            <input
                                type="password"
                                value={registerPassword}
                                onChange={(e) => setRegisterPassword(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                placeholder="At least 6 characters"
                                required
                                disabled={loading}
                                minLength={6}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Confirm Password *
                            </label>
                            <input
                                type="password"
                                value={registerConfirmPassword}
                                onChange={(e) => setRegisterConfirmPassword(e.target.value)}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                placeholder="Re-enter your password"
                                required
                                disabled={loading}
                                minLength={6}
                            />
                        </div>
                        <button
                            type="submit"
                            disabled={loading}
                            className={`w-full py-3 rounded-lg font-semibold text-lg transition-colors ${
                                loading
                                    ? 'bg-gray-400 text-gray-700 cursor-not-allowed'
                                    : 'bg-amber-600 text-white hover:bg-amber-700'
                            }`}
                        >
                            {loading ? 'Creating Account...' : 'Create Account'}
                        </button>
                    </form>
                )}

                {/* Guest Checkout Option */}
                <div className="mt-6 pt-6 border-t border-gray-200">
                    <p className="text-center text-gray-600 mb-4">Don't want to create an account?</p>
                    <button
                        onClick={handleGuestCheckout}
                        className="w-full py-3 bg-white border-2 border-amber-600 text-amber-700 rounded-lg hover:bg-amber-50 transition-colors font-semibold"
                    >
                        Continue as Guest
                    </button>
                </div>

                {/* Back to Menu */}
                <div className="mt-4 text-center">
                    <button
                        onClick={() => navigate('/api/menu')}
                        className="text-amber-700 hover:text-amber-900 transition-colors font-medium"
                    >
                        ← Back to Menu
                    </button>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;

