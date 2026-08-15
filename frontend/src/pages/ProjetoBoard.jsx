import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api, { extrairErro } from '../services/api';
import { useAuth } from '../context/AuthContext';
import ErrorAlert from '../components/ErrorAlert';
import Navbar from '../components/Navbar';
import KanbanColumn from '../components/KanbanColumn';
import TarefaForm from '../components/TarefaForm';
import GerenciarMembros from '../components/GerenciarMembros';
import { STATUS_LABEL } from '../utils/labels';

const COLUNAS = Object.keys(STATUS_LABEL);

export default function ProjetoBoard() {
  const { id } = useParams();
  const { usuario } = useAuth();
  const [projeto, setProjeto] = useState(null);
  const [tarefas, setTarefas] = useState([]);
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      const [respostaProjeto, respostaTarefas] = await Promise.all([
        api.get(`/projetos/${id}`),
        api.get(`/projetos/${id}/tarefas`, { params: { size: 100 } }),
      ]);
      setProjeto(respostaProjeto.data);
      setTarefas(respostaTarefas.data.conteudo);
    } catch (err) {
      setErro(extrairErro(err));
    } finally {
      setCarregando(false);
    }
  }, [id]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  async function handleAlterarStatus(tarefa, novoStatus) {
    setErro(null);
    try {
      await api.put(`/tarefas/${tarefa.id}`, {
        titulo: tarefa.titulo,
        descricao: tarefa.descricao,
        status: novoStatus,
        prioridade: tarefa.prioridade,
        prazo: tarefa.prazo,
        responsavelId: tarefa.responsavel?.id ?? null,
      });
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
    }
  }

  async function handleExcluir(tarefaId) {
    setErro(null);
    try {
      await api.delete(`/tarefas/${tarefaId}`);
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
    }
  }

  async function handleCriarTarefa(dadosTarefa) {
    try {
      await api.post(`/projetos/${id}/tarefas`, dadosTarefa);
      setErro(null);
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
      throw err;
    }
  }

  async function handleAdicionarMembro(email) {
    try {
      await api.post(`/projetos/${id}/membros`, { email });
      setErro(null);
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
      throw err;
    }
  }

  async function handleRemoverMembro(usuarioId) {
    setErro(null);
    try {
      await api.delete(`/projetos/${id}/membros/${usuarioId}`);
      carregar();
    } catch (err) {
      setErro(extrairErro(err));
    }
  }

  if (carregando) {
    return <p className="carregando">Carregando...</p>;
  }

  const ehDono = usuario?.email === projeto?.dono?.email;

  return (
    <div>
      <Navbar />
      <main className="container">
        <Link to="/projetos" className="link-voltar">
          ← Projetos
        </Link>
        <h1>{projeto?.nome}</h1>
        <ErrorAlert erro={erro} onFechar={() => setErro(null)} />

        {ehDono && (
          <GerenciarMembros
            projeto={projeto}
            onAdicionar={handleAdicionarMembro}
            onRemover={handleRemoverMembro}
          />
        )}

        <TarefaForm membros={projeto?.membros ?? []} onCriar={handleCriarTarefa} />

        <div className="board">
          {COLUNAS.map((status) => (
            <KanbanColumn
              key={status}
              titulo={STATUS_LABEL[status]}
              tarefas={tarefas.filter((tarefa) => tarefa.status === status)}
              onAlterarStatus={handleAlterarStatus}
              onExcluir={handleExcluir}
            />
          ))}
        </div>
      </main>
    </div>
  );
}
