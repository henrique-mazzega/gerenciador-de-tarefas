import { describe, expect, it } from 'vitest';
import { render, screen } from '@testing-library/react';
import ErrorAlert from './ErrorAlert';

describe('ErrorAlert', () => {
  it('não renderiza nada quando não há erro', () => {
    const { container } = render(<ErrorAlert erro={null} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('exibe apenas o detail para um erro que não é de validação, sem o title', () => {
    render(
      <ErrorAlert
        erro={{ status: 409, title: 'Regra de negócio violada', detail: 'Limite atingido' }}
      />,
    );

    expect(screen.queryByText('Regra de negócio violada')).not.toBeInTheDocument();
    expect(screen.getByText('Limite atingido')).toBeInTheDocument();
  });

  it('lista os campos inválidos quando status é 400 e "erros" está presente', () => {
    render(
      <ErrorAlert
        erro={{
          status: 400,
          title: 'Dados inválidos',
          detail: 'Um ou mais campos possuem valores inválidos',
          erros: [
            { campo: 'email', mensagem: 'E-mail inválido' },
            { campo: 'titulo', mensagem: 'Título é obrigatório' },
          ],
        }}
      />,
    );

    expect(screen.getByText('E-mail inválido')).toBeInTheDocument();
    expect(screen.getByText('Título é obrigatório')).toBeInTheDocument();
  });

  it('não lista campos quando status é 400 mas não há "erros"', () => {
    render(
      <ErrorAlert erro={{ status: 400, title: 'Dados inválidos', detail: 'Campo inválido' }} />,
    );

    expect(document.querySelector('.alerta-erro-campos')).not.toBeInTheDocument();
  });

  it('não lista campos quando "erros" está presente mas o status não é 400', () => {
    render(
      <ErrorAlert
        erro={{
          status: 409,
          title: 'Conflito',
          detail: 'x',
          erros: [{ campo: 'email', mensagem: 'y' }],
        }}
      />,
    );

    expect(document.querySelector('.alerta-erro-campos')).not.toBeInTheDocument();
  });

  it('traduz os tokens de status e prioridade no detail antes de exibir', () => {
    render(
      <ErrorAlert
        erro={{
          status: 409,
          title: 'Regra de negócio violada',
          detail: 'Uma tarefa concluída não pode voltar direto para TODO. Mova primeiro para IN_PROGRESS.',
        }}
      />,
    );

    expect(
      screen.getByText('Uma tarefa concluída não pode voltar direto para A Fazer. Mova primeiro para Em Andamento.'),
    ).toBeInTheDocument();
  });
});
