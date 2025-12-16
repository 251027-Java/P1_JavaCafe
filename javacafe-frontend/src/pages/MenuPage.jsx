import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProductImage } from '../assets/images/imageMap';
import { getMenuProducts } from '../services/MenuService';

function MenuPage() {
    const navigate = useNavigate();
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProducts = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await getMenuProducts();
                console.log('Products loaded from API:', data);
                setProducts(data);
            } catch (err) {
                console.error('Error fetching products:', err);
                setError(err.message || 'Failed to load products. Please try again later.');
                setProducts([]);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

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

                    {loading ? (
                        <div className="text-center py-12">
                            <p className="text-xl text-amber-700">Loading products...</p>
                        </div>
                    ) : error ? (
                        <div className="text-center py-12">
                            <p className="text-xl text-red-600 mb-4 font-semibold">{error}</p>
                            <div className="bg-amber-50 border border-amber-200 rounded-lg p-6 max-w-2xl mx-auto text-left mb-4">
                                <h3 className="font-bold text-amber-900 mb-2">Troubleshooting Steps:</h3>
                                <ol className="list-decimal list-inside space-y-2 text-amber-800">
                                    <li>Check if the Spring Boot backend is running on port 8080</li>
                                    <li>Verify the database (cafe_db) exists and is accessible</li>
                                    <li>Check browser console (F12) for detailed error messages</li>
                                    <li>Try accessing the API directly: <a href="http://localhost:8080/api/menu" target="_blank" rel="noopener noreferrer" className="text-blue-600 underline">http://localhost:8080/api/menu</a></li>
                                </ol>
                            </div>
                            <button
                                onClick={() => window.location.reload()}
                                className="px-6 py-3 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors font-semibold"
                            >
                                Retry
                            </button>
                        </div>
                    ) : filteredProducts.length === 0 ? (
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