import { useState, useEffect } from 'react';

// === CONFIGURATION START ===
const PRODUCT_ID_FIELD_NAME = 'productId'; 
const backendUrl = 'http://localhost:8080';

// Note: MENU_CATEGORIES is no longer strictly necessary but kept for context.
// The filtering logic below ensures ALL products are fetched.
// === CONFIGURATION END ===

const groupProductsByCategory = (products) => {
    return products.reduce((acc, product) => {
        const category = product.category || 'Other'; 
        if (!acc[category]) {
            acc[category] = [];
        }
        acc[category].push(product);
        return acc;
    }, {});
};


function MenuPage() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // 💥 REMOVED: selectedCategory state and setSelectedCategory 💥
    // const [selectedCategory, setSelectedCategory] = useState('');

    const [selectedProduct, setSelectedProduct] = useState(null);
    const [productDetails, setProductDetails] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [detailsLoading, setDetailsLoading] = useState(false);
    

    // --- Data Fetching: Now fetches ALL products only once ---
    useEffect(() => {
        setLoading(true);
        setError(null);
        
        // Always call the base /api/menu endpoint without any query parameters
        const url = `${backendUrl}/api/menu`; 

        fetch(url)
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
                setError("Failed to load products. Check network and backend configuration.");
            })
            .finally(() => {
                setLoading(false);
            });
    }, []); // 💥 Dependency array is empty ([]), so it runs only on mount. 💥

    // --- Function to Fetch Details ---
    const handleViewDetails = async (product) => {
        const productId = product[PRODUCT_ID_FIELD_NAME];
        if (!productId) {
            alert("Product ID is missing.");
            return;
        }

        setSelectedProduct(product);
        setProductDetails(null);
        setIsModalOpen(true);
        setDetailsLoading(true);

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
    
    const groupedProducts = groupProductsByCategory(products);
    const categoryKeys = Object.keys(groupedProducts);

    
    if (loading) return <div className="text-center p-8 text-xl text-amber-700">Loading Menu...</div>;
    if (error) return <div className="text-center p-8 text-xl text-red-600">{error}</div>;

    
    return (
        <div className="container mx-auto px-4 py-8 max-w-4xl">
            <h1 className="text-3xl font-serif font-bold text-amber-900 mb-6 border-b pb-2">Our Menu</h1>
            
            {/* 💥 REMOVED: The entire Category Dropdown (Filter) section 💥 */}

            {/* NEW PRODUCT LIST */}
            {categoryKeys.length === 0 ? (
                <p className="text-gray-500 italic">No products found for this selection.</p>
            ) : (
                <div className="space-y-8">
                    {categoryKeys.map(category => (
                        <div key={category} className="menu-category">
                            {/* Category Header */}
                            <h2 className="text-2xl font-serif font-semibold text-amber-700 border-b-2 border-amber-300 pb-1 mb-4 capitalize">
                                {category.toLowerCase()}
                            </h2>

                            {/* Items within the Category */}
                            <div className="space-y-3">
                                {groupedProducts[category].map(product => (
                                    <div 
                                        key={product[PRODUCT_ID_FIELD_NAME]} 
                                        className="text-lg menu-item-row"
                                    >
                                        
                                        {/* Row 1: Name, Dotted Line, Price */}
                                        <div className="flex justify-between items-end">
                                            {/* Item Name */}
                                            <p className="text-gray-800 font-medium whitespace-nowrap">
                                                {product.name}
                                            </p>

                                            {/* Dotted Line Effect */}
                                            <div className="flex-grow border-b border-dotted border-gray-400 mx-2 mb-1"></div>

                                            {/* Price */}
                                            <div className="text-right">
                                                <p className="text-gray-900 font-bold whitespace-nowrap">
                                                    {product.basePrice ? `$${product.basePrice.toFixed(2)}` : 'N/A'}
                                                </p>
                                            </div>
                                        </div>
                                        
                                        {/* Row 2: Short Description & Details Link */}
                                        <div className="flex justify-between items-center text-sm pt-0.5">
                                            {/* Short Description */}
                                            <p className="text-gray-500 italic line-clamp-1">
                                                {product.shortDescription}
                                            </p>

                                            {/* 💥 STYLED DETAILS BUTTON 💥 */}
                                            <button
                                                onClick={() => handleViewDetails(product)}
                                                className="
                                                    bg-amber-600 text-white text-xs font-semibold px-2.5 py-1 rounded-full 
                                                    shadow-md hover:bg-amber-700 transition-colors duration-200 ml-4
                                                    focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-opacity-50
                                                "
                                            >
                                                Details
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Product Detail Modal */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md">
                        <h3 className="text-2xl font-bold text-amber-900 mb-4 border-b pb-2">
                            {selectedProduct?.name} Details
                        </h3>
                        
                        {detailsLoading ? (
                            <p className="text-center text-gray-600">Loading full description...</p>
                        ) : (
                            <div>
                                <p className="text-lg font-semibold mb-2">
                                    Price: ${selectedProduct?.basePrice?.toFixed(2) || 'N/A'}
                                </p>
                                <p className="text-gray-700 whitespace-pre-line">
                                    {productDetails?.description || 'No detailed description available.'}
                                </p>
                            </div>
                        )}

                        <button
                            onClick={() => setIsModalOpen(false)}
                            className="mt-6 w-full bg-red-500 text-white py-2 rounded-lg hover:bg-red-600 transition"
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default MenuPage;