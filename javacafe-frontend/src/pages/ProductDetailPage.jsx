import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProductImage } from '../assets/images/imageMap';
import { getMenuProducts, getProductDescription } from '../services/MenuService';

const PRODUCT_ID_FIELD_NAME = 'productId';

function ProductDetailPage() {
    const { productId } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [productDetails, setProductDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [addToCartMessage, setAddToCartMessage] = useState('');
    const [itemAdded, setItemAdded] = useState(false);

    useEffect(() => {
        const fetchProduct = async () => {
            setLoading(true);
            setError(null);

            try {
                // Fetch products from API
                const products = await getMenuProducts();
                
                if (!products || products.length === 0) {
                    throw new Error('No products available');
                }

                const foundProduct = products.find(p => p[PRODUCT_ID_FIELD_NAME] === parseInt(productId));
                
                if (!foundProduct) {
                    throw new Error('Product not found');
                }
                
                setProduct(foundProduct);

                // Fetch product description
                try {
                    const details = await getProductDescription(productId);
                    setProductDetails(details);
                } catch (descError) {
                    console.error("Error fetching description:", descError);
                    // Set a default description if API fails
                    setProductDetails({ description: `Delicious ${foundProduct.name} - a perfect choice for any time of day.` });
                }
            } catch (e) {
                console.error("Fetch Product Error:", e);
                setError(e.message || "Failed to load product.");
            } finally {
                setLoading(false);
            }
        };

        if (productId) {
            fetchProduct();
        }
    }, [productId]);

    const handleAddToCart = () => {
        if (!product) return;

        const productIdValue = product[PRODUCT_ID_FIELD_NAME];
        if (!productIdValue) {
            alert("Product ID is missing.");
            return;
        }

        const cartItem = {
            id: productIdValue,
            name: product.name,
            price: product.basePrice ? (typeof product.basePrice === 'number' ? product.basePrice : parseFloat(product.basePrice || 0)) : 0,
            quantity: quantity
        };

        const existingCart = JSON.parse(localStorage.getItem('cart') || '[]');
        const existingItemIndex = existingCart.findIndex(item => item.id === productIdValue);

        if (existingItemIndex >= 0) {
            existingCart[existingItemIndex].quantity += quantity;
        } else {
            existingCart.push(cartItem);
        }

        localStorage.setItem('cart', JSON.stringify(existingCart));
        setAddToCartMessage(`${quantity} ${product.name}(s) added to cart!`);
        setItemAdded(true);
        
        window.dispatchEvent(new Event('storage'));
        window.dispatchEvent(new Event('cartUpdated'));
        
        setTimeout(() => {
            setAddToCartMessage('');
        }, 5000);
    };

    const handleQuantityChange = (delta) => {
        const newQuantity = Math.max(1, quantity + delta);
        setQuantity(newQuantity);
    };

    if (loading) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="text-center">
                    <p className="text-xl text-amber-700">Loading product...</p>
                </div>
            </div>
        );
    }

    if (error || !product) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="text-center">
                    <p className="text-xl text-red-600 mb-4">Error: {error || 'Product not found'}</p>
                    <button
                        onClick={() => navigate('/api/menu')}
                        className="px-4 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors"
                    >
                        Back to Menu
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-3 sm:px-4 py-4 sm:py-8 max-w-6xl">
            <button
                onClick={() => navigate('/api/menu')}
                className="flex items-center text-amber-700 hover:text-amber-900 transition-colors mb-4 sm:mb-6 text-sm sm:text-base"
            >
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
                Back to Menu
            </button>

            <div className="bg-white rounded-lg shadow-xl overflow-hidden">
                <div className="flex flex-col md:flex-row">
                    <div className="w-full md:w-1/2 bg-gray-100 flex items-center justify-center min-h-[300px] sm:min-h-[400px] md:min-h-[500px] relative">
                        {getProductImage(product.name, product.category) ? (
                            <>
                                <img 
                                    src={getProductImage(product.name, product.category)} 
                                    alt={product.name}
                                    className="w-full h-full object-cover"
                                    onError={(e) => {
                                        e.target.style.display = 'none';
                                        if (e.target.nextSibling) {
                                            e.target.nextSibling.style.display = 'flex';
                                        }
                                    }}
                                />
                                <div className="absolute inset-0 flex items-center justify-center text-gray-400" style={{ display: 'none' }}>
                                    <svg className="w-24 h-24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                </div>
                            </>
                        ) : (
                            <div className="w-full h-full flex items-center justify-center text-gray-400">
                                <svg className="w-24 h-24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                            </div>
                        )}
                    </div>
                    <div className="w-full md:w-1/2 p-4 sm:p-6 lg:p-8">
                        <div className="mb-4 sm:mb-6">
                            <span className="text-xs sm:text-sm text-amber-600 uppercase tracking-wide font-medium">
                                {product.category}
                            </span>
                            <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-amber-900 mt-2 mb-3 sm:mb-4">
                                {product.name}
                            </h1>
                            <div className="flex items-center gap-3 mb-4">
                                <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                                    product.availability === 'IN_STOCK' 
                                        ? 'bg-green-100 text-green-700' 
                                        : 'bg-red-100 text-red-700'
                                }`}>
                                    {product.availability === 'IN_STOCK' ? 'In Stock' : 'Out of Stock'}
                                </span>
                            </div>
                        </div>

                        <div className="mb-4 sm:mb-6">
                            <p className="text-2xl sm:text-3xl font-bold text-amber-700 mb-4 sm:mb-6">
                                ${product.basePrice ? (typeof product.basePrice === 'number' ? product.basePrice.toFixed(2) : parseFloat(product.basePrice || 0).toFixed(2)) : 'N/A'}
                            </p>
                            
                            <div className="mb-4 sm:mb-6">
                                <p className="text-sm sm:text-base text-gray-700 whitespace-pre-line leading-relaxed">
                                    {productDetails?.description || `Delicious ${product.name} - a perfect choice for any time of day.`}
                                </p>
                            </div>
                        </div>

                        <div className="border-t pt-4 sm:pt-6">
                            <div className="flex items-center justify-between mb-4 sm:mb-6">
                                <label className="text-base sm:text-lg font-semibold text-gray-700">Quantity:</label>
                                <div className="flex items-center space-x-3 sm:space-x-4">
                                    <button
                                        onClick={() => handleQuantityChange(-1)}
                                        className="w-9 h-9 sm:w-10 sm:h-10 rounded-full bg-amber-100 text-amber-800 hover:bg-amber-200 active:bg-amber-300 transition-colors font-bold text-lg disabled:opacity-50 disabled:cursor-not-allowed"
                                        disabled={quantity <= 1}
                                    >
                                        -
                                    </button>
                                    <span className="text-xl sm:text-2xl font-semibold w-10 sm:w-12 text-center">{quantity}</span>
                                    <button
                                        onClick={() => handleQuantityChange(1)}
                                        className="w-9 h-9 sm:w-10 sm:h-10 rounded-full bg-amber-100 text-amber-800 hover:bg-amber-200 active:bg-amber-300 transition-colors font-bold text-lg"
                                    >
                                        +
                                    </button>
                                </div>
                            </div>

                            <div className="mb-4">
                                <p className="text-sm sm:text-base text-gray-600">
                                    Subtotal: <span className="font-bold text-amber-700 text-lg sm:text-xl">
                                        ${product.basePrice ? ((typeof product.basePrice === 'number' ? product.basePrice : parseFloat(product.basePrice || 0)) * quantity).toFixed(2) : 'N/A'}
                                    </span>
                                </p>
                            </div>

                            {addToCartMessage && (
                                <div className="mb-4 p-3 bg-green-100 border border-green-300 text-green-700 rounded-lg text-center">
                                    {addToCartMessage}
                                </div>
                            )}

                            {itemAdded && (
                                <button
                                    onClick={() => navigate('/api/cart')}
                                    className="w-full py-3 mb-3 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors font-semibold text-lg flex items-center justify-center gap-2"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                                    </svg>
                                    View Cart
                                </button>
                            )}

                            <button
                                onClick={handleAddToCart}
                                disabled={product.availability !== 'IN_STOCK'}
                                className={`w-full py-3 rounded-lg font-semibold text-lg transition-colors flex items-center justify-center gap-2 ${
                                    product.availability === 'IN_STOCK'
                                        ? 'bg-amber-600 text-white hover:bg-amber-700'
                                        : 'bg-gray-400 text-gray-700 cursor-not-allowed'
                                }`}
                            >
                                {product.availability === 'IN_STOCK' ? (
                                    <>
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                        </svg>
                                        Add to Cart
                                    </>
                                ) : (
                                    'Out of Stock'
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProductDetailPage;
