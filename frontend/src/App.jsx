import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Registrar from './pages/Registrar';
import Projetos from './pages/Projetos';
import ProjetoBoard from './pages/ProjetoBoard';
import './App.css';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/registrar" element={<Registrar />} />
          <Route
            path="/projetos"
            element={
              <ProtectedRoute>
                <Projetos />
              </ProtectedRoute>
            }
          />
          <Route
            path="/projetos/:id"
            element={
              <ProtectedRoute>
                <ProjetoBoard />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/projetos" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
