import { useState } from 'react';
import { PRIORIDADE_LABEL, STATUS_LABEL } from '../utils/labels';

const STATUS_OPCOES = Object.keys(STATUS_LABEL);
const PRIORIDADE_OPCOES = Object.keys(PRIORIDADE_LABEL);

export default function TarefaForm({ membros, onCriar }) {
  const [aberto, setAberto] = useState(false);
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [status, setStatus] = useState('TODO');
  const [prioridade, setPrioridade] = useState('MEDIUM');
  const [responsavelId, setResponsavelId] = useState('');

  function limpar() {
    setTitulo('');
    setDescricao('');
    setStatus('TODO');
    setPrioridade('MEDIUM');
    setResponsavelId('');
    setAberto(false);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      await onCriar({
        titulo,
        descricao: descricao || null,
        status,
        prioridade,
        prazo: null,
        responsavelId: responsavelId ? Number(responsavelId) : null,
      });
      limpar();
    } catch {
      // erro já exibido pelo componente pai
    }
  }

  if (!aberto) {
    return (
      <button type="button" className="botao-nova-tarefa" onClick={() => setAberto(true)}>
        + Nova tarefa
      </button>
    );
  }

  return (
    <form className="formulario formulario-tarefa" onSubmit={handleSubmit}>
      <input
        placeholder="Título"
        value={titulo}
        onChange={(e) => setTitulo(e.target.value)}
        required
      />
      <input placeholder="Descrição" value={descricao} onChange={(e) => setDescricao(e.target.value)} />

      <label>
        Status
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          {STATUS_OPCOES.map((opcao) => (
            <option key={opcao} value={opcao}>
              {STATUS_LABEL[opcao]}
            </option>
          ))}
        </select>
      </label>

      <label>
        Prioridade
        <select value={prioridade} onChange={(e) => setPrioridade(e.target.value)}>
          {PRIORIDADE_OPCOES.map((opcao) => (
            <option key={opcao} value={opcao}>
              {PRIORIDADE_LABEL[opcao]}
            </option>
          ))}
        </select>
      </label>

      <label>
        Responsável
        <select value={responsavelId} onChange={(e) => setResponsavelId(e.target.value)}>
          <option value="">Sem responsável</option>
          {membros.map((membro) => (
            <option key={membro.id} value={membro.id}>
              {membro.nome}
            </option>
          ))}
        </select>
      </label>

      <div className="formulario-acoes">
        <button type="submit">Criar</button>
        <button type="button" onClick={limpar}>
          Cancelar
        </button>
      </div>
    </form>
  );
}
