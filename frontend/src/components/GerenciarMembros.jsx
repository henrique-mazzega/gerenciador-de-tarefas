import { useState } from 'react';

export default function GerenciarMembros({ projeto, onAdicionar, onRemover }) {
  const [email, setEmail] = useState('');

  async function handleAdicionar(e) {
    e.preventDefault();
    try {
      await onAdicionar(email);
      setEmail('');
    } catch {
      // erro já exibido pelo componente pai
    }
  }

  return (
    <section className="painel-membros">
      <h2>Membros</h2>
      <ul className="lista-membros">
        {projeto.membros.map((membro) => (
          <li key={membro.id}>
            <span>
              {membro.nome} <small>({membro.email})</small>
            </span>
            {membro.id !== projeto.dono.id && (
              <button type="button" className="botao-excluir" onClick={() => onRemover(membro.id)}>
                Remover
              </button>
            )}
          </li>
        ))}
      </ul>

      <form className="formulario formulario-inline" onSubmit={handleAdicionar}>
        <input
          type="email"
          placeholder="Email do usuário"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <button type="submit">Adicionar membro</button>
      </form>
    </section>
  );
}
