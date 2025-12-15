import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';

function Navbar() {
    const [cartItemCount, setCartItemCount] = useState(0);

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

        updateCartCount();

        window.addEventListener('cartUpdated', updateCartCount);
        window.addEventListener('storage', updateCartCount);

        return () => {
            window.removeEventListener('cartUpdated', updateCartCount);
            window.removeEventListener('storage', updateCartCount);
        };
    }, []);

    return (
        <nav className="bg-amber-900 text-white shadow-lg relative">
            <div className="container mx-auto px-4 py-4">
                <div className="flex items-center justify-between">
                    <div className="flex-1"></div>
                    <div className="flex-1 text-center">
                        <h1 className="text-5xl font-black mb-4" style={{ fontFamily: "'Playfair Display', serif", letterSpacing: '0.05em', textShadow: '2px 2px 4px rgba(0,0,0,0.3)' }}>
                            JAVA CAFE
                        </h1>
                    </div>
                    <div className="flex-1 flex justify-end">
                        {/* Cart Icon*/}
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
                        to="/api/cart" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Cart
                    </Link>
                    <Link 
                        to="/api/contact-us" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Contact Us
                    </Link>
                    <Link 
                        to="/api/login" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Login
                    </Link>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
