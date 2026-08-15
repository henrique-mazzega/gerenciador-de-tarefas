import { createContext, useCallback, useContext, useState } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [usuario, setUsuario] = useState(() => {
    const salvo = localStorage.getItem('usuario');
    return salvo ? JSON.parse(salvo) : null;
  });

  const autenticar = useCallback((novoToken, novoUsuario) => {
    localStorage.setItem('token', novoToken);
    localStorage.setItem('usuario', JSON.stringify(novoUsuario));
    setToken(novoToken);
    setUsuario(novoUsuario);
  }, []);

  const login = useCallback(
    async (email, senha) => {
      const { data } = await api.post('/auth/login', { email, senha });
      autenticar(data.token, { nome: data.nome, email: data.email });
    },
    [autenticar],
  );

  const registrar = useCallback(
    async (nome, email, senha) => {
      const { data } = await api.post('/auth/registrar', { nome, email, senha });
      autenticar(data.token, { nome: data.nome, email: data.email });
    },
    [autenticar],
  );

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    setToken(null);
    setUsuario(null);
  }, []);

  return (
    <AuthContext.Provider value={{ token, usuario, login, registrar, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}
