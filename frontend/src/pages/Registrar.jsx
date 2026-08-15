import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { extrairErro } from '../services/api';
import ErrorAlert from '../components/ErrorAlert';

export default function Registrar() {
  const { registrar } = useAuth();
  const navigate = useNavigate();
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    setCarregando(true);
    try {
      await registrar(nome, email, senha);
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
        <h1>Criar conta</h1>
        <ErrorAlert erro={erro} onFechar={() => setErro(null)} />

        <label>
          Nome
          <input type="text" value={nome} onChange={(e) => setNome(e.target.value)} required />
        </label>
        <label>
          E-mail
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Senha
          <input
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            minLength={8}
            required
          />
        </label>

        <button type="submit" disabled={carregando}>
          {carregando ? 'Registrando...' : 'Registrar'}
        </button>

        <p>
          Já tem conta? <Link to="/login">Entrar</Link>
        </p>
      </form>
    </div>
  );
}
