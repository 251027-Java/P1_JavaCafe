import { useState } from 'react';

// Mock data for the menu
const mockProducts = [
    // Coffee
    { productId: 1, category: 'COFFEE', name: 'Java House Espresso', basePrice: 3.00, availability: 'IN_STOCK' },
    { productId: 2, category: 'COFFEE', name: 'Coffee Misto', basePrice: 4.00, availability: 'IN_STOCK' },
    { productId: 3, category: 'COFFEE', name: 'Mocha Frappuccino', basePrice: 6.50, availability: 'IN_STOCK' },
    
    // Cupcakes
    { productId: 4, category: 'CUPCAKES', name: 'Vanilla Bean Bliss', basePrice: 4.00, availability: 'IN_STOCK' },
    { productId: 5, category: 'CUPCAKES', name: 'Red Velvet Dream', basePrice: 4.25, availability: 'IN_STOCK' },
    { productId: 6, category: 'CUPCAKES', name: 'Triple Chocolate Overload', basePrice: 4.25, availability: 'IN_STOCK' },
    
    // Croissants
    { productId: 7, category: 'CROISSANTS', name: 'Classic Butter Croissant', basePrice: 3.75, availability: 'IN_STOCK' },
    { productId: 8, category: 'CROISSANTS', name: 'Cinnamon Swirl Croissant', basePrice: 4.50, availability: 'IN_STOCK' },
    
    // Cookies
    { productId: 9, category: 'COOKIES', name: 'Signature Chocolate Chip', basePrice: 2.50, availability: 'IN_STOCK' },
    { productId: 10, category: 'COOKIES', name: 'Oatmeal Cranberry White Chocolate', basePrice: 2.75, availability: 'IN_STOCK' },
];

function MenuPage() {
    const [selectedCategory, setSelectedCategory] = useState(null);
    const products = mockProducts;

    const formatCategoryName = (category) => {
        if (!category) return 'Other';
        return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
    };

    // Group products by category
    const productsByCategory = products.reduce((acc, product) => {
        const category = product.category || 'OTHER';
        if (!acc[category]) {
            acc[category] = [];
        }
        acc[category].push(product);
        return acc;
    }, {});

    const allCategories = Object.keys(productsByCategory).sort();
    const preferredOrder = ['COFFEE', 'CUPCAKES', 'CROISSANTS', 'COOKIES'];
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
                    <h1 className="text-4xl font-bold text-amber-900 mb-2">Menu</h1>
                    <h2 className="text-2xl font-bold text-amber-800 mb-8">
                        {selectedCategory ? formatCategoryName(selectedCategory) : 'All Categories'}
                    </h2>

                    {products.length === 0 ? (
                        <div className="text-center py-12">
                            <p className="text-xl text-amber-700">No products available at this time.</p>
                        </div>
                    ) : selectedCategory === null ? (
                        sortedCategories.map((category) => (
                            <div key={category} className="mb-12">
                                <h3 className="text-3xl font-bold text-amber-800 mb-6 border-b-2 border-amber-300 pb-2">
                                    {formatCategoryName(category)}
                                </h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                    {productsByCategory[category].map((product) => (
                                        <div 
                                            key={product.productId} 
                                            className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow cursor-pointer"
                                        >
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
                                    ))}
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="mb-12">
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                {productsByCategory[selectedCategory]?.map((product) => (
                                    <div 
                                        key={product.productId} 
                                        className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow cursor-pointer"
                                    >
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
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}

export default MenuPage;