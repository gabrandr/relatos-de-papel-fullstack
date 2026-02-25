import { useEffect } from "react";
import { useLocation } from "react-router-dom";

/**
 * Controla la posición global de scroll en cambios de ruta.
 * Omite el reset cuando la navegación solicita restauración contextual.
 *
 * @returns {null} Componente de efecto sin render visual.
 */
const ScrollToTop = () => {
  const { key, pathname, search, state } = useLocation();

  useEffect(() => {
    // Respeta la restauración de scroll iniciada desde el flujo catálogo -> detalle -> catálogo.
    if (state?.restoreScroll) {
      return;
    }
    window.scrollTo({ top: 0, left: 0, behavior: "auto" });
  }, [key, pathname, search, state]);

  return null;
};

export default ScrollToTop;
