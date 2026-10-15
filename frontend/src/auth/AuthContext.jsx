import { useCallback, useMemo, useState } from 'react';
import { api } from '../api';
import { AuthContext } from './useAuth';

// Read the username and expiry from the JWT payload. The server still verifies
// the token on every request, so this is only used to decide what to show.
function readToken() {
  try {
    const token = localStorage.getItem('token');
    if (!token) return null;
    const b64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(decodeURIComponent(escape(atob(b64))));
    if (payload.exp * 1000 < Date.now()) {
      localStorage.removeItem('token');
      return null;
    }
    return payload.sub;
  } catch {
    localStorage.removeItem('token');
    return null;
  }
}

export function AuthProvider({ children }) {
  const [username, setUsername] = useState(readToken);

  const authenticate = useCallback(async (path, body) => {
    const { token } = await api.post(path, body);
    localStorage.setItem('token', token);
    setUsername(readToken());
  }, []);

  const value = useMemo(
    () => ({
      username,
      login: (username, password) => authenticate('/api/auth/login', { username, password }),
      register: (username, email, password) =>
        authenticate('/api/auth/register', { username, email, password }),
      logout: () => {
        localStorage.removeItem('token');
        setUsername(null);
      },
    }),
    [username, authenticate],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
