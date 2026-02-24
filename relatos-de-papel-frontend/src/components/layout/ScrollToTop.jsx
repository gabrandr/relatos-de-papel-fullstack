import { useEffect } from "react";
import { useLocation } from "react-router-dom";

/**
 * Resetea el scroll al inicio cuando cambia la ruta.
 */
const ScrollToTop = () => {
  const { pathname } = useLocation(); //escucha cambios en la ruta

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]); //al cambiar la ruta, se ejecuta el useEffect

  return null;
};

export default ScrollToTop;
