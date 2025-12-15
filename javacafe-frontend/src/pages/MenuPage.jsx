import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProductImage } from '../assets/images/imageMap';

const mockProducts = [
    // COFFEE
    { productId: 1, category: 'COFFEE', name: 'Java House Espresso', basePrice: 3.00, availability: 'IN_STOCK' },
    { productId: 2, category: 'COFFEE', name: 'Coffee Misto', basePrice: 4.00, availability: 'IN_STOCK' },
    { productId: 3, category: 'COFFEE', name: 'Cappuccino', basePrice: 4.50, availability: 'IN_STOCK' },
    { productId: 4, category: 'COFFEE', name: 'Caramel Macchiato', basePrice: 5.25, availability: 'IN_STOCK' },
    { productId: 5, category: 'COFFEE', name: 'Mocha Frappuccino', basePrice: 6.50, availability: 'IN_STOCK' },
    
    // CUPCAKES
    { productId: 11, category: 'CUPCAKES', name: 'Vanilla Bean Bliss', basePrice: 4.00, availability: 'IN_STOCK' },
    { productId: 12, category: 'CUPCAKES', name: 'Red Velvet Dream', basePrice: 4.25, availability: 'IN_STOCK' },
    { productId: 13, category: 'CUPCAKES', name: 'Triple Chocolate Overload', basePrice: 4.50, availability: 'IN_STOCK' },
    { productId: 14, category: 'CUPCAKES', name: 'Lemon Zest Delight', basePrice: 4.00, availability: 'IN_STOCK' },
    { productId: 15, category: 'CUPCAKES', name: 'Strawberry Shortcake', basePrice: 4.25, availability: 'IN_STOCK' },
    
    // COOKIES
    { productId: 21, category: 'COOKIES', name: 'Signature Chocolate Chip', basePrice: 2.50, availability: 'IN_STOCK' },
    { productId: 22, category: 'COOKIES', name: 'Oatmeal Cranberry White Chocolate', basePrice: 2.75, availability: 'IN_STOCK' },
    { productId: 23, category: 'COOKIES', name: 'Double Fudge Brownie Cookie', basePrice: 3.00, availability: 'IN_STOCK' },
    { productId: 24, category: 'COOKIES', name: 'Snickerdoodle', basePrice: 2.50, availability: 'IN_STOCK' },
    
    // CROISSANTS
    { productId: 31, category: 'CROISSANTS', name: 'Classic Butter Croissant', basePrice: 3.75, availability: 'IN_STOCK' },
    { productId: 32, category: 'CROISSANTS', name: 'Cinnamon Swirl Croissant', basePrice: 4.50, availability: 'IN_STOCK' },
    { productId: 33, category: 'CROISSANTS', name: 'Chocolate Almond Croissant', basePrice: 4.75, availability: 'IN_STOCK' },
    { productId: 34, category: 'CROISSANTS', name: 'Plain Croissant', basePrice: 3.50, availability: 'IN_STOCK' },
    { productId: 35, category: 'CROISSANTS', name: 'Ham and Cheese Croissant', basePrice: 5.00, availability: 'IN_STOCK' },
    
    // PASTRIES
    { productId: 41, category: 'PASTRIES', name: 'Cheese Danish', basePrice: 4.50, availability: 'IN_STOCK' },
    { productId: 42, category: 'PASTRIES', name: 'Blueberry Muffin', basePrice: 3.50, availability: 'IN_STOCK' },
    { productId: 43, category: 'PASTRIES', name: 'Apple Turnover', basePrice: 4.25, availability: 'IN_STOCK' },
    { productId: 44, category: 'PASTRIES', name: 'Almond Croissant', basePrice: 4.75, availability: 'IN_STOCK' },
    { productId: 45, category: 'PASTRIES', name: 'Chocolate Eclair', basePrice: 4.50, availability: 'IN_STOCK' },
    
    // SANDWICHES
    { productId: 51, category: 'SANDWICHES', name: 'BLT Classic', basePrice: 7.50, availability: 'IN_STOCK' },
    { productId: 52, category: 'SANDWICHES', name: 'Caprese Sandwich', basePrice: 8.00, availability: 'IN_STOCK' },
    { productId: 53, category: 'SANDWICHES', name: 'Grilled Chicken Panini', basePrice: 8.50, availability: 'IN_STOCK' },
    { productId: 54, category: 'SANDWICHES', name: 'Turkey Avocado Club', basePrice: 9.00, availability: 'IN_STOCK' },
    { productId: 55, category: 'SANDWICHES', name: 'Veggie Delight', basePrice: 7.00, availability: 'IN_STOCK' },
    
    // SALADS
    { productId: 61, category: 'SALADS', name: 'Caesar Salad', basePrice: 8.50, availability: 'IN_STOCK' },
    { productId: 62, category: 'SALADS', name: 'Cobb Salad', basePrice: 9.50, availability: 'IN_STOCK' },
    { productId: 63, category: 'SALADS', name: 'Garden Fresh Salad', basePrice: 7.50, availability: 'IN_STOCK' },
    { productId: 64, category: 'SALADS', name: 'Grilled Chicken Salad', basePrice: 9.00, availability: 'IN_STOCK' },
    { productId: 65, category: 'SALADS', name: 'Quinoa Power Bowl', basePrice: 9.75, availability: 'IN_STOCK' },
    
    // SMOOTHIES
    { productId: 71, category: 'SMOOTHIES', name: 'Berry Blast Smoothie', basePrice: 5.50, availability: 'IN_STOCK' },
    { productId: 72, category: 'SMOOTHIES', name: 'Chocolate Banana Smoothie', basePrice: 5.75, availability: 'IN_STOCK' },
    { productId: 73, category: 'SMOOTHIES', name: 'Green Power Smoothie', basePrice: 6.00, availability: 'IN_STOCK' },
    { productId: 74, category: 'SMOOTHIES', name: 'Peach Mango Smoothie', basePrice: 5.75, availability: 'IN_STOCK' },
    { productId: 75, category: 'SMOOTHIES', name: 'Tropical Paradise Smoothie', basePrice: 6.25, availability: 'IN_STOCK' },
];

function MenuPage() {
    const navigate = useNavigate();
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');
    const products = mockProducts;

    const formatCategoryName = (category) => {
        if (!category) return 'Other';
        return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
    };

    // Filter products by search query
    const filteredProducts = products.filter(product => {
        if (!searchQuery.trim()) return true;
        const query = searchQuery.toLowerCase();
        return product.name.toLowerCase().includes(query) || 
               product.category.toLowerCase().includes(query);
    });

    // Group products by category
    const productsByCategory = filteredProducts.reduce((acc, product) => {
        const category = product.category || 'OTHER';
        if (!acc[category]) {
            acc[category] = [];
        }
        acc[category].push(product);
        return acc;
    }, {});

    const allCategories = Object.keys(productsByCategory).sort();
    const preferredOrder = ['COFFEE', 'CUPCAKES', 'COOKIES', 'CROISSANTS', 'PASTRIES', 'SANDWICHES', 'SALADS', 'SMOOTHIES'];
    const sortedCategories = [
        ...preferredOrder.filter(cat => productsByCategory[cat]),
        ...allCategories.filter(cat => !preferredOrder.includes(cat))
    ];

    return (
        <div className="flex bg-amber-50">
            {/* Left Sidebar - Categories */}
            <aside className="w-64 bg-white shadow-lg p-6 min-h-screen sticky top-0 self-start">
                <h2 className="text-2xl font-bold text-amber-900 mb-6 pb-3 border-b-2 border-amber-200">
                    Categories
                </h2>
                <nav className="space-y-2">
                    <button
                        onClick={() => setSelectedCategory(null)}
                        className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                            selectedCategory === null
                                ? 'bg-amber-600 text-white font-semibold'
                                : 'text-amber-900 hover:bg-amber-100'
                        }`}
                    >
                        All Categories
                    </button>
                    {allCategories.map((category) => (
                        <button
                            key={category}
                            onClick={() => setSelectedCategory(category)}
                            className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                                selectedCategory === category
                                    ? 'bg-amber-600 text-white font-semibold'
                                    : 'text-amber-900 hover:bg-amber-100'
                            }`}
                        >
                            {formatCategoryName(category)}
                        </button>
                    ))}
                </nav>
            </aside>

            {/* Main Content Area */}
            <main className="flex-1 p-8">
                <div className="max-w-7xl mx-auto">
                    <div className="flex justify-between items-center mb-8">
                        <div>
                            <h1 className="text-4xl font-bold text-amber-900 mb-2">Menu</h1>
                            <h2 className="text-2xl font-bold text-amber-800">
                                {selectedCategory ? formatCategoryName(selectedCategory) : 'All Categories'}
                            </h2>
                        </div>
                        {/* Search Bar */}
                        <div className="relative w-80">
                            <input
                                type="text"
                                placeholder="Search products..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                className="w-full px-4 py-3 pl-10 pr-10 border-2 border-amber-200 rounded-lg focus:outline-none focus:border-amber-500 text-amber-900 placeholder-amber-400"
                            />
                            <svg 
                                className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-amber-400" 
                                fill="none" 
                                stroke="currentColor" 
                                viewBox="0 0 24 24"
                            >
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                            {searchQuery && (
                                <button
                                    onClick={() => setSearchQuery('')}
                                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-amber-400 hover:text-amber-600 transition-colors"
                                    aria-label="Clear search"
                                >
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            )}
                        </div>
                    </div>

                    {filteredProducts.length === 0 ? (
                        <div className="text-center py-12">
                            <p className="text-xl text-amber-700">
                                {searchQuery ? `No products found matching "${searchQuery}"` : 'No products available at this time.'}
                            </p>
                        </div>
                    ) : selectedCategory === null ? (
                        sortedCategories.map((category) => (
                            <div key={category} className="mb-12">
                                <h3 className="text-3xl font-bold text-amber-800 mb-6 border-b-2 border-amber-300 pb-2">
                                    {formatCategoryName(category)}
                                </h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                    {productsByCategory[category].map((product) => {
                                        const productImage = getProductImage(product.name, product.category);
                                        return (
                                            <div 
                                                key={product.productId} 
                                                className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow"
                                            >
                                                {/* Product Image Box */}
                                                <div 
                                                    onClick={() => navigate(`/api/menu/product/${product.productId}`)}
                                                    className="w-full h-48 bg-amber-100 rounded-lg mb-4 flex items-center justify-center overflow-hidden cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-xl hover:shadow-amber-200/50"
                                                >
                                                    {productImage ? (
                                                        <img 
                                                            src={productImage} 
                                                            alt={product.name}
                                                            className="w-full h-full object-cover transition-transform duration-300 hover:scale-110"
                                                        />
                                                    ) : (
                                                        <div className="text-amber-400 text-4xl transition-transform duration-300 hover:scale-110">☕</div>
                                                    )}
                                                </div>
                                                <h4 className="text-xl font-semibold text-amber-900 mb-2">
                                                    {product.name}
                                                </h4>
                                                <p className="text-2xl font-bold text-amber-700 mb-2">
                                                    ${typeof product.basePrice === 'number' 
                                                        ? product.basePrice.toFixed(2) 
                                                        : parseFloat(product.basePrice || 0).toFixed(2)}
                                                </p>
                                                {product.availability && (
                                                    <p className={`text-sm ${
                                                        product.availability === 'IN_STOCK' 
                                                            ? 'text-green-600' 
                                                            : 'text-red-600'
                                                    }`}>
                                                        {product.availability === 'IN_STOCK' ? 'In Stock' : 'Out of Stock'}
                                                    </p>
                                                )}
                                            </div>
                                        );
                                    })}
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="mb-12">
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                {productsByCategory[selectedCategory]?.map((product) => {
                                    const productImage = getProductImage(product.name, product.category);
                                    return (
                                        <div 
                                            key={product.productId} 
                                            className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow"
                                        >
                                            {/* Product Image Box */}
                                            <div 
                                                onClick={() => navigate(`/api/menu/product/${product.productId}`)}
                                                className="w-full h-48 bg-amber-100 rounded-lg mb-4 flex items-center justify-center overflow-hidden cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-xl hover:shadow-amber-200/50"
                                            >
                                                {productImage ? (
                                                    <img 
                                                        src={productImage} 
                                                        alt={product.name}
                                                        className="w-full h-full object-cover transition-transform duration-300 hover:scale-110"
                                                    />
                                                ) : (
                                                    <div className="text-amber-400 text-4xl transition-transform duration-300 hover:scale-110">☕</div>
                                                )}
                                            </div>
                                            <h4 className="text-xl font-semibold text-amber-900 mb-2">
                                                {product.name}
                                            </h4>
                                            <p className="text-2xl font-bold text-amber-700 mb-2">
                                                ${typeof product.basePrice === 'number' 
                                                    ? product.basePrice.toFixed(2) 
                                                    : parseFloat(product.basePrice || 0).toFixed(2)}
                                            </p>
                                            {product.availability && (
                                                <p className={`text-sm ${
                                                    product.availability === 'IN_STOCK' 
                                                        ? 'text-green-600' 
                                                        : 'text-red-600'
                                                }`}>
                                                    {product.availability === 'IN_STOCK' ? 'In Stock' : 'Out of Stock'}
                                                </p>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}

export default MenuPage;