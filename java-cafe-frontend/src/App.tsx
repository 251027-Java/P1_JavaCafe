// src/App.tsx

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar'; // Import the Navbar component
import LoginPage from './pages/LoginPage';
import MenuPage from './pages/MenuPage'; 
// AdminDashboard and ProtectedRoute imports are now removed.

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        
        {/* ====================================================== */}
        {/* CRITICAL: PLACE THE NAVBAR HERE, OUTSIDE <Routes>      */}
        {/* This ensures the Navbar renders on every single page.  */}
        {/* ====================================================== */}
        <Navbar />
        
        {/* All route-specific page content goes inside <Routes> */}
        <Routes>
          
          {/* Public Routes */}
          {/* Mapped the root path and /menu path to the MenuPage */}
          <Route path="/" element={<MenuPage />} /> 
          <Route path="/menu" element={<MenuPage />} />
          <Route path="/login" element={<LoginPage />} />

          {/* ====================================================== */}
          {/* PROTECTED ROUTES SECTION HAS BEEN REMOVED FOR NOW      */}
          {/* ====================================================== */}
          
          {/* Catch-all 404 Route */}
          <Route path="*" element={
             <div className="p-10 text-center text-xl text-red-600">404: Not Found</div>
          } />
        </Routes>
      </div>
    </Router>
  );
}

export default App;