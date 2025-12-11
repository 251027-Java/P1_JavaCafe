import { Link } from 'react-router-dom';

function Announcement({ announcement, imageOnLeft = true }) {
    const { image, title, description } = announcement;

    return (
        <div className="bg-white rounded-lg shadow-md overflow-hidden mb-6 flex flex-col md:flex-row">
            {imageOnLeft ? (
                <>
                    <div className="md:w-1/2 w-full h-64 md:h-auto">
                        <img 
                            src={image} 
                            alt={title}
                            className="w-full h-full object-cover"
                        />
                    </div>
                    <div className="md:w-1/2 w-full p-6 md:p-8 flex flex-col justify-center">
                        <h3 className="text-2xl font-bold text-amber-900 mb-3">{title}</h3>
                        <p className="text-gray-700 leading-relaxed">{description}</p>
                    </div>
                </>
            ) : (
                <>
                    <div className="md:w-1/2 w-full p-6 md:p-8 flex flex-col justify-center order-2 md:order-1">
                        <h3 className="text-2xl font-bold text-amber-900 mb-3">{title}</h3>
                        <p className="text-gray-700 leading-relaxed">{description}</p>
                    </div>
                    <div className="md:w-1/2 w-full h-64 md:h-auto order-1 md:order-2">
                        <img 
                            src={image} 
                            alt={title}
                            className="w-full h-full object-cover"
                        />
                    </div>
                </>
            )}
        </div>
    );
}

export default Announcement;
