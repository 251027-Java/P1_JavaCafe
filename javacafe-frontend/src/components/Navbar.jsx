import { Link, useNavigate } from 'react-router-dom'; // 1. Import useNavigate
import { useAuth } from '../pages/useAuth'; // 1. Import useAuth

function Navbar() {
    // 2. Access state and functions from the context
    const { isAuthenticated, user, logout } = useAuth();
    const navigate = useNavigate();

    // 3. Define the logout handler
    const handleLogout = () => {
        logout(); // Calls the function that clears sessionStorage/state
        
        // Redirect the user to a public page after clearing the session
        navigate('/api/menu'); 
    };

    return (
        <nav className="bg-amber-900 text-white shadow-lg">
            <div className="container mx-auto px-4 py-4">
                <h1 className="text-3xl font-bold text-center mb-4">JAVA CAFE</h1>
                <div className="flex flex-wrap justify-center gap-4 md:gap-6">
                    {/* Public Links (Always visible) */}
                    <Link to="/api" className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded">
                        Home
                    </Link>
                    <Link to="/api/menu" className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded">
                        Menu
                    </Link>
                    <Link to="/api/cart" className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded">
                        Cart
                    </Link>
                    <Link to="/api/contact" className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded">
                        Contact Us
                    </Link>

                    {/* Authentication Links (Conditional Rendering) */}
                    {isAuthenticated ? (
                        <>
                            {/* Greeting */}
                            <span className="px-3 py-2">
                                Hello, {user.firstName || 'User'}!
                            </span>
                            
                            {/* LOGOUT BUTTON */}
                            <button
                                onClick={handleLogout} // Calls the function to clear session and redirect
                                className="bg-red-600 hover:bg-red-700 transition-colors duration-200 px-3 py-2 rounded font-semibold"
                            >
                                Logout
                            </button>
                        </>
                    ) : (
                        // Login Link (Visible when NOT authenticated)
                        <>
                            <Link 
                                to="/api/auth/login" 
                                className="hover:text-amber-200 transition-colors duration-200 px-3 py-2 rounded"
                            >
                                Login
                            </Link>
                            {/* REMOVED: The <Link> for /api/auth/register was here */}
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;