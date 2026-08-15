import { PRIORIDADE_LABEL, STATUS_LABEL } from './labels';

const LABELS = { ...STATUS_LABEL, ...PRIORIDADE_LABEL };
const CHAVES_POR_TAMANHO = Object.keys(LABELS).sort((a, b) => b.length - a.length);

export function traduzirMensagem(texto) {
  if (!texto) {
    return texto;
  }
  return CHAVES_POR_TAMANHO.reduce((acc, chave) => acc.replaceAll(chave, LABELS[chave]), texto);
}
