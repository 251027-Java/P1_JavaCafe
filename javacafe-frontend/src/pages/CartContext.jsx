// CartContext.jsx

import React, { createContext, useState, useContext, useCallback } from 'react';

// === CONFIGURATION ===
const PRODUCT_ID_FIELD_NAME = 'productId';

// Create the Context
export const CartContext = createContext();

// Create the Provider Component
export const CartProvider = ({ children }) => {
    // Cart State: Stores items as { id: uniqueIdentifier, name, price, quantity }
    const [cartItems, setCartItems] = useState([]);
    
    // Function to add or update items in the cart
    const addToCart = useCallback((productToAdd, quantityToAdd = 1) => {
        if (quantityToAdd <= 0) return;

        const uniqueIdValue = productToAdd[PRODUCT_ID_FIELD_NAME];
        if (!uniqueIdValue) return;

        const itemIdString = String(uniqueIdValue);

        setCartItems(prevItems => {
            const existingItem = prevItems.find(item => String(item.id) === itemIdString);

            if (existingItem) {
                // Item exists, update quantity
                return prevItems.map(item =>
                    String(item.id) === itemIdString
                        ? { ...item, quantity: item.quantity + quantityToAdd }
                        : item
                );
            } else {
                // New item, add to cart
                return [
                    ...prevItems,
                    {
                        id: uniqueIdValue,
                        name: productToAdd.name,
                        price: productToAdd.basePrice,
                        quantity: quantityToAdd
                    }
                ];
            }
        });
        
        // Optional: Provide feedback
        //alert(`Added ${quantityToAdd} x ${productToAdd.name} to the cart!`);

        // IMPORTANT: In a real app, you would send this update to your Spring Boot backend here!
        // You would likely call a separate async function to handle the API call.

    }, []);
    
    // Function to update the quantity of an item already in the cart
    const updateQuantity = useCallback((id, newQuantity) => {
        if (newQuantity < 0) return;

        setCartItems(prevItems => {
            if (newQuantity === 0) {
                return prevItems.filter(item => item.id !== id);
            } else {
                return prevItems.map(item =>
                    item.id === id ? { ...item, quantity: newQuantity } : item
                );
            }
        });
    }, []);
    
    // Function to clear the cart
    const clearCart = useCallback(() => {
        setCartItems([]);
    }, []);

    // Function to remove a single item
    const removeItem = useCallback((idToRemove) => {
        setCartItems(prevItems => prevItems.filter(item => item.id !== idToRemove));
    }, []);

    const cartTotal = cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);

    return (
        <CartContext.Provider 
            value={{ 
                cartItems, 
                addToCart, 
                updateQuantity,
                clearCart,
                removeItem,
                cartTotal
            }}
        >
            {children}
        </CartContext.Provider>
    );
};

// Custom hook for easy access to cart context
export const useCart = () => useContext(CartContext);