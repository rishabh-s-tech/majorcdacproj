import { createContext, useCallback, useContext, useMemo, useState } from "react";

const AuthContext = createContext(null);

function readStoredUser() {
  try {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);

  const login = useCallback((loginResponse) => {
    const storedUser = {
      userId: loginResponse.userId,
      name: loginResponse.name,
      email: loginResponse.email,
      role: loginResponse.role
    };

    localStorage.setItem("token", loginResponse.token);
    localStorage.setItem("user", JSON.stringify(storedUser));
    setUser(storedUser);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  }, []);

  const value = useMemo(() => ({ user, login, logout, isAdmin: user?.role === "ADMIN" }), [user, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
}
