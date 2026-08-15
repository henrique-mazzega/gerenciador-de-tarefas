import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { extrairErro } from '../services/api';
import ErrorAlert from '../components/ErrorAlert';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    setCarregando(true);
    try {
      await login(email, senha);
      navigate('/projetos');
    } catch (err) {
      setErro(extrairErro(err));
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="pagina-auth">
      <form className="formulario" onSubmit={handleSubmit}>
        <h1>Entrar</h1>
        <ErrorAlert erro={erro} onFechar={() => setErro(null)} />

        <label>
          E-mail
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Senha
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required />
        </label>

        <button type="submit" disabled={carregando}>
          {carregando ? 'Entrando...' : 'Entrar'}
        </button>

        <p>
          Não tem conta? <Link to="/registrar">Registre-se</Link>
        </p>
      </form>
    </div>
  );
}
