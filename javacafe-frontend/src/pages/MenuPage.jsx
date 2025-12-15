
import  { useState, useEffect } from 'react';
import { useCart } from './CartContext'; 

// === CONFIGURATION START ===
const backendUrl = 'http://localhost:8080';
// === CONFIGURATION END ===

// Helper function to format category names for display
const formatCategoryName = (category) => {
    if (!category) return 'Other';
    return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
};

// Helper function to group products by category
const groupProductsByCategory = (products) => {
    return products.reduce((acc, product) => {
        const category = product.category || 'OTHER'; 
        if (!acc[category]) {
            acc[category] = [];
        }
        acc[category].push(product);
        return acc;
    }, {});
};

// --- Product Detail Modal Component ---
// This component is now passed the handler directly from the parent
const ProductDetailModal = ({ 
    product, 
    isModalOpen, 
    closeModal, 
    backendUrl, 
    handleAddToCart // Prop from MenuPage (which gets it from Context)
}) => {
    const [productDetails, setProductDetails] = useState(null);
    const [detailsLoading, setDetailsLoading] = useState(false);
    const [quantity, setQuantity] = useState(1); // State for selected quantity

    // Quantity controls
    const incrementQuantity = () => {
        setQuantity(prevQ => prevQ + 1);
    };

    const decrementQuantity = () => {
        setQuantity(prevQ => Math.max(1, prevQ - 1));
    };

    // Add to Cart handler
    const cartHandler = () => {
        if (product && quantity > 0) {
            // Call the addToCart function passed from the parent (which came from Context)
            handleAddToCart(product, quantity); // <--- CONTEXT HANDLER CALLED HERE
            setQuantity(1); 
            closeModal();
        }
    };

    // Effect to fetch product description when the modal opens
    useEffect(() => {
        if (!isModalOpen || !product) return;
        setQuantity(1); 
        
        const fetchDetails = async () => {
            const productId = product.productId; 
            if (!productId) return;

            setDetailsLoading(true);
            setProductDetails(null);
            const endpointPath = `${backendUrl}/api/menu/description/${productId}`;

            try {
                const response = await fetch(endpointPath);
                if (!response.ok) {
                    throw new Error(`Failed to fetch details. Status: ${response.status}`);
                }
                const data = await response.json(); 
                setProductDetails(data); 
            } catch (e) {
                console.error("Fetch Description Error:", e);
                setProductDetails({ description: `Error loading details: ${e.message}` });
            } finally {
                setDetailsLoading(false);
            }
        };

        fetchDetails();
        return () => { setProductDetails(null); }; 
    }, [isModalOpen, product, backendUrl]);


    if (!isModalOpen || !product) return null;

    const isAvailable = product.availability === 'IN_STOCK';

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md">
                <h3 className="text-2xl font-bold text-amber-900 mb-4 border-b pb-2">
                    {product.name}
                </h3>
                
                {detailsLoading ? (
                    <p className="text-center text-gray-600">Loading full description...</p>
                ) : (
                    <div>
                        <p className="text-lg font-semibold mb-2">
                            Price: ${product.basePrice ? product.basePrice.toFixed(2) : 'N/A'}
                        </p>
                        <p className="text-gray-700 whitespace-pre-line mb-4">
                            {productDetails?.description || 'No detailed description available.'}
                        </p>
                    </div>
                )}
                
                {/* --- QUANTITY SELECTOR --- */}
                <div className="flex items-center justify-between border-t pt-4 mt-4">
                    <label className="text-lg font-semibold text-gray-700">Quantity:</label>
                    <div className="flex items-center space-x-2">
                        <button
                            onClick={decrementQuantity}
                            disabled={quantity <= 1 || !isAvailable}
                            className="p-2 border border-gray-300 rounded-lg text-lg font-bold w-10 h-10 flex justify-center items-center bg-gray-100 hover:bg-gray-200 disabled:opacity-50 transition"
                        >
                            -
                        </button>
                        <span className="text-xl font-bold w-8 text-center">{quantity}</span>
                        <button
                            onClick={incrementQuantity}
                            disabled={!isAvailable}
                            className="p-2 border border-gray-300 rounded-lg text-lg font-bold w-10 h-10 flex justify-center items-center bg-gray-100 hover:bg-gray-200 disabled:opacity-50 transition"
                        >
                            +
                        </button>
                    </div>
                </div>

                {/* --- ADD TO CART & CLOSE BUTTONS --- */}
                <div className="mt-6 space-y-3">
                    <button
                        onClick={cartHandler}
                        disabled={!isAvailable || quantity < 1}
                        className={`w-full py-3 rounded-lg text-white font-semibold transition ${
                            isAvailable && quantity >= 1
                                ? 'bg-amber-600 hover:bg-amber-700'
                                : 'bg-gray-400 cursor-not-allowed'
                        }`}
                    >
                        {isAvailable ? `Add ${quantity} to Cart` : 'Out of Stock'}
                    </button>

                    <button
                        onClick={closeModal}
                        className="w-full bg-red-500 text-white py-2 rounded-lg hover:bg-red-600 transition"
                    >
                        Close Details
                    </button>
                </div>
            </div>
        </div>
    );
};
// --- End Product Detail Modal Component ---


export function MenuPage() { // <--- EXPORT CHANGED TO NAMED EXPORT
    // Get the addToCart function from the shared context
    const { addToCart } = useCart(); // <--- GETTING ADD TO CART FROM CONTEXT

    // State for API data and status
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // State for UI interaction (category selection)
    const [selectedCategory, setSelectedCategory] = useState(null); 
    
    // State for Modal
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // REMOVED: const handleAddToCart = (product, quantity) => { ... } 
    // The function is now retrieved from Context!

    const handleOpenModal = (product) => {
        setSelectedProduct(product);
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setSelectedProduct(null);
    };


    // --- Data Fetching: Calls GET /api/menu on mount ---
    useEffect(() => {
        setLoading(true);
        setError(null);
        
        fetch(`${backendUrl}/api/menu`) 
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                setProducts(Array.isArray(data) ? data : []);
            })
            .catch(e => {
                console.error("Fetch Menu Error:", e);
                setError(`Failed to load products. Error: ${e.message}. Ensure backend is running and CORS is configured.`);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []); 

    // --- Prepare Data for Rendering ---
    const groupedProducts = groupProductsByCategory(products);
    const allCategories = Object.keys(groupedProducts).sort();

    // Custom sort order for categories
    const preferredOrder = ['COFFEE', 'CUPCAKES', 'CROISSANTS', 'COOKIES'];
    const sortedCategories = [
        ...preferredOrder.filter(cat => groupedProducts[cat]),
        ...allCategories.filter(cat => !preferredOrder.includes(cat))
    ];
    
    // Determine which categories to display
    const categoriesToDisplay = selectedCategory === null 
        ? sortedCategories 
        : [selectedCategory].filter(cat => groupedProducts[cat]);


    // --- Render Logic ---
    if (loading) return <div className="text-center p-8 text-xl text-amber-700">Loading Menu...</div>;
    if (error) return <div className="text-center p-8 text-xl text-red-600">{error}</div>;

    return (
        <div className="flex bg-amber-50 min-h-screen">
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
                    ) : (
                        categoriesToDisplay.map((category) => (
                            <div key={category} className="mb-12">
                                <h3 className="text-3xl font-bold text-amber-800 mb-6 border-b-2 border-amber-300 pb-2">
                                    {formatCategoryName(category)}
                                </h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                    {groupedProducts[category].map((product) => (
                                        <div 
                                            key={product.productId} 
                                            onClick={() => handleOpenModal(product)} 
                                            className="bg-white rounded-lg shadow-md p-6 hover:shadow-xl transition-shadow cursor-pointer border border-transparent hover:border-amber-500"
                                        >
                                            <h4 className="text-xl font-semibold text-amber-900 mb-2">
                                                {product.name}
                                            </h4>
                                            <p className="text-2xl font-bold text-amber-700 mb-2">
                                                ${product.basePrice ? product.basePrice.toFixed(2) : 'N/A'}
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
                    )}
                </div>
            </main>
            
            {/* Modal for Product Details */}
            <ProductDetailModal 
                product={selectedProduct} 
                isModalOpen={isModalOpen} 
                closeModal={handleCloseModal} 
                backendUrl={backendUrl}
                handleAddToCart={addToCart} // <--- PASSING CONTEXT'S addToCart
            />
        </div>
    );
}

export default MenuPage; // Removed default export