// React: src/App.jsx (CORRECT MINIMAL ROUTING)

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import PlaceOrderPage from './pages/PlaceOrderPage';
import MenuPage from './pages/MenuPage'

function App() {
    return (
        <Router>
            <div className="min-h-screen bg-amber-50">
                <Navbar />
                
                <Routes>
                    
                    {/* FIX 1: Add the root path to show the HomePage on initial load. */}
                    <Route path="/" element={<HomePage />} />
                    
                    {/* FIX 2: Add the '/api' path so the Navbar link works. */}
                    <Route path="/api" element={<HomePage />} />

                    {<Route path="/api/menu" element={<MenuPage />} />}
                    
                    {/* FIX 3: Keep the Cart route, and now it will successfully replace the HomePage. */}
                   { <Route path="/api/cart" element={<PlaceOrderPage />} />}
                    
                </Routes>

            </div>
        </Router>
    );
}

export default App;