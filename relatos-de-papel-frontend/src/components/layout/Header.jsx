import { useEffect, useRef, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import logo from "../../assets/logo.png";
import { useCartStore } from "../../store/useCartStore";
import { getBookSuggestions } from "../../api/booksApi";

/**
 * Encabezado principal con logo, buscador conectado a OpenSearch y acceso al carrito.
 *
 * @returns {JSX.Element} Header fijo con estado de búsqueda sincronizado a la URL.
 */
const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const cartCount = useCartStore((state) => state.getTotalItems());
  const [search, setSearch] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const wrapperRef = useRef(null);

  /**
   * Ejecuta búsqueda explícita al enviar el formulario.
   *
   * @param {React.FormEvent<HTMLFormElement>} e Evento submit del formulario.
   * @returns {void}
   */
  const handleSearch = (e) => {
    e.preventDefault();
    const value = search.trim();
    if (!value) {
      return;
    }
    navigate(`/home?search=${encodeURIComponent(value)}`);
    setShowSuggestions(false);
  };

  /**
   * Aplica una sugerencia seleccionada y navega al resultado.
   *
   * @param {string} value Texto de la sugerencia.
   * @returns {void}
   */
  const handleSuggestionSelect = (value) => {
    setSearch(value);
    setSuggestions([]);
    setShowSuggestions(false);
    navigate(`/home?search=${encodeURIComponent(value)}`);
  };

  useEffect(() => {
    // Mantiene input/sugerencias alineados con la URL actual.
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

    // Debounce para evitar disparar requests de suggest por cada pulsación.
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
    /**
     * Cierra el dropdown de sugerencias si el click ocurre fuera del buscador.
     *
     * @param {MouseEvent} event Evento de mouse global.
     * @returns {void}
     */
    const handleOutsideClick = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, []);

  /**
   * Actualiza el valor del input y oculta sugerencias al limpiar texto.
   *
   * @param {string} value Valor escrito por el usuario.
   * @returns {void}
   */
  const handleInputChange = (value) => {
    setSearch(value);
    if (!value.trim()) {
      setShowSuggestions(false);
    }
  };

  /**
   * Limpia la búsqueda y vuelve al catálogo base sin query params.
   *
   * @returns {void}
   */
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
