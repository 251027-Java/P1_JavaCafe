import { useState, useEffect } from 'react';
import { getMenuProducts } from '../services/MenuService';

function MenuPage() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const data = await getMenuProducts();
                setProducts(data);
                setLoading(false);
            } catch (err) {
                setError(err.message);
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    // Group products by category
    const productsByCategory = products.reduce((acc, product) => {
        const category = product.category || 'OTHER';
        if (!acc[category]) {
            acc[category] = [];
        }
        acc[category].push(product);
        return acc;
    }, {});

    const categoryOrder = ['COFFEE', 'CUPCAKES', 'CROISSANTS', 'COOKIES'];
    const sortedCategories = categoryOrder.filter(cat => productsByCategory[cat]);

    if (loading) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="text-center">
                    <p className="text-lg text-amber-900">Loading menu...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="text-center">
                    <p className="text-lg text-red-600">Error: {error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-4xl font-bold text-amber-900 text-center mb-8">Menu</h1>
            
            {sortedCategories.map((category) => (
                <div key={category} className="mb-12">
                    <h2 className="text-3xl font-bold text-amber-800 mb-6 border-b-2 border-amber-300 pb-2">
                        {category}
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {productsByCategory[category].map((product) => (
                            <div 
                                key={product.productId} 
                                className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow"
                            >
                                <h3 className="text-xl font-semibold text-amber-900 mb-2">
                                    {product.name}
                                </h3>
                                <p className="text-2xl font-bold text-amber-700 mb-2">
                                    ${product.basePrice}
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
            ))}
        </div>
    );
}

export default MenuPage;