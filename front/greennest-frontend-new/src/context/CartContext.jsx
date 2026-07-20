import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import API from "../api/axiosConfig";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [count, setCount] = useState(0);

  const refreshCount = useCallback(() => {
    if (!user) {
      return;
    }

    API.get("/cart")
      .then((response) => {
        const total = response.data.reduce((sum, item) => sum + (item.quantity || 0), 0);
        setCount(total);
      })
      .catch(() => {
        setCount(0);
      });
  }, [user]);

  // Refetch the count whenever the logged-in user changes (login/logout).
  // The cart link (and its badge) is hidden entirely for logged-out users,
  // so there's no need to synchronously reset the count here.
  useEffect(() => {
    if (!user) {
      return;
    }

    let ignore = false;

    API.get("/cart")
      .then((response) => {
        if (!ignore) {
          const total = response.data.reduce((sum, item) => sum + (item.quantity || 0), 0);
          setCount(total);
        }
      })
      .catch(() => {
        if (!ignore) {
          setCount(0);
        }
      });

    return () => {
      ignore = true;
    };
  }, [user]);

  const value = useMemo(() => ({ count, refreshCount }), [count, refreshCount]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);

  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }

  return context;
}
