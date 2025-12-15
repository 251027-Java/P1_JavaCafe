import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import MenuPage from './pages/MenuPage';
import ContactPage from './pages/ContactPage';
import ProductDetailPage from './pages/ProductDetailPage';
import CartPage from './pages/CartPage';

function App() {
    return (
        <Router>
            <div className="min-h-screen bg-amber-50">
                <Navbar />
                
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/api" element={<HomePage />} />
                    <Route path="/api/menu" element={<MenuPage />} />
                    <Route path="/api/menu/product/:productId" element={<ProductDetailPage />} />
                    <Route path="/api/cart" element={<CartPage />} />
                    <Route path="/api/contact-us" element={<ContactPage />} />
                </Routes>

            </div>
        </Router>
    );
}

export default App;