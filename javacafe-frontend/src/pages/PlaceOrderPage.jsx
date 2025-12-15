// PlaceOrderPage.jsx
import React, { useState, useEffect, useCallback } from 'react'; 
import { useCart } from './CartContext'; 
import { useAuth } from './useAuth'; 
import { useNavigate, useLocation } from 'react-router-dom'; 

// === CONFIGURATION START ===
const PRODUCT_ID_FIELD_NAME = 'productId';
const backendUrl = 'http://localhost:8080';
// === CONFIGURATION END ===

function PlaceOrderPage() {
    const { 
        cartItems,          
        updateQuantity,     
        clearCart,          
        removeItem,         
        cartTotal           
    } = useCart();
    
    // INITIALIZE HOOKS
    const navigate = useNavigate();
    const location = useLocation();
    const { isAuthenticated, getToken, user } = useAuth(); 

    // Menu Data States (omitted for brevity)
    const [products, setProducts] = useState([]); 
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // Checkout State
    const [isCheckoutStarted, setIsCheckoutStarted] = useState(false);
    
    // Order Status States
    const [isProcessingOrder, setIsProcessingOrder] = useState(false);
    const [finalOrderTotal, setFinalOrderTotal] = useState(null); 
    const [confirmationMessage, setConfirmationMessage] = useState(null); 
    // Correct syntax from setOrderError(null) to useState(null)
    const [orderError, setOrderError] = useState(null); 
        
    // Guest Customer Information and form visibility
    const [isGuestFormVisible, setIsGuestFormVisible] = useState(false);
    const [guestEmail, setGuestEmail] = useState('');
    const [guestFirstName, setGuestFirstName] = useState('');
    const [guestLastName, setGuestLastName] = useState('');

    // --- Data Fetching Effect (Unchanged) ---
    useEffect(() => {
        setLoading(true);
        setError(null);
        
        fetch(`${backendUrl}/api/menu`)
            .then(response => {
                if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
                return response.json();
            })
            .then(data => {
                setProducts(Array.isArray(data) ? data : []);
            })
            .catch(e => {
                console.error("Fetch error:", e);
                setError("Failed to load data.");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [backendUrl]);


    // MEMBER ORDER FUNCTIONALITY 
    const submitMemberOrder = useCallback(async () => {
        if (cartItems.length === 0) {
            setOrderError("Cannot place an empty order.");
            return;
        }
        
        if (isProcessingOrder || confirmationMessage) return;

        setIsProcessingOrder(true);
        setOrderError(null);
        setFinalOrderTotal(null); 
        setConfirmationMessage(null);

        const orderItems = cartItems.map(item => ({
            [PRODUCT_ID_FIELD_NAME]: item.id, 
            quantity: item.quantity,
            price: item.price
        }));

        const memberOrderPayload = {
            items: orderItems,
            total: cartTotal, 
            orderType: "MEMBER" 
        };

        try {
            const token = getToken(); 
            const response = await fetch(`${backendUrl}/api/cart/member/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify(memberOrderPayload),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Member order failed: ${response.status} - ${errorText.substring(0, 100)}...`);
            }

            const result = await response.json();
            
            // 1. SAVE FINAL TOTAL *BEFORE* CLEARING CART
            const currentTotal = cartTotal; 
            setFinalOrderTotal(result.total || currentTotal); 

            // 2. CLEAN UP THE CART
            clearCart(); 
            setIsCheckoutStarted(false);
            setIsGuestFormVisible(false);
            
            // 3. SET CONFIRMATION MESSAGE (using saved user name)
            const memberFirstName = user?.firstName || 'Valued Member'; 
            const orderId = result.id || result.orderId || 'Unknown';

            setConfirmationMessage(
                `🎉 Thank you ${memberFirstName}, your order number is: ${orderId}`
            );
            
        } catch (e) {
            console.error("Error submitting member order:", e.message);
            setOrderError(`Failed to place member order: ${e.message}`);
        } finally {
            setIsProcessingOrder(false);
        }
    }, [cartItems, cartTotal, clearCart, getToken, user, isProcessingOrder, confirmationMessage, backendUrl]); 


    // REMOVED: The auto-submit useEffect for authenticated users is deliberately 
    // removed to ensure explicit submission via the "Place Order" button.

    // --- Guest Order Functionality (Unchanged) ---
    const submitGuestOrder = async () => {
        if (cartItems.length === 0) {
            setOrderError("Cannot place an empty order.");
            return;
        }

        if (!guestEmail || !guestFirstName || !guestLastName) {
            setOrderError("Please fill out all guest information fields.");
            return;
        }

        setIsProcessingOrder(true);
        setOrderError(null);
        setFinalOrderTotal(null); 
        setConfirmationMessage(null);

        const orderItems = cartItems.map(item => ({
            [PRODUCT_ID_FIELD_NAME]: item.id,
            quantity: item.quantity,
            price: item.price
        }));

        const guestOrderPayload = {
            items: orderItems,
            total: cartTotal,
            orderType: "GUEST",
            email: guestEmail,
            firstName: guestFirstName,
            lastName: guestLastName
        };

        try {
            const response = await fetch(`${backendUrl}/api/cart/guest/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(guestOrderPayload),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Guest order failed: ${response.status} - ${errorText.substring(0, 100)}...`);
            }

            const result = await response.json();

            // 1. SAVE FINAL TOTAL *BEFORE* CLEARING CART
            const currentTotal = cartTotal; 
            setFinalOrderTotal(result.total || currentTotal);

            // 2. CLEAN UP THE CART
            clearCart();
            setIsCheckoutStarted(false);
            setIsGuestFormVisible(false);

            // 3. SET CONFIRMATION MESSAGE 
            const orderId = result.id || result.orderId || 'Unknown';
            setConfirmationMessage(
                `🎉 Thank you ${guestFirstName}, your order number is: ${orderId}`
            );

        } catch (e) {
            console.error("Error submitting guest order:", e.message);
            setOrderError(`Failed to place guest order: ${e.message}`);
        } finally {
            setIsProcessingOrder(false);
        }
    };
    
    const handleGuestFormSubmit = (e) => {
        e.preventDefault(); 
        submitGuestOrder();
    };

    // --- Checkout Flow Functions (Unchanged) ---
    const handleStartCheckout = () => {
        if (cartItems.length === 0) {
            alert("Your cart is empty. Please add items before checking out.");
            return;
        }
        setIsCheckoutStarted(true);
        setIsGuestFormVisible(false);
        setFinalOrderTotal(null); 
        setConfirmationMessage(null); 
        setOrderError(null); 
    };

    const handleGuestCheckout = () => {
        setIsCheckoutStarted(false);
        setIsGuestFormVisible(true);
    };
    
    const handleLogin = () => {
        navigate('/api/auth/login', { state: { from: location.pathname } }); 
    };

    const handleCreateAccount = () => {
        navigate('/api/auth/register', { state: { from: location.pathname } }); 
    };
    
    // Function using the correct menu path
    const handleBackToMenu = () => {
        navigate('/api/menu'); 
    };

    // --- Rendered Component ---
    if (error && !loading) return <div className="text-center p-8 text-xl text-red-600">{error}</div>;

    const isOrderConfirmed = !!confirmationMessage;
    const currentTotalDisplay = isOrderConfirmed && finalOrderTotal !== null
        ? finalOrderTotal
        : cartTotal;

    const showCheckoutOptions = cartItems.length > 0 && finalOrderTotal === null;

    return (
        <div className="container mx-auto px-4 py-8 max-w-lg">
            <div className="w-full border border-gray-300 rounded-lg p-6 bg-gray-50 flex flex-col min-h-96">
                
                <h3 className="text-3xl font-bold text-amber-900 mb-6 border-b pb-2">🛒 Your Shopping Cart</h3>
                
                {cartItems.length === 0 && finalOrderTotal === null ? (
                    <p className="text-gray-500 italic flex-grow text-center flex items-center justify-center text-lg">
                        Your cart is empty.
                    </p>
                ) : (
                    <div className="space-y-4 flex-grow flex flex-col justify-between">
                        
                        {/* Cart Item Mapping (Unchanged) */}
                        <div className="space-y-3 overflow-y-auto max-h-[70vh] pr-2">
                             {cartItems.map(item => (
                                 <div key={item.id} className="flex justify-between items-center border-b pb-2">
                                     <div className="flex-grow pr-2">
                                         <p className="font-semibold text-base text-amber-900">{item.name}</p>
                                         <p className="text-sm text-gray-600">
                                             $ {item.price.toFixed(2)} x {item.quantity} 
                                             <span className="ml-2 font-bold text-amber-700">(${(item.price * item.quantity).toFixed(2)})</span>
                                         </p>
                                     </div>
                                     <div className="flex items-center space-x-1 shrink-0">
                                         <button onClick={() => { updateQuantity(item.id, item.quantity - 1); }} disabled={item.quantity === 1 || isProcessingOrder} className={`text-sm w-6 h-6 rounded-full transition ${ item.quantity === 1 || isProcessingOrder ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-amber-100 text-amber-800 hover:bg-amber-200' }`}>-</button>
                                         <span className="text-base font-medium w-4 text-center">{item.quantity}</span>
                                         <button onClick={() => updateQuantity(item.id, item.quantity + 1)} disabled={isProcessingOrder} className={`text-sm w-6 h-6 rounded-full transition ${ isProcessingOrder ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-amber-100 text-amber-800 hover:bg-amber-200' }`}>+</button>
                                     </div>
                                     <button onClick={() => removeItem(item.id)} disabled={isProcessingOrder} className={`ml-4 text-lg transition ${ isProcessingOrder ? 'text-gray-400 cursor-not-allowed' : 'text-red-600 hover:text-red-800' }`} title="Remove item completely">&times;</button>
                                 </div>
                             ))}
                        </div>
                        
                        {/* Summary and Checkout Options */}
                        <div className="pt-4 mt-auto">
                            <div className="border-t font-bold text-xl flex justify-between pt-4">
                                <span>Order Total:</span>
                                <span>${currentTotalDisplay.toFixed(2)}</span>
                            </div>
                            
                            {orderError && (
                                <p className="text-center text-sm text-red-600 font-medium mt-2 p-2 bg-red-100 rounded">
                                    {orderError}
                                </p>
                            )}
                            
                            {/* --- Start Checkout Logic --- */}
                            
                            {/* RENDER: Guest/Login Options or Initial Checkout Button */}
                            {showCheckoutOptions && !isAuthenticated && (
                                <>
                                    {isGuestFormVisible ? (
                                        // 1. RENDER: Guest Info Form
                                        <form onSubmit={handleGuestFormSubmit} className="mt-4 space-y-3 p-4 bg-white border border-green-300 rounded-lg shadow-md">
                                            <p className="text-center font-semibold text-green-700">Enter Guest Information</p>
                                            
                                            <input type="email" placeholder="Email*" value={guestEmail} onChange={(e) => setGuestEmail(e.target.value)} required disabled={isProcessingOrder} className="w-full p-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 disabled:bg-gray-100"/>
                                            <input type="text" placeholder="First Name*" value={guestFirstName} onChange={(e) => setGuestFirstName(e.target.value)} required disabled={isProcessingOrder} className="w-full p-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 disabled:bg-gray-100"/>
                                            <input type="text" placeholder="Last Name*" value={guestLastName} onChange={(e) => setGuestLastName(e.target.value)} required disabled={isProcessingOrder} className="w-full p-2 border border-gray-300 rounded-md focus:ring-green-500 focus:border-green-500 disabled:bg-gray-100"/>
                                            
                                            <button type="submit" disabled={isProcessingOrder} className={`w-full py-2 rounded-lg font-semibold transition ${isProcessingOrder ? 'bg-gray-400 text-gray-700 cursor-not-allowed' : 'bg-green-600 text-white hover:bg-green-700'}`}>
                                                {isProcessingOrder ? 'Submitting Order...' : 'Place Guest Order'}
                                            </button>
                                            <button type="button" onClick={() => { setIsGuestFormVisible(false); setIsCheckoutStarted(true); setOrderError(null); }} disabled={isProcessingOrder} className={`w-full py-2 rounded-lg font-semibold transition ${isProcessingOrder ? 'bg-red-300 text-white cursor-not-allowed' : 'bg-red-500 text-white hover:bg-red-600'}`}>
                                                Cancel
                                            </button>
                                        </form>

                                    ) : isCheckoutStarted ? (
                                        // 2. RENDER: 3 Options (Login/Register/Guest)
                                        <div className="mt-4 space-y-2 p-4 bg-white border border-blue-300 rounded-lg shadow-md">
                                            <p className="text-center font-semibold text-gray-700">How would you like to proceed?</p>
                                            <button onClick={handleGuestCheckout} disabled={isProcessingOrder} className={`w-full py-2 rounded-lg font-semibold transition ${isProcessingOrder ? 'bg-gray-400 text-gray-700 cursor-not-allowed' : 'bg-green-600 text-white hover:bg-green-700'}`}>
                                                Continue as Guest
                                            </button>
                                            <button onClick={handleLogin} className="w-full py-2 rounded-lg font-semibold bg-blue-600 text-white hover:bg-blue-700 transition">
                                                Login
                                            </button>
                                            <button onClick={handleCreateAccount} className="w-full py-2 rounded-lg font-semibold bg-purple-600 text-white hover:bg-purple-700 transition">
                                                Create Account
                                            </button>
                                        </div>

                                    ) : (
                                        // 3. RENDER: Initial Checkout Button
                                        <button
                                            onClick={handleStartCheckout}
                                            className="w-full mt-4 bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 font-bold transition text-lg"
                                        >
                                            Proceed to Checkout ({cartItems.length} items)
                                        </button>
                                    )}
                                </>
                            )}
                            
                            {/* RENDER: Member Checkout Button (Explicit Submission) */}
                            {showCheckoutOptions && isAuthenticated && !isProcessingOrder && (
                                <div className="mt-4 p-4 bg-blue-100 border border-blue-400 rounded-lg text-center">
                                    <p className="font-semibold text-blue-700">You are logged in as {user?.firstName || 'a member'}. Ready to submit!</p>
                                    <button
                                        onClick={submitMemberOrder} // 👈 **EXPLICIT SUBMISSION**
                                        disabled={isProcessingOrder}
                                        className="w-full mt-2 bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 font-bold transition text-lg"
                                    >
                                        Place Order as Member (${cartTotal.toFixed(2)})
                                    </button>
                                </div>
                            )}

                            {isProcessingOrder && (
                                <button
                                    disabled
                                    className="w-full mt-4 bg-gray-400 text-white py-3 rounded-lg font-bold text-lg cursor-not-allowed"
                                >
                                    Processing Order...
                                </button>
                            )}

                            {/* RENDER: Order Confirmation (Post-Submission) */}
                            {confirmationMessage && (
                                <>
                                    <p className="text-center text-green-600 font-bold mt-3 text-lg p-2 bg-green-100 rounded-lg">
                                        {confirmationMessage}
                                    </p>
                                    
                                    {/* Back to Menu Button */}
                                    <button
                                        onClick={handleBackToMenu}
                                        className="w-full mt-4 bg-amber-600 text-white py-3 rounded-lg hover:bg-amber-700 font-bold transition text-lg"
                                    >
                                        ☕ Start a New Order (Back to Menu)
                                    </button>
                                </>
                            )}
                            
                            <button
                                onClick={clearCart}
                                disabled={isProcessingOrder || cartItems.length === 0}
                                className={`w-full mt-2 py-2 rounded-lg font-semibold transition ${
                                    isProcessingOrder || cartItems.length === 0
                                    ? 'bg-red-300 text-white cursor-not-allowed'
                                    : 'bg-red-500 text-white hover:bg-red-600'
                                }`}
                            >
                                Clear Cart
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default PlaceOrderPage;