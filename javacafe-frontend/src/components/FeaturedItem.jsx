import { Link } from 'react-router-dom';

function FeaturedItem({ item }) {
    const { id, image, title, description, linkText, linkTo } = item;

    return (
        <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300 flex flex-col">
            <div className="w-full h-48 overflow-hidden">
                <img 
                    src={image} 
                    alt={title}
                    className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
                />
            </div>
            <div className="p-6 flex flex-col flex-grow">
                <h3 className="text-xl font-bold text-amber-900 mb-2">{title}</h3>
                <p className="text-gray-600 mb-4 flex-grow">{description}</p>
                <Link 
                    to={linkTo}
                    className="inline-block bg-amber-900 text-white px-6 py-2 rounded-md hover:bg-amber-800 transition-colors duration-200 text-center font-medium"
                >
                    {linkText}
                </Link>
            </div>
        </div>
    );
}

export default FeaturedItem;
