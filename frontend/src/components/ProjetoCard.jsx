import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProjetoCard({ projeto, onAtualizar, onExcluir }) {
  const { usuario } = useAuth();
  const navigate = useNavigate();
  const ehDono = usuario?.email === projeto.dono.email;

  const [editando, setEditando] = useState(false);
  const [nome, setNome] = useState(projeto.nome);
  const [descricao, setDescricao] = useState(projeto.descricao ?? '');

  function abrirProjeto() {
    navigate(`/projetos/${projeto.id}`);
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      abrirProjeto();
    }
  }

  async function handleSalvar(e) {
    e.preventDefault();
    try {
      await onAtualizar(projeto.id, { nome, descricao: descricao || null });
      setEditando(false);
    } catch {
      // erro já exibido pelo componente pai
    }
  }

  function handleEditarClick(e) {
    e.stopPropagation();
    setEditando(true);
  }

  function handleExcluirClick(e) {
    e.stopPropagation();
    const confirmado = window.confirm(
      `Excluir o projeto "${projeto.nome}"? As tarefas do projeto também serão removidas. Esta ação não pode ser desfeita.`,
    );
    if (confirmado) {
      onExcluir(projeto.id);
    }
  }

  if (editando) {
    return (
      <li>
        <form className="formulario formulario-edicao-projeto" onSubmit={handleSalvar}>
          <input value={nome} onChange={(e) => setNome(e.target.value)} required />
          <input
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
            placeholder="Descrição"
          />
          <div className="formulario-acoes">
            <button type="submit">Salvar</button>
            <button
              type="button"
              onClick={() => {
                setNome(projeto.nome);
                setDescricao(projeto.descricao ?? '');
                setEditando(false);
              }}
            >
              Cancelar
            </button>
          </div>
        </form>
      </li>
    );
  }

  return (
    <li
      className="card-projeto"
      role="button"
      tabIndex={0}
      onClick={abrirProjeto}
      onKeyDown={handleKeyDown}
    >
      <span className="card-projeto-titulo">{projeto.nome}</span>
      {projeto.descricao && <p>{projeto.descricao}</p>}
      {ehDono && (
        <div className="card-acoes-projeto">
          <button type="button" className="botao-secundario" onClick={handleEditarClick}>
            Editar
          </button>
          <button type="button" className="botao-excluir" onClick={handleExcluirClick}>
            Excluir
          </button>
        </div>
      )}
    </li>
  );
}
