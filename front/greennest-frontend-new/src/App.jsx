import { BrowserRouter, Routes, Route } from "react-router-dom";

import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import ProtectedRoute from "./components/ProtectedRoute";

import { AuthProvider } from "./context/AuthContext";
import { CartProvider } from "./context/CartContext";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Plants from "./pages/Plants";
import Cart from "./pages/Cart";
import Orders from "./pages/Orders";
import AdminPlants from "./pages/AdminPlants";
import AdminOrders from "./pages/AdminOrders";

function App() {

  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>

          <Navbar />

          <Routes>

            <Route path="/" element={<Home />} />

            <Route path="/login" element={<Login />} />

            <Route path="/register" element={<Register />} />

            <Route path="/plants" element={<Plants />} />

            <Route
              path="/cart"
              element={
                <ProtectedRoute>
                  <Cart />
                </ProtectedRoute>
              }
            />

            <Route
              path="/orders"
              element={
                <ProtectedRoute>
                  <Orders />
                </ProtectedRoute>
              }
            />

            <Route
              path="/admin/plants"
              element={
                <ProtectedRoute requireRole="ADMIN">
                  <AdminPlants />
                </ProtectedRoute>
              }
            />

            <Route
              path="/admin/orders"
              element={
                <ProtectedRoute requireRole="ADMIN">
                  <AdminOrders />
                </ProtectedRoute>
              }
            />

          </Routes>

          <Footer />

        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
