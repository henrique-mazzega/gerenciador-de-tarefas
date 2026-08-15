import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <header className="navbar">
      <Link to="/projetos" className="marca">
        Gerenciador de Tarefas
      </Link>
      <div className="navbar-usuario">
        {usuario?.nome && <span>{usuario.nome}</span>}
        <button type="button" onClick={handleLogout}>
          Sair
        </button>
      </div>
    </header>
  );
}
