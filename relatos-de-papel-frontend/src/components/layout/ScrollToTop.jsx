import { useEffect } from "react";
import { useLocation } from "react-router-dom";

/**
 * Controla el scroll al navegar entre rutas.
 */
const ScrollToTop = () => {
  const { key, pathname, search, state } = useLocation();

  useEffect(() => {
    if (state?.restoreScroll) {
      return;
    }
    window.scrollTo({ top: 0, left: 0, behavior: "auto" });
  }, [key, pathname, search, state]);

  return null;
};

export default ScrollToTop;
