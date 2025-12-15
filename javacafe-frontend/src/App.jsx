// React: src/App.jsx (CORRECT MINIMAL ROUTING & CONTEXTS)

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
// 1. IMPORT AuthProvider
import { AuthProvider } from './pages/useAuth'; // <--- ASSUMES AuthContext.jsx is here
import { CartProvider } from './pages/CartContext'; 

import HomePage from './pages/HomePage';
import PlaceOrderPage from './pages/PlaceOrderPage';
import MenuPage from './pages/MenuPage';
// 3. IMPORT THE LOGIN PAGE (which handles both login and register)
import LoginPage from './pages/LoginPage';
// Import the ContactUsPage that was added previously
import ContactUsPage from './pages/ContactUsPage'; 


function App() {
    return (
        <Router>
            {/* 2. WRAP THE APPLICATION LOGIC WITH AuthProvider */}
            <AuthProvider>
                {/* CartProvider is wrapped inside AuthProvider. */}
                <CartProvider> 
                    <div className="min-h-screen bg-amber-50">
                        <Navbar />
                        
                        <Routes>
                            <Route path="/" element={<HomePage />} />
                            <Route path="/api" element={<HomePage />} />
                            
                            {/* Main application pages */}
                            <Route path="/api/menu" element={<MenuPage />} /> 
                            <Route path="/api/cart" element={<PlaceOrderPage />} /> 
                            
                            {/* Contact page route (previously added) */}
                            <Route path="/api/contact" element={<ContactUsPage />} />
                            
                            {/* 4. UPDATED AUTH ROUTES: 
                                We now need TWO separate <Route> definitions 
                                because the LoginPage component toggles 
                                between the full paths: /api/auth/login and /api/auth/register.
                            */}
                            {/* Route for the Login form */}
                            <Route path="/api/auth/login" element={<LoginPage />} />
                            
                            {/* Route for the Register form, using the SAME component */}
                            <Route path="/api/auth/register" element={<LoginPage />} />
                            
                        </Routes>
                    </div>
                </CartProvider>
            </AuthProvider>
        </Router>
    );
}

export default App;