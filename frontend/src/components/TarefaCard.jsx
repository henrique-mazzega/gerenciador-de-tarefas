import { PRIORIDADE_LABEL, STATUS_LABEL } from '../utils/labels';

const STATUS_OPCOES = Object.keys(STATUS_LABEL);

export default function TarefaCard({ tarefa, onAlterarStatus, onExcluir }) {
  return (
    <article className={`card-tarefa prioridade-${tarefa.prioridade.toLowerCase()}`}>
      <h3>{tarefa.titulo}</h3>
      {tarefa.descricao && <p className="descricao">{tarefa.descricao}</p>}

      <div className="card-meta">
        <span className="badge">{PRIORIDADE_LABEL[tarefa.prioridade]}</span>
        <span className="responsavel">
          {tarefa.responsavel ? tarefa.responsavel.nome : 'Sem responsável'}
        </span>
      </div>

      <div className="card-acoes">
        <label>
          Status
          <select value={tarefa.status} onChange={(e) => onAlterarStatus(tarefa, e.target.value)}>
            {STATUS_OPCOES.map((opcao) => (
              <option key={opcao} value={opcao}>
                {STATUS_LABEL[opcao]}
              </option>
            ))}
          </select>
        </label>
        <button type="button" className="botao-excluir" onClick={() => onExcluir(tarefa.id)}>
          Excluir
        </button>
      </div>
    </article>
  );
}
