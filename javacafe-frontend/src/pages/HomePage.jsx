import { useState, useEffect } from 'react';
import FeaturedItem from '../components/FeaturedItem';
import Announcement from '../components/Announcement';
import { Link } from 'react-router-dom';
import { getMenuProducts, getProductDescription } from '../services/MenuService';
import { getProductImage } from '../assets/images/imageMap';
import Snowfall from '../components/Snowfall';

function HomePage() {
    const [featuredItems, setFeaturedItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const announcements = [
        {
            id: 1,
            image: 'https://images.unsplash.com/photo-1603912699214-92627f304eb6?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Z3JhbmQlMjBvcGVuaW5nfGVufDB8fDB8fHww',
            title: 'GRAND OPENING!!!',
            description: 'Java Café is now open and pouring happiness one cup at a time. Come enjoy fresh brews, cozy vibes, and delicious treats. Come celebrate with us and taste the difference!'
        },
        {
            id: 2,
            image: 'https://images.unsplash.com/photo-1600093463592-8e36ae95ef56?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y29mZmVlJTIwc2hvcHxlbnwwfHwwfHx8MA%3D%3D',
            title: 'Multiple Locations',
            description: 'Visit one of our many DFW locations! With shops in Plano, Euless, Allen, Frisco, and Flower Mound we aim to provide you with delicious treats no matter where you go!! '
        },
        {
            id: 3,
            image: 'https://plus.unsplash.com/premium_vector-1720532759341-feb0cfea424d?w=352&dpr=2&h=367&auto=format&fit=crop&q=60&ixlib=rb-4.1.0',
            title: 'Java Café\'s 25 Days of Cheer!',
            description: 'All season long, we\'re picking 3 guests each day to win a free drink or sweet treat. Stop in, enjoy the festive atmosphere, and you might leave with a little extra Christmas joy!'
        }
    ];

    useEffect(() => {
        const fetchFeaturedProducts = async () => {
            setLoading(true);
            setError(null);
            try {
                const products = await getMenuProducts();
                
                const availableProducts = products.filter(p => p.availability === 'IN_STOCK');
                
                if (availableProducts.length === 0) {
                    setError('No products available at the moment.');
                    setFeaturedItems([]);
                    setLoading(false);
                    return;
                }
                
                const categories = ['COFFEE', 'CROISSANTS', 'COOKIES', 'CUPCAKES', 'PASTRIES', 'SMOOTHIES'];
                const selectedProducts = [];
                
                categories.forEach(category => {
                    const categoryProducts = availableProducts.filter(p => p.category === category);
                    if (categoryProducts.length > 0) {
                        const productWithImage = categoryProducts.find(p => 
                            getProductImage(p.name, p.category) !== null
                        );
                        selectedProducts.push(productWithImage || categoryProducts[0]);
                    }
                });
                
                if (selectedProducts.length < 6) {
                    const remainingProducts = availableProducts.filter(p => 
                        !selectedProducts.some(sp => sp.productId === p.productId)
                    );
                    const withImages = remainingProducts.filter(p => 
                        getProductImage(p.name, p.category) !== null
                    );
                    const withoutImages = remainingProducts.filter(p => 
                        getProductImage(p.name, p.category) === null
                    );
                    
                    selectedProducts.push(...withImages.slice(0, 6 - selectedProducts.length));
                    if (selectedProducts.length < 6) {
                        selectedProducts.push(...withoutImages.slice(0, 6 - selectedProducts.length));
                    }
                }
                
                const featuredPromises = selectedProducts.slice(0, 6).map(async (product) => {
                    let description = `Delicious ${product.name.toLowerCase()} from our ${product.category.toLowerCase()} collection.`;
                    
                    try {
                        const descData = await getProductDescription(product.productId);
                        if (descData && descData.description) {
                            description = descData.description;
                        }
                    } catch (err) {
                        console.warn(`Could not fetch description for product ${product.productId}:`, err);
                    }
                    
                    const productImage = getProductImage(product.name, product.category);
                    
                    return {
                        id: product.productId,
                        productId: product.productId,
                        name: product.name,
                        category: product.category,
                        description: description,
                        price: product.basePrice,
                        image: productImage,
                        linkTo: `/api/menu/product/${product.productId}`
                    };
                });
                
                const featured = await Promise.all(featuredPromises);
                setFeaturedItems(featured);
            } catch (err) {
                console.error('Error fetching featured products:', err);
                setError('Failed to load featured items. Please try again later.');
                setFeaturedItems([]);
            } finally {
                setLoading(false);
            }
        };

        fetchFeaturedProducts();
    }, []);

    return (
        <div className="relative min-h-screen">
            {/* Snowfall Effect */}
            <Snowfall />
            <div className="bg-yellow-700 text-white py-3 shadow-md mb-6 relative z-10">
                <div className="container mx-auto px-4">
                    <p className="text-center text-lg font-medium">
                        Serving Fresh Coffee, Not Runtime Errors
                    </p>
                </div>
            </div>

            <section className="mb-12 relative z-10">
                    <div className="max-w-5xl mx-auto">
                        {announcements.map((announcement, index) => (
                            <Announcement 
                                key={announcement.id} 
                                announcement={announcement}
                                imageOnLeft={index % 2 === 0}
                            />
                        ))}
                    </div>
                </section>

            <div className="container mx-auto px-4 py-8 relative z-10">
                <section className="mb-12">
                    <h2 className="text-3xl font-bold text-amber-900 text-center mb-8">
                        Featured Items
                    </h2>
                    {loading && (
                        <div className="text-center py-8">
                            <p className="text-amber-700">Loading featured items...</p>
                        </div>
                    )}
                    {error && (
                        <div className="text-center py-8">
                            <p className="text-red-600">{error}</p>
                        </div>
                    )}
                    {!loading && !error && featuredItems.length === 0 && (
                        <div className="text-center py-8">
                            <p className="text-gray-600">No featured items available at the moment.</p>
                        </div>
                    )}
                    {!loading && !error && featuredItems.length > 0 && (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {featuredItems.map((item) => (
                                <FeaturedItem key={item.id} item={item} />
                            ))}
                        </div>
                    )}
                </section>
                
                <div className="flex justify-center mt-12 mb-8">
                    <Link 
                        to="/api/menu"
                        className="w-full max-w-md bg-amber-900 text-white px-8 py-4 rounded-lg hover:bg-amber-800 transition-colors duration-200 text-center font-semibold text-lg shadow-md hover:shadow-lg"
                    >
                        View Full Menu
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default HomePage;
