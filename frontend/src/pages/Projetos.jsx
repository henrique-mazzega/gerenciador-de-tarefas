import { useEffect, useState } from 'react';
import api, { extrairErro } from '../services/api';
import ErrorAlert from '../components/ErrorAlert';
import Navbar from '../components/Navbar';
import ProjetoCard from '../components/ProjetoCard';

export default function Projetos() {
  const [projetos, setProjetos] = useState([]);
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(true);

  async function carregar() {
    try {
      const { data } = await api.get('/projetos');
      setProjetos(data);
    } catch (err) {
      setErro(extrairErro(err));
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  async function handleCriar(e) {
    e.preventDefault();
    setErro(null);
    try {
      await api.post('/projetos', { nome, descricao: descricao || null });
      setNome('');
      setDescricao('');
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
    }
  }

  async function handleAtualizar(projetoId, dados) {
    try {
      await api.put(`/projetos/${projetoId}`, dados);
      setErro(null);
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
      throw err;
    }
  }

  async function handleExcluir(projetoId) {
    setErro(null);
    try {
      await api.delete(`/projetos/${projetoId}`);
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
    }
  }

  return (
    <div>
      <Navbar />
      <main className="container">
        <h1>Projetos</h1>
        <ErrorAlert erro={erro} onFechar={() => setErro(null)} />

        <form className="formulario formulario-inline" onSubmit={handleCriar}>
          <input
            placeholder="Nome do projeto"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            required
          />
          <input
            placeholder="Descrição"
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
          />
          <button type="submit">Criar projeto</button>
        </form>

        {carregando ? (
          <p>Carregando...</p>
        ) : (
          <ul className="lista-projetos">
            {projetos.map((projeto) => (
              <ProjetoCard
                key={projeto.id}
                projeto={projeto}
                onAtualizar={handleAtualizar}
                onExcluir={handleExcluir}
              />
            ))}
            {projetos.length === 0 && <p className="vazio">Nenhum projeto ainda</p>}
          </ul>
        )}
      </main>
    </div>
  );
}
