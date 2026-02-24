import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import logo from "../../assets/logo.png";
import { useCartStore } from "../../store/useCartStore";

/**
 * Encabezado con logo, buscador y carrito.
 */
const Header = () => {
  const navigate = useNavigate(); //navegador
  const cartCount = useCartStore((state) => state.getTotalItems()); //cantidad total de items en carrito
  const [search, setSearch] = useState(""); //estado para el buscador

  //funcion para buscar
  const handleSearch = (e) => {
    e.preventDefault(); //prevenir el comportamiento por defecto del formulario
    navigate(`/home?search=${encodeURIComponent(search)}`); //navegar a la pagina de home con el query param search
    setSearch(""); //limpiar el buscador
  };

  return (
    <header className="bg-white border-b border-border sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between gap-4">
        {/* logo */}
        <Link to="/home" className="flex items-center gap-3 shrink-0">
          <img src={logo} alt="Logo" className="h-10" />
          <span className="text-xl font-bold text-primary-dark hidden md:block">
            Relatos de Papel
          </span>
        </Link>
        {/* buscador */}
        <form onSubmit={handleSearch} className="grow max-w-2xl">
          <input
            type="text"
            placeholder="Buscar por título"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full px-4 py-2 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </form>
        {/* carrito */}
        <Link to="/cart" className="relative">
          <span className="text-2xl">🛒</span>
          {cartCount > 0 && (
            <span className="absolute -top-2 -right-2 bg-primary text-white text-xs w-5 h-5 rounded-full flex items-center justify-center">
              {cartCount}
            </span>
          )}
        </Link>
      </div>
    </header>
  );
};
export default Header;
