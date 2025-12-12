import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import MenuPage from './pages/MenuPage';
import { Routes, Route } from 'react-router-dom';

function App() {
    return (
        <div className="min-h-screen bg-amber-50">
            <Navbar />
            <Routes>
                <Route path="/api" element={<HomePage />} />
                <Route path="/api/menu" element={<MenuPage />} />
            </Routes>
        </div>
    );
}

export default App;