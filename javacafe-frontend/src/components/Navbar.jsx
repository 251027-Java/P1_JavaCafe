import { Link, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import javaLogo from '../assets/images/logo.png';

function Navbar() {
    const navigate = useNavigate();
    const [cartItemCount, setCartItemCount] = useState(0);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userEmail, setUserEmail] = useState(null);
    const [userFirstName, setUserFirstName] = useState(null);

    useEffect(() => {
        // Function to update cart count
        const updateCartCount = () => {
            const savedCart = localStorage.getItem('cart');
            if (savedCart) {
                try {
                    const cart = JSON.parse(savedCart);
                    const totalItems = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
                    setCartItemCount(totalItems);
                } catch (e) {
                    setCartItemCount(0);
                }
            } else {
                setCartItemCount(0);
            }
        };

        // Function to check login status
        const checkLoginStatus = () => {
            const token = localStorage.getItem('token');
            if (token) {
                setIsLoggedIn(true);
                try {
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    setUserEmail(payload.email || null);
                    setUserFirstName(payload.firstName || null);
                } catch (e) {
                    // If token decode fails, just show logged in
                    setUserEmail(null);
                    setUserFirstName(null);
                }
            } else {
                setIsLoggedIn(false);
                setUserEmail(null);
                setUserFirstName(null);
            }
        };

        updateCartCount();
        checkLoginStatus();

        window.addEventListener('cartUpdated', updateCartCount);
        window.addEventListener('storage', updateCartCount);
        window.addEventListener('userLoggedIn', checkLoginStatus);
        window.addEventListener('userLoggedOut', checkLoginStatus);

        return () => {
            window.removeEventListener('cartUpdated', updateCartCount);
            window.removeEventListener('storage', updateCartCount);
            window.removeEventListener('userLoggedIn', checkLoginStatus);
            window.removeEventListener('userLoggedOut', checkLoginStatus);
        };
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        setIsLoggedIn(false);
        setUserEmail(null);
        setUserFirstName(null);
        window.dispatchEvent(new Event('userLoggedOut'));
        navigate('/api/menu');
    };

    return (
        <nav className="bg-amber-900 text-white shadow-lg relative">
            <div className="container mx-auto px-4 py-4">
                <div className="flex items-center justify-between">
                    <div className="flex-1"></div>
                    <div className="flex-1 text-center">
                        <div className="flex items-center justify-center gap-3 mb-4">
                            <h1 className="text-5xl font-black" style={{ fontFamily: "'Playfair Display', serif", letterSpacing: '0.05em', textShadow: '2px 2px 4px rgba(0,0,0,0.3)' }}>
                                JAVA CAFE
                            </h1>
                            <img 
                                src={javaLogo} 
                                alt="Java Cafe Logo" 
                                className="h-12 w-12 object-contain"
                                style={{ filter: 'drop-shadow(2px 2px 4px rgba(0,0,0,0.3))' }}
                                onError={(e) => {
                                    e.target.style.display = 'none';
                                    const parent = e.target.parentNode;
                                    if (!parent.querySelector('.logo-fallback')) {
                                        const fallback = document.createElement('span');
                                        fallback.className = 'logo-fallback text-4xl';
                                        fallback.textContent = '☕';
                                        fallback.style.filter = 'drop-shadow(2px 2px 4px rgba(0,0,0,0.3))';
                                        parent.appendChild(fallback);
                                    }
                                }}
                            />
                        </div>
                    </div>
                    <div className="flex-1 flex justify-end items-center gap-4">
                        {/* Cart Icon */}
                        <Link 
                            to="/api/cart" 
                            className="relative hover:text-amber-200 transition-colors duration-200 p-2"
                            aria-label="Shopping Cart"
                        >
                            <svg 
                                xmlns="http://www.w3.org/2000/svg" 
                                className="h-8 w-8" 
                                fill="none" 
                                viewBox="0 0 24 24" 
                                stroke="currentColor"
                            >
                                <path 
                                    strokeLinecap="round" 
                                    strokeLinejoin="round" 
                                    strokeWidth={2} 
                                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" 
                                />
                            </svg>
                            {cartItemCount > 0 && (
                                <span className="absolute -top-1 -right-1 bg-red-600 text-white text-xs font-bold rounded-full h-6 w-6 flex items-center justify-center">
                                    {cartItemCount > 99 ? '99+' : cartItemCount}
                                </span>
                            )}
                        </Link>
                        
                        {/* Profile Icon / Login */}
                        {isLoggedIn ? (
                            <div className="relative group">
                                <button
                                    className="hover:text-amber-200 transition-colors duration-200 p-2"
                                    aria-label="User Profile"
                                >
                                    <svg 
                                        xmlns="http://www.w3.org/2000/svg" 
                                        className="h-8 w-8" 
                                        fill="none" 
                                        viewBox="0 0 24 24" 
                                        stroke="currentColor"
                                    >
                                        <path 
                                            strokeLinecap="round" 
                                            strokeLinejoin="round" 
                                            strokeWidth={2} 
                                            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" 
                                        />
                                    </svg>
                                </button>
                                {/* Dropdown Menu */}
                                <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50">
                                    {isLoggedIn && (
                                        <div className="px-4 py-2 text-sm text-gray-700 border-b border-gray-200">
                                            {userFirstName ? (
                                                <>
                                                    <span className="font-medium">Hi </span>
                                                    <span className="text-amber-700 font-semibold">{userFirstName}</span>
                                                </>
                                            ) : userEmail ? (
                                                <>
                                                    <span className="font-medium">Hi </span>
                                                    <span className="text-amber-700 font-semibold truncate block">{userEmail}</span>
                                                </>
                                            ) : (
                                                <span className="font-medium text-gray-700">Logged in</span>
                                            )}
                                        </div>
                                    )}
                                    <button
                                        onClick={handleLogout}
                                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                                    >
                                        Logout
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <Link 
                                to="/api/login" 
                                className="hover:text-amber-200 transition-colors duration-200 p-2"
                                aria-label="Login"
                            >
                                <svg 
                                    xmlns="http://www.w3.org/2000/svg" 
                                    className="h-8 w-8" 
                                    fill="none" 
                                    viewBox="0 0 24 24" 
                                    stroke="currentColor"
                                >
                                    <path 
                                        strokeLinecap="round" 
                                        strokeLinejoin="round" 
                                        strokeWidth={2} 
                                        d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" 
                                    />
                                </svg>
                            </Link>
                        )}
                    </div>
                </div>
                <div className="flex flex-wrap justify-center gap-4 md:gap-6">
                    <Link 
                        to="/api" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Home
                    </Link>
                    <Link 
                        to="/api/menu" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Menu
                    </Link>
                    <Link 
                        to="/api/contact-us" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Contact Us
                    </Link>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
