import TarefaCard from './TarefaCard';

export default function KanbanColumn({ titulo, tarefas, onAlterarStatus, onExcluir }) {
  return (
    <section className="coluna" aria-label={titulo}>
      <h2>
        {titulo} <span className="contador">{tarefas.length}</span>
      </h2>
      <div className="coluna-cards">
        {tarefas.map((tarefa) => (
          <TarefaCard key={tarefa.id} tarefa={tarefa} onAlterarStatus={onAlterarStatus} onExcluir={onExcluir} />
        ))}
        {tarefas.length === 0 && <p className="vazio">Nenhuma tarefa</p>}
      </div>
    </section>
  );
}
