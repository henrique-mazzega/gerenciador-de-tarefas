import { describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen } from '@testing-library/react';
import TarefaCard from './TarefaCard';

const tarefa = {
  id: 1,
  titulo: 'Configurar CI',
  descricao: 'Pipeline de build e testes',
  status: 'TODO',
  prioridade: 'HIGH',
  responsavel: { id: 2, nome: 'Maria Souza', email: 'maria@teste.com' },
};

describe('TarefaCard', () => {
  it('exibe título, prioridade e responsável da tarefa', () => {
    render(<TarefaCard tarefa={tarefa} onAlterarStatus={() => {}} onExcluir={() => {}} />);

    expect(screen.getByText('Configurar CI')).toBeInTheDocument();
    expect(screen.getByText('Alta')).toBeInTheDocument();
    expect(screen.getByText('Maria Souza')).toBeInTheDocument();
  });

  it('exibe "Sem responsável" quando a tarefa não tem responsável', () => {
    render(
      <TarefaCard
        tarefa={{ ...tarefa, responsavel: null }}
        onAlterarStatus={() => {}}
        onExcluir={() => {}}
      />,
    );

    expect(screen.getByText('Sem responsável')).toBeInTheDocument();
  });

  it('chama onAlterarStatus com a tarefa e o novo status ao mudar o select', () => {
    const onAlterarStatus = vi.fn();
    render(<TarefaCard tarefa={tarefa} onAlterarStatus={onAlterarStatus} onExcluir={() => {}} />);

    fireEvent.change(screen.getByLabelText('Status'), { target: { value: 'DONE' } });

    expect(onAlterarStatus).toHaveBeenCalledWith(tarefa, 'DONE');
  });

  it('chama onExcluir com o id da tarefa ao clicar em Excluir', () => {
    const onExcluir = vi.fn();
    render(<TarefaCard tarefa={tarefa} onAlterarStatus={() => {}} onExcluir={onExcluir} />);

    fireEvent.click(screen.getByRole('button', { name: 'Excluir' }));

    expect(onExcluir).toHaveBeenCalledWith(1);
  });
});
