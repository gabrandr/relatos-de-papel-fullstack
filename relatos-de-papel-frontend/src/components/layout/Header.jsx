import { useEffect, useRef, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import logo from "../../assets/logo.png";
import { useCartStore } from "../../store/useCartStore";
import { getBookSuggestions } from "../../api/booksApi";

/**
 * Encabezado con logo, buscador y carrito.
 */
const Header = () => {
  const navigate = useNavigate(); //navegador
  const location = useLocation();
  const cartCount = useCartStore((state) => state.getTotalItems()); //cantidad total de items en carrito
  const [search, setSearch] = useState(""); //estado para el buscador
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const wrapperRef = useRef(null);

  //funcion para buscar
  const handleSearch = (e) => {
    e.preventDefault(); //prevenir el comportamiento por defecto del formulario
    const value = search.trim();
    if (!value) {
      return;
    }
    navigate(`/home?search=${encodeURIComponent(value)}`); //navegar a la pagina de home con el query param search
    setShowSuggestions(false);
  };

  const handleSuggestionSelect = (value) => {
    setSearch(value);
    setSuggestions([]);
    setShowSuggestions(false);
    navigate(`/home?search=${encodeURIComponent(value)}`);
  };

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const nextSearch = location.pathname === "/home" ? params.get("search") || "" : "";
    setSearch(nextSearch);
    setSuggestions([]);
    setShowSuggestions(false);
  }, [location.pathname, location.search]);

  useEffect(() => {
    if (!search.trim()) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }

    const timer = setTimeout(async () => {
      try {
        const values = await getBookSuggestions(search);
        setSuggestions(values);
        setShowSuggestions(values.length > 0);
      } catch {
        setSuggestions([]);
        setShowSuggestions(false);
      }
    }, 250);

    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, []);

  const handleInputChange = (value) => {
    setSearch(value);
    if (!value.trim()) {
      setShowSuggestions(false);
    }
  };

  const handleClearSearch = () => {
    setSearch("");
    setSuggestions([]);
    setShowSuggestions(false);
    navigate("/home");
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
        <div ref={wrapperRef} className="grow max-w-2xl relative">
          <form onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="Buscar por título"
              value={search}
              onChange={(e) => handleInputChange(e.target.value)}
              onFocus={() => setShowSuggestions(suggestions.length > 0)}
              className="w-full px-4 py-2 pr-10 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
            {search.trim() && (
              <button
                type="button"
                onClick={handleClearSearch}
                className="absolute right-2 top-1/2 -translate-y-1/2 w-7 h-7 rounded-full bg-primary text-white font-bold text-base leading-none flex items-center justify-center hover:bg-primary-dark hover:scale-105 focus:scale-105 focus:outline-none focus:ring-2 focus:ring-primary/40 transition-all duration-150 shadow-sm"
                aria-label="Limpiar búsqueda"
                title="Limpiar búsqueda"
              >
                ×
              </button>
            )}
          </form>

          {showSuggestions && (
            <div className="absolute top-12 left-0 right-0 bg-white border border-border rounded-lg shadow-md z-50">
              {suggestions.map((suggestion) => (
                <button
                  key={suggestion}
                  type="button"
                  onClick={() => handleSuggestionSelect(suggestion)}
                  className="w-full text-left px-4 py-2 hover:bg-background text-text-body"
                >
                  {suggestion}
                </button>
              ))}
            </div>
          )}
        </div>
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
