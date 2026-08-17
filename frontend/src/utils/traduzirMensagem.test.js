import { describe, expect, it } from 'vitest';
import { traduzirMensagem } from './traduzirMensagem';

describe('traduzirMensagem', () => {
  it('substitui múltiplas ocorrências de status e prioridade pelos rótulos em português', () => {
    const original =
      'Uma tarefa concluída não pode voltar direto para TODO. Mova primeiro para IN_PROGRESS.';

    expect(traduzirMensagem(original)).toBe(
      'Uma tarefa concluída não pode voltar direto para A Fazer. Mova primeiro para Em Andamento.',
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
