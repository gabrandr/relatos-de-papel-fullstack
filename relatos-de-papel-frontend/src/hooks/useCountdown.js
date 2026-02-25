import { useEffect, useRef, useState } from "react";

/**
 * Hook para manejar un contador regresivo en segundos.
 * Garantiza que `onComplete` se ejecuta una sola vez por ciclo.
 *
 * @param {number} initialValue Valor inicial del contador.
 * @param {(() => void) | undefined} onComplete Callback opcional al llegar a cero.
 * @returns {number} Valor actual del contador.
 */
const useCountdown = (initialValue, onComplete) => {
  const [count, setCount] = useState(initialValue);
  const completedRef = useRef(false);

  useEffect(() => {
    setCount(initialValue);
    completedRef.current = false;
  }, [initialValue]);

  useEffect(() => {
    if (count === 0) {
      // Evita ejecuciones duplicadas de callback por re-render o efectos encadenados.
      if (!completedRef.current && onComplete) {
        completedRef.current = true;
        onComplete();
      }
      return;
    }

    const timer = setTimeout(() => {
      setCount((prev) => Math.max(prev - 1, 0));
    }, 1000);

    return () => clearTimeout(timer);
  }, [count, onComplete]);

  return count;
};

export default useCountdown;
