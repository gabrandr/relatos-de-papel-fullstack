import { useEffect, useRef, useState } from "react";

/**
 * Hook para manejar un contador regresivo.
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
