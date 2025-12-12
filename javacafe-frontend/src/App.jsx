import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import ContactPage from './pages/ContactPage';

function App() {
    return (
        <div className="min-h-screen bg-amber-50">
            <Routes>
                <Route path="/" element={ <> <Navbar /> <HomePage /> </> } />
                <Route path="/contact" element={ <> <Navbar /> <ContactPage /> </> } />                
            </Routes>
        </div>
    );
}

export default App;