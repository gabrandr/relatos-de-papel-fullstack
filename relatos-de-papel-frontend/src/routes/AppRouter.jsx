import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainLayout from "../components/layout/MainLayout";
import ScrollToTop from "../components/layout/ScrollToTop";
import LandingPage from "../pages/LandingPage";
import HomePage from "../pages/HomePage";
import BookDetailPage from "../pages/BookDetailPage";
import CartPage from "../pages/CartPage";
import CheckoutPage from "../pages/CheckoutPage";
import OrderConfirmationPage from "../pages/OrderConfirmationPage";
import NotFoundPage from "../pages/NotFoundPage";
import {
  ProtectedCheckout,
  ProtectedOrderConfirmation,
} from "./ProtectedRoutes";

/**
 * Configuración de rutas de la aplicación.
 */
export const AppRouter = () => {
  return (
    <BrowserRouter>
      <ScrollToTop />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        {/*rutas con layout*/}
        <Route element={<MainLayout />}>
          <Route path="/home" element={<HomePage />} />
          <Route path="/book/:id" element={<BookDetailPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route
            path="/checkout"
            element={
              <ProtectedCheckout>
                <CheckoutPage />
              </ProtectedCheckout>
            }
          />
          <Route
            path="/order-confirmation"
            element={
              <ProtectedOrderConfirmation>
                <OrderConfirmationPage />
              </ProtectedOrderConfirmation>
            }
          />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};
