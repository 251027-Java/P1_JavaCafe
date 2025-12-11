// src/pages/MenuPage.tsx

import React, { useState, useEffect } from 'react';
// Note: You must ensure src/services/menuService.ts exists and defines MenuProduct
import { fetchPublicMenu, type MenuProduct } from '../services/menuService'; 

const MenuPage: React.FC = () => {
  const [menuItems, setMenuItems] = useState<MenuProduct[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const getMenu = async () => {
      try {
        const data = await fetchPublicMenu();
        
        // Sorting items by productId in ascending order
        const sortedData = data.sort((a, b) => a.productId - b.productId);
        
        setMenuItems(sortedData); // Set the sorted array to state
        setError(null); 
      } catch (e: any) {
        setError("Failed to load menu: " + (e.message || 'Check server connection.'));
      } finally {
        setLoading(false);
      }
    };
    
    getMenu();
  }, []); 
  
  // === RENDERING LOGIC: Loading and Error States ===

  if (loading) {
    return (
      <div className="p-8 text-center text-lg">
        <h1 className="text-3xl font-bold">Loading JavaCafe Menu...</h1>
        <p className="mt-2 text-gray-500">Connecting to Spring Boot...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8 text-center text-red-600 bg-red-50 border border-red-200 rounded-lg m-4">
        <h1 className="text-2xl font-bold">Error</h1>
        <p className="mt-2">{error}</p>
        <p className="mt-4 text-sm text-red-500">Please ensure your Spring Boot server is running and CORS is configured.</p>
      </div>
    );
  }

  // === Main Menu Display ===

  return (
    <div className="p-8 container mx-auto">
      <h1 className="text-4xl font-extrabold text-gray-900 mb-8">☕ JavaCafe Menu</h1>
      
      <p className="mb-6 text-lg text-gray-600">Total items available: <span className="font-semibold text-blue-600">{menuItems.length}</span></p>

      {/* Grid Layout for Menu Items */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {menuItems.map((product) => (
          <div 
            key={product.productId} 
            className="bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition duration-300 border-t-4 border-t-blue-500"
          >
            <h2 className="text-xl font-bold text-gray-800">{product.name}</h2>
            <p className="text-3xl font-extrabold text-green-600 mt-2">${product.basePrice.toFixed(2)}</p>
            <p className="text-sm text-gray-500 mt-1">Category: {product.category}</p>
            
            {/* Display the exact availability status string from the database */}
            <p className={`text-sm font-medium mt-3 ${
              product.availability === 'IN_STOCK' ? 'text-green-600' : 'text-red-600'
            }`}>
              Status: {product.availability} 
            </p>
            
          </div>
        ))}
      </div>
      
      <div className="mt-12 text-center">
         <a href="/login" className="text-blue-600 hover:text-blue-800 font-medium text-lg">
           Ready to place an order? Go to Login
         </a>
      </div>
    </div>
  );
};

export default MenuPage;