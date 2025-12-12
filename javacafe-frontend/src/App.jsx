import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import PlaceOrderPage from './pages/PlaceOrderPage';
import MenuPage from './pages/MenuPage';

function App() {
    return (
        <Router>
            <div className="min-h-screen bg-amber-50">
                <Navbar />
                
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/api" element={<HomePage />} />
                    <Route path="/api/menu" element={<MenuPage />} />
                    <Route path="/api/cart" element={<PlaceOrderPage />} />
                </Routes>

            </div>
        </Router>
    );
}

export default App;