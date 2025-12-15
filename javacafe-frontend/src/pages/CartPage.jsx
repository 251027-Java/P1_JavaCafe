import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function CartPage() {
    const navigate = useNavigate();
    const [cart, setCart] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [orderPlaced, setOrderPlaced] = useState(false);
    const [orderId, setOrderId] = useState(null);
    const [checkoutMode, setCheckoutMode] = useState(null); // 'guest' or 'login'
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [token, setToken] = useState(null);
    
    // Guest checkout form state
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    
    // Login checkout form state
    const [loginEmail, setLoginEmail] = useState('');
    const [loginPassword, setLoginPassword] = useState('');

    useEffect(() => {
        // Check if user is logged in
        const savedToken = localStorage.getItem('token');
        if (savedToken) {
            setToken(savedToken);
            setIsLoggedIn(true);
        }

        // Load cart from localStorage
        const savedCart = localStorage.getItem('cart');
        if (savedCart) {
            try {
                setCart(JSON.parse(savedCart));
            } catch (e) {
                console.error('Error reading cart:', e);
                setCart([]);
            }
        }
    }, []);

    const updateQuantity = (id, delta) => {
        const updatedCart = cart.map(item => {
            if (item.id === id) {
                const newQuantity = Math.max(0, (item.quantity || 0) + delta);
                if (newQuantity === 0) {
                    return null; // Mark for removal
                }
                return { ...item, quantity: newQuantity };
            }
            return item;
        }).filter(item => item !== null);
        
        setCart(updatedCart);
        localStorage.setItem('cart', JSON.stringify(updatedCart));
        window.dispatchEvent(new Event('cartUpdated'));
    };

    const removeItem = (id) => {
        const updatedCart = cart.filter(item => item.id !== id);
        setCart(updatedCart);
        localStorage.setItem('cart', JSON.stringify(updatedCart));
        window.dispatchEvent(new Event('cartUpdated'));
    };

    const calculateTotal = () => {
        return cart.reduce((total, item) => {
            const price = typeof item.price === 'number' ? item.price : parseFloat(item.price || 0);
            const quantity = item.quantity || 0;
            return total + (price * quantity);
        }, 0);
    };

    const handleLogin = async () => {
        if (!loginEmail.trim() || !loginPassword.trim()) {
            setError('Please enter both email and password');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: loginEmail.trim(),
                    password: loginPassword
                })
            });

            if (!response.ok) {
                throw new Error('Invalid email or password');
            }

            const data = await response.json();
            const newToken = data.token;
            
            setToken(newToken);
            setIsLoggedIn(true);
            localStorage.setItem('token', newToken);
            setCheckoutMode(null);
            setError(null);
        } catch (e) {
            console.error('Login error:', e);
            setError(e.message || 'Failed to login. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleGuestCheckout = async () => {
        if (cart.length === 0) {
            setError('Your cart is empty');
            return;
        }

        if (!firstName.trim() || !lastName.trim() || !email.trim()) {
            setError('Please fill in all required fields');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const orderData = {
                firstName: firstName.trim(),
                lastName: lastName.trim(),
                email: email.trim(),
                items: cart.map(item => ({
                    productId: item.id,
                    quantity: item.quantity || 1
                }))
            };

            const response = await fetch('/api/cart/guest/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(orderData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP ${response.status}: ${response.statusText}`);
            }

            const orderResult = await response.json();
            setOrderId(orderResult.orderid || orderResult.orderId);
            setOrderPlaced(true);
            
            // Clear cart after successful order
            localStorage.removeItem('cart');
            setCart([]);
            window.dispatchEvent(new Event('cartUpdated'));
        } catch (e) {
            console.error('Order placement error:', e);
            setError(e.message || 'Failed to place order. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleAuthenticatedCheckout = async () => {
        if (cart.length === 0) {
            setError('Your cart is empty');
            return;
        }

        if (!token) {
            setError('Please login first');
            setCheckoutMode('login');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const orderData = {
                items: cart.map(item => ({
                    productId: item.id,
                    quantity: item.quantity || 1
                }))
            };

            const response = await fetch('/api/cart/new', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(orderData)
            });

            if (!response.ok) {
                if (response.status === 401) {
                    // Token expired or invalid
                    localStorage.removeItem('token');
                    setToken(null);
                    setIsLoggedIn(false);
                    setError('Session expired. Please login again.');
                    setCheckoutMode('login');
                    return;
                }
                const errorText = await response.text();
                throw new Error(errorText || `HTTP ${response.status}: ${response.statusText}`);
            }

            const orderResult = await response.json();
            setOrderId(orderResult.orderid || orderResult.orderId);
            setOrderPlaced(true);
            
            // Clear cart after successful order
            localStorage.removeItem('cart');
            setCart([]);
            window.dispatchEvent(new Event('cartUpdated'));
        } catch (e) {
            console.error('Order placement error:', e);
            setError(e.message || 'Failed to place order. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (orderPlaced) {
        return (
            <div className="container mx-auto px-4 py-8 max-w-2xl">
                <div className="bg-white rounded-lg shadow-xl p-8 text-center">
                    <div className="mb-6">
                        <svg className="mx-auto h-16 w-16 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <h1 className="text-3xl font-bold text-amber-900 mb-4">Order Placed Successfully!</h1>
                    {orderId && (
                        <p className="text-lg text-gray-700 mb-2">
                            Order ID: <span className="font-semibold text-amber-700">{orderId}</span>
                        </p>
                    )}
                    <p className="text-gray-600 mb-6">
                        Thank you for your order! {email && `We'll send a confirmation email to ${email}.`}
                    </p>
                    <div className="flex gap-4 justify-center">
                        <button
                            onClick={() => navigate('/api/menu')}
                            className="px-6 py-3 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors font-semibold"
                        >
                            Continue Shopping
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    if (cart.length === 0) {
        return (
            <div className="container mx-auto px-4 py-8 max-w-2xl">
                <div className="bg-white rounded-lg shadow-xl p-8 text-center">
                    <h1 className="text-3xl font-bold text-amber-900 mb-4">Your Cart is Empty</h1>
                    <p className="text-gray-600 mb-6">Add some delicious items to your cart!</p>
                    <button
                        onClick={() => navigate('/api/menu')}
                        className="px-6 py-3 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors font-semibold"
                    >
                        Browse Menu
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8 max-w-6xl">
            <h1 className="text-3xl font-bold text-amber-900 mb-6">Shopping Cart</h1>
            
            {error && (
                <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
                    {error}
                </div>
            )}

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Cart Items */}
                <div className="lg:col-span-2">
                    <div className="bg-white rounded-lg shadow-md p-6">
                        <h2 className="text-2xl font-semibold text-amber-900 mb-4">Items</h2>
                        <div className="space-y-4">
                            {cart.map((item) => (
                                <div key={item.id} className="flex items-center justify-between p-4 border-b border-gray-200">
                                    <div className="flex-1">
                                        <h3 className="text-lg font-semibold text-amber-900">{item.name}</h3>
                                        <p className="text-amber-700 font-medium">
                                            ${typeof item.price === 'number' ? item.price.toFixed(2) : parseFloat(item.price || 0).toFixed(2)}
                                        </p>
                                    </div>
                                    <div className="flex items-center gap-4">
                                        <div className="flex items-center gap-2">
                                            <button
                                                onClick={() => updateQuantity(item.id, -1)}
                                                className="w-8 h-8 rounded-full bg-amber-100 text-amber-800 hover:bg-amber-200 transition-colors font-bold"
                                            >
                                                -
                                            </button>
                                            <span className="text-lg font-semibold w-8 text-center">{item.quantity || 1}</span>
                                            <button
                                                onClick={() => updateQuantity(item.id, 1)}
                                                className="w-8 h-8 rounded-full bg-amber-100 text-amber-800 hover:bg-amber-200 transition-colors font-bold"
                                            >
                                                +
                                            </button>
                                        </div>
                                        <p className="text-lg font-bold text-amber-700 w-20 text-right">
                                            ${((typeof item.price === 'number' ? item.price : parseFloat(item.price || 0)) * (item.quantity || 1)).toFixed(2)}
                                        </p>
                                        <button
                                            onClick={() => removeItem(item.id)}
                                            className="text-red-600 hover:text-red-800 transition-colors"
                                        >
                                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                            </svg>
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Checkout Section */}
                <div className="lg:col-span-1">
                    <div className="bg-white rounded-lg shadow-md p-6 sticky top-4">
                        <h2 className="text-2xl font-semibold text-amber-900 mb-4">Checkout</h2>
                        
                        {/* Total */}
                        <div className="border-t pt-4 mb-4">
                            <div className="flex justify-between items-center mb-2">
                                <span className="text-gray-700">Subtotal:</span>
                                <span className="text-lg font-semibold text-amber-700">
                                    ${calculateTotal().toFixed(2)}
                                </span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="text-gray-700 font-semibold">Total:</span>
                                <span className="text-2xl font-bold text-amber-700">
                                    ${calculateTotal().toFixed(2)}
                                </span>
                            </div>
                        </div>

                        {/* Checkout Mode Selection */}
                        {!checkoutMode && !isLoggedIn && (
                            <div className="space-y-3 mb-4">
                                <button
                                    onClick={() => setCheckoutMode('guest')}
                                    className="w-full py-3 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors font-semibold"
                                >
                                    Checkout as Guest
                                </button>
                                <button
                                    onClick={() => setCheckoutMode('login')}
                                    className="w-full py-3 bg-white text-amber-700 border-2 border-amber-600 rounded-lg hover:bg-amber-50 transition-colors font-semibold"
                                >
                                    Login to Checkout
                                </button>
                            </div>
                        )}

                        {/* Guest Checkout Form */}
                        {checkoutMode === 'guest' && (
                            <div className="space-y-4 mb-4">
                                <h3 className="text-lg font-semibold text-amber-900">Guest Checkout</h3>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        First Name *
                                    </label>
                                    <input
                                        type="text"
                                        value={firstName}
                                        onChange={(e) => setFirstName(e.target.value)}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                        required
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Last Name *
                                    </label>
                                    <input
                                        type="text"
                                        value={lastName}
                                        onChange={(e) => setLastName(e.target.value)}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                        required
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Email *
                                    </label>
                                    <input
                                        type="email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                        required
                                    />
                                </div>
                                <button
                                    onClick={handleGuestCheckout}
                                    disabled={loading}
                                    className={`w-full py-3 rounded-lg font-semibold text-lg transition-colors ${
                                        loading
                                            ? 'bg-gray-400 text-gray-700 cursor-not-allowed'
                                            : 'bg-amber-600 text-white hover:bg-amber-700'
                                    }`}
                                >
                                    {loading ? 'Placing Order...' : 'Place Order'}
                                </button>
                                <button
                                    onClick={() => setCheckoutMode(null)}
                                    className="w-full mt-2 py-2 text-amber-700 hover:text-amber-900 transition-colors font-medium text-sm"
                                >
                                    Cancel
                                </button>
                            </div>
                        )}

                        {/* Login Form */}
                        {checkoutMode === 'login' && !isLoggedIn && (
                            <div className="space-y-4 mb-4">
                                <h3 className="text-lg font-semibold text-amber-900">Login to Checkout</h3>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">
                                        Email *
                                    </label>
                                    <input
                                        type="email"
                                        value={loginEmail}
                                        onChange={(e) => setLoginEmail(e.target.value)}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
                                        required
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
                                        required
                                    />
                                </div>
                                <button
                                    onClick={handleLogin}
                                    disabled={loading}
                                    className={`w-full py-3 rounded-lg font-semibold text-lg transition-colors ${
                                        loading
                                            ? 'bg-gray-400 text-gray-700 cursor-not-allowed'
                                            : 'bg-amber-600 text-white hover:bg-amber-700'
                                    }`}
                                >
                                    {loading ? 'Logging in...' : 'Login'}
                                </button>
                                <button
                                    onClick={() => {
                                        setCheckoutMode(null);
                                        navigate('/api/login');
                                    }}
                                    className="w-full mt-2 py-2 text-amber-700 hover:text-amber-900 transition-colors font-medium text-sm"
                                >
                                    Don't have an account? Register
                                </button>
                                <button
                                    onClick={() => setCheckoutMode(null)}
                                    className="w-full mt-2 py-2 text-gray-600 hover:text-gray-800 transition-colors font-medium text-sm"
                                >
                                    Cancel
                                </button>
                            </div>
                        )}

                        {/* Authenticated Checkout */}
                        {isLoggedIn && checkoutMode !== 'guest' && checkoutMode !== 'login' && (
                            <div className="space-y-3">
                                <p className="text-sm text-gray-600 mb-2">
                                    Logged in and ready to checkout
                                </p>
                                <button
                                    onClick={handleAuthenticatedCheckout}
                                    disabled={loading || cart.length === 0}
                                    className={`w-full py-3 rounded-lg font-semibold text-lg transition-colors ${
                                        loading || cart.length === 0
                                            ? 'bg-gray-400 text-gray-700 cursor-not-allowed'
                                            : 'bg-amber-600 text-white hover:bg-amber-700'
                                    }`}
                                >
                                    {loading ? 'Placing Order...' : 'Place Order'}
                                </button>
                                <button
                                    onClick={() => {
                                        localStorage.removeItem('token');
                                        setToken(null);
                                        setIsLoggedIn(false);
                                        setCheckoutMode(null);
                                    }}
                                    className="w-full mt-2 py-2 text-gray-600 hover:text-gray-800 transition-colors font-medium text-sm"
                                >
                                    Logout
                                </button>
                            </div>
                        )}

                        <button
                            onClick={() => navigate('/api/menu')}
                            className="w-full mt-3 py-2 text-amber-700 hover:text-amber-900 transition-colors font-medium"
                        >
                            Continue Shopping
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default CartPage;

