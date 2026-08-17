import { traduzirMensagem } from '../utils/traduzirMensagem';

export default function ErrorAlert({ erro, onFechar }) {
  if (!erro) {
    return null;
  }

  const camposInvalidos = erro.status === 400 && Array.isArray(erro.erros) ? erro.erros : null;

  return (
    <div className="alerta-erro" role="alert">
      <div>
        <p>{traduzirMensagem(erro.detail) || 'Ocorreu um erro inesperado. Tente novamente.'}</p>
        {camposInvalidos && camposInvalidos.length > 0 && (
          <ul className="alerta-erro-campos">
            {camposInvalidos.map((campoErro) => (
              <li key={campoErro.campo}>
                <strong>{campoErro.campo}:</strong> {campoErro.mensagem}
              </li>
            ))}
          </ul>
        )}
      </div>
      {onFechar && (
        <button type="button" className="alerta-fechar" onClick={onFechar} aria-label="Fechar alerta">
          ×
        </button>
      )}
    </div>
  );
}
