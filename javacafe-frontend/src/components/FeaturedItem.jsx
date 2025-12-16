import { Link, useNavigate } from 'react-router-dom';

function FeaturedItem({ item }) {
    const navigate = useNavigate();
    const { id, productId, name, image, description, linkTo, category, price } = item;
    const productName = name || 'Product';
    const productImage = image || null;
    
    const formatCategoryName = (cat) => {
        if (!cat) return 'Product';
        return cat.charAt(0).toUpperCase() + cat.slice(1).toLowerCase();
    };
    
    const linkText = category ? `View ${formatCategoryName(category)}` : 'View Product';
    const productLink = linkTo || `/api/menu/product/${productId || id}`;

    return (
        <div 
            className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300 flex flex-col cursor-pointer"
            onClick={() => navigate(productLink)}
        >
            <div className="w-full h-48 overflow-hidden bg-amber-100">
                {productImage ? (
                    <img 
                        src={productImage} 
                        alt={productName}
                        className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-amber-400 text-6xl">
                        ☕
                    </div>
                )}
            </div>
            <div className="p-6 flex flex-col flex-grow">
                <h3 className="text-xl font-bold text-amber-900 mb-2">{productName}</h3>
                {price && (
                    <p className="text-amber-700 font-semibold mb-2">${typeof price === 'number' ? price.toFixed(2) : parseFloat(price || 0).toFixed(2)}</p>
                )}
                <p className="text-gray-600 mb-4 flex-grow line-clamp-3">{description || `Delicious ${productName.toLowerCase()} from our menu.`}</p>
                <Link 
                    to={productLink}
                    onClick={(e) => e.stopPropagation()}
                    className="inline-block bg-amber-900 text-white px-6 py-2 rounded-md hover:bg-amber-800 transition-colors duration-200 text-center font-medium"
                >
                    {linkText}
                </Link>
            </div>
        </div>
    );
}

export default FeaturedItem;
