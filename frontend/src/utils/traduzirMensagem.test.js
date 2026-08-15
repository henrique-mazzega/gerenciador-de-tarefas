import { describe, expect, it } from 'vitest';
import { traduzirMensagem } from './traduzirMensagem';

describe('traduzirMensagem', () => {
  it('substitui múltiplas ocorrências de status e prioridade pelos rótulos em português', () => {
    const original =
      'Tarefa concluída (DONE) não pode retornar para TODO. A transição permitida é DONE → IN_PROGRESS';

    expect(traduzirMensagem(original)).toBe(
      'Tarefa concluída (Concluído) não pode retornar para A Fazer. A transição permitida é Concluído → Em Andamento',
    );
  });

  it('traduz tokens de prioridade', () => {
    expect(traduzirMensagem('Prioridade CRITICAL exige aprovação do dono')).toBe(
      'Prioridade Crítica exige aprovação do dono',
    );
  });

  it('mantém o texto intacto quando não há tokens conhecidos', () => {
    expect(traduzirMensagem('O responsável deve ser membro do projeto')).toBe(
      'O responsável deve ser membro do projeto',
    );
  });

  it('retorna o valor original quando vazio ou nulo', () => {
    expect(traduzirMensagem('')).toBe('');
    expect(traduzirMensagem(null)).toBe(null);
    expect(traduzirMensagem(undefined)).toBe(undefined);
  });
});
