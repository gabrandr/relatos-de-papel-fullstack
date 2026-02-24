import { useState, useEffect } from "react";

/**
 * Hook para manejar un contador regresivo.
 */
const useCountdown = (initialValue, onComplete) => {
  const [count, setCount] = useState(initialValue);

  useEffect(() => {
    // Si llega a 0, ejecutar callback
    if (count === 0) {
      if (onComplete) onComplete();
      return;
    }
    // Cada segundo resta 1
    const interval = setInterval(() => {
      setCount((prev) => prev - 1);
    }, 1000);
    // Limpia intervalo al desmontar
    return () => clearInterval(interval);
  }, [count, onComplete]);

  return count;
};

export default useCountdown;
