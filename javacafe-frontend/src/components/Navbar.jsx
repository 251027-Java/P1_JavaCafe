import { Link } from 'react-router-dom';

function Navbar() {
    return (
        <nav className="bg-amber-900 text-white shadow-lg">
            <div className="container mx-auto px-4 py-4">
                <h1 className="text-5xl font-black text-center mb-4" style={{ fontFamily: "'Playfair Display', serif", letterSpacing: '0.05em', textShadow: '2px 2px 4px rgba(0,0,0,0.3)' }}>
                    JAVA CAFE
                </h1>
                <div className="flex flex-wrap justify-center gap-4 md:gap-6">
                    <Link 
                        to="/api" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Home
                    </Link>
                    <Link 
                        to="/api/menu" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Menu
                    </Link>
                    <Link 
                        to="/api/cart" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Cart
                    </Link>
                    <Link 
                        to="/api/contact-us" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Contact Us
                    </Link>
                    <Link 
                        to="/api/login" 
                        className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                    >
                        Login
                    </Link>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
